package com.stacktivity.voiceproject.services.fcm

import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import com.quickblox.messages.services.fcm.QBFcmPushListenerService
import com.stacktivity.voiceproject.utils.SharedPrefsHelper
import com.quickblox.users.model.QBUser
import com.stacktivity.voiceproject.App
import com.stacktivity.voiceproject.services.LoginService

class PushListenerService : QBFcmPushListenerService() {  // TODO
    private val TAG = PushListenerService::class.java.simpleName

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        val qbUser: QBUser? = SharedPrefsHelper.getCurrentUser()
        if (qbUser != null) {
            Log.d(TAG, "App has logged user" + qbUser.id)
            LoginService.start(
                App.getInstance().applicationContext,
                qbUser
            )  // TODO check
        }
    }

    override fun sendPushMessage(data: MutableMap<Any?, Any?>?, from: String?, message: String?) {
        super.sendPushMessage(data, from, message)
        Log.v(TAG, "From: $from")
        Log.v(TAG, "Message: $message")
    }
}