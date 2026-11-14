package com.example.gymapp.ui.profile

import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.GymApp
import com.example.gymapp.data.GymBackup
import com.example.gymapp.data.GymRepository
import com.example.gymapp.service.RestTimerController
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class ProfileViewModel(private val app: GymApp) : AndroidViewModel(app) {
    private val repository: GymRepository = app.repository
    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val messages: SharedFlow<String> = _messages

    fun exportCsv(uri: Uri) = runJob("Không xuất được CSV") {
        val data = repository.exportBackup()
        writeText(uri, GymBackup.toCsv(data))
        "Đã xuất ${data.sets.size} hiệp ra CSV"
    }

    fun exportBackup(uri: Uri) = runJob("Không tạo được bản sao lưu") {
        val data = repository.exportBackup()
        writeText(uri, GymBackup.toJson(data))
        "Đã sao lưu ${data.sessions.size} buổi tập"
    }

    fun importBackup(uri: Uri) = runJob("Không khôi phục được") {
        val text = withContext(Dispatchers.IO) {
            app.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
                ?: throw IOException("Không mở được file")
        }
        val data = GymBackup.fromJson(text)
        repository.restoreBackup(data)
        app.mealPhotos.clear()
        RestTimerController.skip(app)
        "Đã khôi phục ${data.sessions.size} buổi tập"
    }

    private fun runJob(failure: String, block: suspend () -> String) {
        if (_busy.value) return
        _busy.value = true
        viewModelScope.launch {
            val message = try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                "$failure: ${e.message ?: e.javaClass.simpleName}"
            } finally {
                _busy.value = false
            }
            _messages.emit(message)
        }
    }

    private suspend fun writeText(uri: Uri, text: String) = withContext(Dispatchers.IO) {
        val stream = app.contentResolver.openOutputStream(uri, "wt") ?: throw IOException("Không mở được file")
        stream.use { it.write(text.toByteArray(Charsets.UTF_8)) }
    }
}
