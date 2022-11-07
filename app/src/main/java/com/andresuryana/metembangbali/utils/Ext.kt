package com.andresuryana.metembangbali.utils

import android.util.Patterns
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
}