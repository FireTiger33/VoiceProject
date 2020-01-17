package com.stacktivity.voiceproject.utils

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import java.io.Serializable
import kotlin.collections.ArrayList

class PreferencesUtils {

    companion object {
        interface BaseJson<T> {
            fun getItemList(): List<T>
        }

        private val TAG = PreferencesUtils::class.java.simpleName

        fun <SerializableClass: Serializable>loadItemFromJSON(
            jsonClass: Class<SerializableClass>,
            preferences: SharedPreferences,
            key: String
        ): Any? {
            return if (preferences.contains(key)) {
                val titlesJSON = preferences.getString(key, null)
                Log.d(TAG, "loadJSON:\n${titlesJSON}")
                Gson().fromJson(titlesJSON, jsonClass)
            } else {
                 null
            }
        }

        fun <ClassJSON: BaseJson<ItemClass>, ItemClass> loadItemListFromJSON(
            jsonClass: Class<ClassJSON>,
            preferences: SharedPreferences,
            key: String
        ): ArrayList<ItemClass> {
            return if (preferences.contains(key)) {
                val titlesJSON = preferences.getString(key, null)
                val jsonData: ClassJSON = Gson().fromJson(titlesJSON, jsonClass)
                ArrayList(jsonData.getItemList())
            } else {
                ArrayList()
            }
        }

        fun <ClassJSON> saveItemToJSON(
            itemClass: ClassJSON,
            preferences: SharedPreferences,
            key: String
        ) {
            Log.d(TAG, itemClass.toString())
            val itemsJSON = Gson().toJson(itemClass)
            Log.d(TAG, "saveJSON:\n${itemsJSON}")

            var success = false
            while (!success) {
                success = preferences.edit()
                    .putString(key, itemsJSON)
                    .commit()
            }
        }

        fun <ClassJSON> saveItemListToJSON(
            itemsClass: ClassJSON,
            preferences: SharedPreferences,
            key: String
        ) {
            saveItemToJSON(itemsClass, preferences, key)
        }
    }
}