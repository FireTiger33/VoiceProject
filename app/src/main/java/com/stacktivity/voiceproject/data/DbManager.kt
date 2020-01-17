package com.stacktivity.voiceproject.data

import android.util.Log
import androidx.lifecycle.ViewModel
import com.stacktivity.voiceproject.utils.SharedPrefsHelper
import com.quickblox.users.model.QBUser
import com.stacktivity.voiceproject.App

class DbManager: ViewModel() {
    companion object {
        private val TAG = DbManager::class.java.simpleName
        private val context = App.getInstance().applicationContext

        suspend fun getQbUserById(userID: Int): QBUser {
            Log.d(TAG, "getQbUser, id: $userID")
            return AppDataBase.getInstance(context).userDao().getQbUserById(userID)
        }

        suspend fun getQbUsersByIds(usersIDs: List<Int>): ArrayList<QBUser> {
            Log.d(TAG, "getQbUsers, count ${usersIDs.size}")
            return AppDataBase.getInstance(context).userDao().getQbUsersByIds(usersIDs)
        }

        suspend fun saveAllUsers(users: ArrayList<QBUser>, clear: Boolean) {
            Log.d(TAG, "saveAllUsers, clear $clear")
            // TODO clear selective cleaning
            AppDataBase.getInstance(context).userDao().incertAllQbUsers(users)
        }

        suspend fun getAllUsers(): ArrayList<QBUser> {
            Log.d(TAG, "getAllUsers")
            val allUsers = AppDataBase.getInstance(context).userDao().getAllQbUsers()
            allUsers.remove(SharedPrefsHelper.getCurrentUser())

            return allUsers
        }

        suspend fun clearData() {
            AppDataBase.getInstance(context).userDao().clearDB()
        }
    }
}