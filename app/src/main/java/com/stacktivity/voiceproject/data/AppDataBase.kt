package com.stacktivity.voiceproject.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.stacktivity.voiceproject.data.db.User
import com.stacktivity.voiceproject.data.db.UserDao

@Database(
    entities = [User::class],
    version = 1
)
abstract class AppDataBase: RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile // For Singleton instantiation
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDataBase(context).also { INSTANCE = it }
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }

        private fun buildDataBase(context: Context): AppDataBase {
            Log.d("AppDataBase", "build")
            return Room.databaseBuilder(context.applicationContext,
                AppDataBase::class.java, "voiceprojectWebrtc.db")
                .build()
        }
    }
}