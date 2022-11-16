package com.andresuryana.metembangbali.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Patterns
import android.webkit.MimeTypeMap
import com.andresuryana.metembangbali.helper.Helpers.generateFilename
import org.apache.commons.io.FileUtils
import java.io.File
import kotlin.time.DurationUnit
import kotlin.time.toDuration


object Ext {

    fun String.isEmail(): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun String.spaceCamelCase(): String {
        val charArray = this.toCharArray()
        val resultCharArray = mutableListOf<Char>()

        for (i in charArray.indices) {
            if (charArray[i] in 'A'..'Z') {
                if (i != 0) {
                    resultCharArray.add(' ')
                }
                resultCharArray.add(charArray[i])
            } else {
                resultCharArray.add(charArray[i])
            }
        }

        return resultCharArray.joinToString(separator = "")
    }

    fun String.toHttpsUrl(): String =
        if (contains("http:"))
            replace("http:", "https:")
        else this

    fun Long.toMusicTimeline(): String {
        // Convert to duration unit
        val duration = toDuration(DurationUnit.MILLISECONDS)

        return duration.toComponents { minutes, seconds, _ ->
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun Uri.getName(resolver: ContentResolver): String {
        val returnCursor: Cursor = resolver.query(this, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()

        return name
    }

    fun Uri.toAudioFile(context: Context): File? {
        return try {
            // File path
            val mediaDir = File(context.filesDir, "Metembang Bali")
            val extension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(context.contentResolver?.getType(this))
            val filename = generateFilename("AUDIO", extension ?: "wav")
            val filePath = mediaDir.absolutePath + filename

            val inputStream = context.contentResolver.openInputStream(this)
            val file = File(filePath)
            FileUtils.copyInputStreamToFile(inputStream, file)
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun String.toIntegerArray(delimiter: Char): ArrayList<Int>? {
        return try {
            if (this.isEmpty()) return null
            val arr = ArrayList<Int>()
            val noSpaceStr = StringBuilder(this.replace("\\s".toRegex(), ""))
            if (noSpaceStr.last() == delimiter) {
                noSpaceStr.deleteCharAt(noSpaceStr.lastIndex)
            }
            noSpaceStr.split(delimiter).forEach {
                val currentValue = it.toIntOrNull()
                if (currentValue != null) arr.add(currentValue)
            }
            arr
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun String.toCharArray(delimiter: Char): ArrayList<Char>? {
        return try {
            if (this.isEmpty()) return null
            val arr = ArrayList<Char>()
            val noSpaceStr = StringBuilder(this.replace("\\s".toRegex(), ""))
            if (noSpaceStr.last() == delimiter) {
                noSpaceStr.deleteCharAt(noSpaceStr.lastIndex)
            }
            noSpaceStr.split(delimiter).forEach {
                val currentValue = it.firstOrNull()
                if (currentValue != null && currentValue.isLetter()) arr.add(currentValue)
            }
            arr
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun String.capitalizeEachWord(): String? {
        return if (this.isEmpty()) null
        else this.trim().split("\\s+".toRegex()).map { str ->
            str.replaceFirstChar { it.uppercase() }
        }.joinToString(" ")
    }
}