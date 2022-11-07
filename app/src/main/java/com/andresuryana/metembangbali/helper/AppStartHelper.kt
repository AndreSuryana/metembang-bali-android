package com.andresuryana.metembangbali.helper

import android.content.Context
import com.andresuryana.metembangbali.utils.Constants.APP_START_METHOD
import com.andresuryana.metembangbali.utils.Constants.SHARED_PREFS_KEY

object AppStartHelper {

    const val APP_FIRST_START = "first"
    const val APP_START_NORMAL = "normal"

    fun getStartMethod(context: Context): String? {
        val prefs = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)

        return prefs.getString(APP_START_METHOD, APP_FIRST_START)
    }

    fun setStartMethod(context: Context, method: String) {
        val prefs = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)

        if (method == APP_FIRST_START || method == APP_START_NORMAL) {
            prefs.edit().putString(APP_START_METHOD, method).apply()
        } else {
            error("Invalid method for application start. It should be between 'first' or 'normal'")
        }
    }
}