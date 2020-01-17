package com.stacktivity.voiceproject.utils

import android.content.Context
import android.content.SharedPreferences
import com.quickblox.users.model.QBUser
import com.stacktivity.voiceproject.App
import com.stacktivity.voiceproject.data.db.User

object SharedPrefsHelper {

    private var sharedPreferences: SharedPreferences = App.getInstance().getSharedPreferences(
        "permission", Context.MODE_PRIVATE)

    fun delete(key: String) {
        if (sharedPreferences.contains(key)) {
            sharedPreferences.edit().remove(key).apply()
        }
    }

    fun save(key: String, value: Any?) {
        val editor = sharedPreferences.edit()
        when {
            value is Boolean -> editor.putBoolean(key, (value))
            value is Int -> editor.putInt(key, (value))
            value is Float -> editor.putFloat(key, (value))
            value is Long -> editor.putLong(key, (value))
            value is String -> editor.putString(key, value)
            value is Enum<*> -> editor.putString(key, value.toString())
            value != null -> throw RuntimeException("Attempting to save non-supported preference")
        }
        editor.apply()
    }

    fun saveCurrentUser(qbUser: QBUser) {
        val prefs = App.getInstance().getSharedPreferences(
            "user", Context.MODE_PRIVATE)
        PreferencesUtils.saveItemToJSON(User(qbUser), prefs, "incomer")
    }

    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(key: String): T {
        return sharedPreferences.all[key] as T
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(key: String, defValue: T): T {
        val returnValue = sharedPreferences.all[key] as T
        return returnValue ?: defValue
    }

    fun getCurrentUser(): QBUser? {
        val prefs = App.getInstance().getSharedPreferences(
            "user", Context.MODE_PRIVATE)
        val user: User? = PreferencesUtils.loadItemFromJSON(
            User::class.java, prefs, "incomer"
        ) as User?

        return user?.getQbUser()
    }
}