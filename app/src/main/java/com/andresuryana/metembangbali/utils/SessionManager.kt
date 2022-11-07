package com.andresuryana.metembangbali.utils

import android.content.Context
import android.content.SharedPreferences
import com.andresuryana.metembangbali.utils.Constants.GUEST_USER
import com.andresuryana.metembangbali.utils.Constants.REGISTERED_USER
import com.andresuryana.metembangbali.utils.Constants.SHARED_PREFS_KEY
import com.andresuryana.metembangbali.utils.Constants.USER_ROLE
import com.andresuryana.metembangbali.utils.Constants.USER_TOKEN_KEY
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var prefs: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)

    fun authenticateAsRegisteredUser(token: String) {
        prefs.edit().apply {
            putString(USER_TOKEN_KEY, token)
            putString(USER_ROLE, REGISTERED_USER)
            apply()
        }
    }

    fun authenticateAsGuest() {
        prefs.edit().apply {
            putString(USER_ROLE, GUEST_USER)
            apply()
        }
    }

    fun fetchAuthToken(): String? {
        return if (prefs.contains(USER_TOKEN_KEY)) {
            prefs.getString(USER_TOKEN_KEY, null)
        } else {
            clearAuthentication()
            null
        }
    }

    fun clearAuthentication() {
        // Clear prefs
        prefs.edit().apply {
            remove(USER_TOKEN_KEY)
            remove(USER_ROLE)
            apply()
        }
    }

    fun getAuthStatus(): String? = prefs.getString(USER_ROLE, null)
}