package com.example.gymapp.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.gymapp.MainActivity
import com.example.gymapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Keeps the rest countdown alive with the screen off. A partial wakelock
 * covers a short rest so the process is still awake to vibrate at zero,
 * and the ongoing notification chronometer keeps counting on the lock screen.
 */
class RestTimerService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var waitJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var endElapsed = 0L
    private var completed = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            when (intent?.action) {
                RestTimerController.ACTION_STOP -> skip()
                RestTimerController.ACTION_ADD_30 -> addThirty()
                RestTimerController.ACTION_START -> startFrom(intent)
                else -> restoreOrStop()
            }
        } catch (_: Exception) {
            val saved = RestTimerStore.read(this)
            if (saved != null && saved.endElapsedRealtime > SystemClock.elapsedRealtime()) {
                RestTimerController.armInProcess(this, saved)
            }
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        waitJob?.cancel()
        scope.cancel()
        releaseLock()
        super.onDestroy()
    }

    private fun startFrom(intent: Intent) {
        val end = intent.getLongExtra(RestTimerController.EXTRA_END, 0L)
        val duration = intent.getLongExtra(RestTimerController.EXTRA_DURATION, 0L)
        val sessionId = intent.getLongExtra(RestTimerController.EXTRA_SESSION, -1L)
        val snapshot = if (end > SystemClock.elapsedRealtime() && duration > 0L) {
            RestSnapshot(end, duration, sessionId)
        } else {
            RestTimerStore.read(this)
        }
        if (snapshot == null || snapshot.endElapsedRealtime <= SystemClock.elapsedRealtime()) {
            restoreOrStop()
        } else {
            begin(snapshot)
        }
    }

    private fun restoreOrStop() {
        val saved = RestTimerStore.read(this)
        if (saved != null && saved.endElapsedRealtime > SystemClock.elapsedRealtime()) {
            begin(saved)
        } else {
            placeholderThenStop()
        }
    }

    private fun begin(snapshot: RestSnapshot) {
        completed = false
        endElapsed = snapshot.endElapsedRealtime
        RestTimerStore.save(this, snapshot)
        RestTimerBus.publish(RestPhase.Running(snapshot))
        acquireLock(snapshot.endElapsedRealtime - SystemClock.elapsedRealtime())
        enterForeground(ongoingNotification(snapshot))
        arm(snapshot.endElapsedRealtime)
    }

    private fun addThirty() {
        val saved = RestTimerStore.read(this)
        val now = SystemClock.elapsedRealtime()
        if (saved == null || saved.endElapsedRealtime <= now) {
            restoreOrStop()
            return
        }
        val maxEnd = now + MAX_REST_MS
        val newEnd = (saved.endElapsedRealtime + 30_000L).coerceAtMost(maxEnd)
        val added = newEnd - saved.endElapsedRealtime
        if (added <= 0L) {
            begin(saved)
            return
        }
        begin(
            saved.copy(
                endElapsedRealtime = newEnd,
                durationMs = saved.durationMs + added,
            ),
        )
    }

    private fun skip() {
        completed = true
        waitJob?.cancel()
        RestTimerStore.clear(this)
        RestTimerBus.publish(RestPhase.Idle)
        releaseLock()
        enterForeground(idleNotification())
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun arm(end: Long) {
        waitJob?.cancel()
        waitJob = scope.launch {
            val waitMs = end - SystemClock.elapsedRealtime()
            if (waitMs > 0) delay(waitMs)
            if (!isActive) return@launch
            if (endElapsed == end) finishNaturally()
        }
    }

    private fun finishNaturally() {
        if (completed) return
        completed = true
        waitJob?.cancel()
        RestTimerStore.clear(this)
        releaseLock()
        RestTimerBus.publish(RestPhase.Finished)
        RestTimerController.vibrate(this)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(DONE_ID, doneNotification())
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        RestTimerController.finishLater()
        stopSelf()
    }

    private fun placeholderThenStop() {
        enterForeground(idleNotification())
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun enterForeground(notification: android.app.Notification) {
        val types = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            intArrayOf(
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            intArrayOf(0)
        }
        var lastError: Exception? = null
        for (type in types) {
            try {
                ServiceCompat.startForeground(this, NOTIF_ID, notification, type)
                return
            } catch (error: Exception) {
                lastError = error
            }
        }
        throw lastError ?: IllegalStateException("Could not start the rest timer")
    }

    private fun ongoingNotification(snapshot: RestSnapshot) = NotificationCompat.Builder(this, RestTimerController.CHANNEL_REST)
        .setSmallIcon(R.drawable.ic_stat_timer)
        .setContentTitle("Nghỉ giữa set")
        .setContentText("Chạm để mở buổi tập")
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setSilent(true)
        .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setUsesChronometer(true)
        .setChronometerCountDown(true)
        .setWhen(wallClockEnd(snapshot.endElapsedRealtime))
        .setContentIntent(openAppIntent())
        .addAction(0, "+30 giây", serviceAction(RestTimerController.ACTION_ADD_30, 2))
        .addAction(0, "Bỏ qua", serviceAction(RestTimerController.ACTION_STOP, 3))
        .build()

    private fun doneNotification() = NotificationCompat.Builder(this, RestTimerController.CHANNEL_DONE)
        .setSmallIcon(R.drawable.ic_stat_timer)
        .setContentTitle("Hết giờ nghỉ")
        .setContentText("Vào set tiếp theo")
        .setAutoCancel(true)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(openAppIntent())
        .setTimeoutAfter(60_000)
        .build()

    private fun idleNotification() = NotificationCompat.Builder(this, RestTimerController.CHANNEL_REST)
        .setSmallIcon(R.drawable.ic_stat_timer)
        .setContentTitle("Nghỉ giữa set")
        .setSilent(true)
        .build()

    private fun wallClockEnd(endElapsedRealtime: Long): Long {
        return System.currentTimeMillis() + (endElapsedRealtime - SystemClock.elapsedRealtime())
    }

    private fun openAppIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        return PendingIntent.getActivity(
            this,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun serviceAction(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(this, RestTimerService::class.java).setAction(action)
        return PendingIntent.getForegroundService(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun acquireLock(holdMs: Long) {
        val manager = getSystemService(POWER_SERVICE) as PowerManager
        val lock = wakeLock ?: manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "gymapp:rest-timer").also {
            it.setReferenceCounted(false)
            wakeLock = it
        }
        if (lock.isHeld) lock.release()
        lock.acquire(holdMs.coerceIn(5_000L, MAX_REST_MS) + 5_000L)
    }

    private fun releaseLock() {
        val lock = wakeLock ?: return
        if (lock.isHeld) lock.release()
    }

    companion object {
        private const val NOTIF_ID = 42
        private const val DONE_ID = 43
        private const val MAX_REST_MS = 15 * 60 * 1000L
    }
}
