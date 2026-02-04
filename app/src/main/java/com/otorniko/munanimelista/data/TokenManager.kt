package com.otorniko.munanimelista.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
            context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String, refreshToken: String? = null) {
        prefs.edit {
            putString("access_token", token)
            if (refreshToken != null) putString("refresh_token", refreshToken)
        }
    }

    fun getToken(): String? {
        return prefs.getString("access_token", null)
    }

    fun clear() {
        prefs.edit { clear() }
    }
}