package com.example.gymapp.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/** Meal photos live in app-private storage and are not part of JSON backups. */
class MealPhotoStore(context: Context) {
    private val dir = File(context.filesDir, "meal_photos")

    suspend fun save(jpeg: ByteArray): String = withContext(Dispatchers.IO) {
        dir.mkdirs()
        val name = "${UUID.randomUUID()}.jpg"
        File(dir, name).writeBytes(jpeg)
        name
    }

    fun file(name: String): File? = File(dir, name).takeIf { it.isFile && it.parentFile == dir }

    /** Not suspending so it can run while a cancelled analysis unwinds. */
    fun delete(name: String?) {
        if (name != null) file(name)?.delete()
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        dir.listFiles()?.forEach { it.delete() }
    }
}
