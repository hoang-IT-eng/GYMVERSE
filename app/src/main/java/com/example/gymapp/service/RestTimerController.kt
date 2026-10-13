package com.example.gymapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.ContextCompat
import com.example.gymapp.GymApp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RestSnapshot(
    val endElapsedRealtime: Long,
    val durationMs: Long,
    val sessionId: Long,
)

sealed interface RestPhase {
    data object Idle : RestPhase
    data class Running(val snapshot: RestSnapshot) : RestPhase
    data object Finished : RestPhase
}

object RestTimerBus {
    private val _phase = MutableStateFlow<RestPhase>(RestPhase.Idle)
    val phase: StateFlow<RestPhase> = _phase

    fun publish(phase: RestPhase) {
        _phase.value = phase
    }
}

object RestTimerStore {
    private const val PREFS = "rest_timer"
    private const val END = "end_elapsed"
    private const val DURATION = "duration"
    private const val SESSION = "session_id"
    private const val REST_SECONDS = "rest_seconds"

    fun save(context: Context, snapshot: RestSnapshot) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putLong(END, snapshot.endElapsedRealtime)
            .putLong(DURATION, snapshot.durationMs)
            .putLong(SESSION, snapshot.sessionId)
            .commit()
    }

    fun read(context: Context): RestSnapshot? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val end = prefs.getLong(END, 0L)
        val duration = prefs.getLong(DURATION, 0L)
        if (end <= 0L || duration <= 0L) return null
        return RestSnapshot(
            endElapsedRealtime = end,
            durationMs = duration,
            sessionId = prefs.getLong(SESSION, -1L),
        )
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .remove(END)
            .remove(DURATION)
            .remove(SESSION)
            .commit()
    }

    fun saveRestSeconds(context: Context, seconds: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putInt(REST_SECONDS, seconds)
            .apply()
    }

    fun readRestSeconds(context: Context): Int {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(REST_SECONDS, 90)
            .coerceIn(15, 15 * 60)
    }
}

object RestTimerController {
    const val ACTION_START = "com.example.gymapp.rest.START"
    const val ACTION_ADD_30 = "com.example.gymapp.rest.ADD_30"
    const val ACTION_STOP = "com.example.gymapp.rest.STOP"
    const val ACTION_RESTORE = "com.example.gymapp.rest.RESTORE"
    const val EXTRA_END = "end"
    const val EXTRA_DURATION = "duration"
    const val EXTRA_SESSION = "session"
    const val CHANNEL_REST = "rest_timer"
    const val CHANNEL_DONE = "rest_done"

    private var fallbackJob: Job? = null

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        val rest = NotificationChannel(
            CHANNEL_REST,
            "Nghỉ giữa set",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Đếm ngược thời gian nghỉ khi màn hình tắt"
            setShowBadge(false)
            setSound(null, null)
            enableVibration(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        val done = NotificationChannel(
            CHANNEL_DONE,
            "Hết giờ nghỉ",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Báo khi đến lúc vào set tiếp"
            setSound(null, null)
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 280, 120, 280)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        manager.createNotificationChannel(rest)
        manager.createNotificationChannel(done)
    }

    fun restorePhase(context: Context) {
        val saved = RestTimerStore.read(context)
        if (saved != null && saved.endElapsedRealtime > SystemClock.elapsedRealtime()) {
            RestTimerBus.publish(RestPhase.Running(saved))
        } else if (saved != null) {
            RestTimerStore.clear(context)
        }
    }

    fun ensureService(context: Context) {
        if (RestTimerBus.phase.value !is RestPhase.Running) return
        startService(context, Intent(context, RestTimerService::class.java).setAction(ACTION_RESTORE))
    }

    fun start(context: Context, seconds: Int, sessionId: Long) {
        ensureChannels(context)
        val durationMs = seconds.coerceIn(15, 15 * 60) * 1000L
        val snapshot = RestSnapshot(
            endElapsedRealtime = SystemClock.elapsedRealtime() + durationMs,
            durationMs = durationMs,
            sessionId = sessionId,
        )
        RestTimerStore.save(context, snapshot)
        RestTimerBus.publish(RestPhase.Running(snapshot))
        val intent = Intent(context, RestTimerService::class.java).apply {
            action = ACTION_START
            putExtra(EXTRA_END, snapshot.endElapsedRealtime)
            putExtra(EXTRA_DURATION, snapshot.durationMs)
            putExtra(EXTRA_SESSION, snapshot.sessionId)
        }
        if (!startService(context, intent)) {
            armInProcess(context, snapshot)
        }
    }

    fun addThirty(context: Context) {
        startService(
            context,
            Intent(context, RestTimerService::class.java).setAction(ACTION_ADD_30),
        )
    }

    fun skip(context: Context) {
        fallbackJob?.cancel()
        RestTimerStore.clear(context)
        RestTimerBus.publish(RestPhase.Idle)
        startService(
            context,
            Intent(context, RestTimerService::class.java).setAction(ACTION_STOP),
        )
    }

    fun vibrate(context: Context) {
        val effect = VibrationEffect.createWaveform(longArrayOf(0, 280, 120, 280), -1)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(VibratorManager::class.java)
            manager.defaultVibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(effect)
        }
    }

    fun finishLater() {
        GymApp.applicationScope.launch {
            delay(2_500)
            if (RestTimerBus.phase.value is RestPhase.Finished) {
                RestTimerBus.publish(RestPhase.Idle)
            }
        }
    }

    fun armInProcess(context: Context, snapshot: RestSnapshot) {
        fallbackJob?.cancel()
        fallbackJob = GymApp.applicationScope.launch {
            val wait = snapshot.endElapsedRealtime - SystemClock.elapsedRealtime()
            if (wait > 0) delay(wait)
            val current = RestTimerBus.phase.value
            val stillThisTimer = current is RestPhase.Running &&
                current.snapshot.endElapsedRealtime == snapshot.endElapsedRealtime
            if (!stillThisTimer) return@launch
            RestTimerStore.clear(context)
            RestTimerBus.publish(RestPhase.Finished)
            vibrate(context)
            delay(2_500)
            if (RestTimerBus.phase.value is RestPhase.Finished) {
                RestTimerBus.publish(RestPhase.Idle)
            }
        }
    }

    private fun startService(context: Context, intent: Intent): Boolean {
        return try {
            ContextCompat.startForegroundService(context, intent)
            true
        } catch (_: Exception) {
            false
        }
    }
}
