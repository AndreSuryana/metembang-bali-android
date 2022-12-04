package com.andresuryana.metembangbali.utils

import android.util.Patterns
import java.util.*
import kotlin.collections.ArrayList
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

    fun Long.toMusicTimeline(): String {
        // Convert to duration unit
        val duration = toDuration(DurationUnit.MILLISECONDS)

        return duration.toComponents { minutes, seconds, _ ->
            String.format("%02d:%02d", minutes, seconds)
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
        else this.trim().split("\\s+".toRegex()).joinToString(" ") { str ->
            str.replaceFirstChar { it.uppercase() }
        }
    }

    fun Date?.toTimeInMillis(): Long {
        return try {
            this?.time ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
}