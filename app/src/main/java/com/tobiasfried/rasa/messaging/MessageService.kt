package com.tobiasfried.rasa.messaging

import android.util.Log

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService

class MessageService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(LOG_TAG, "Refreshed token: $token")
    }

    companion object {

        val LOG_TAG = MessageService::class.java.simpleName

        fun getInstanceId() {
            FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w(LOG_TAG, "getInstanceId failed", task.exception)
                            return@FirebaseInstanceId.getInstance().getInstanceId()
                                    .addOnCompleteListener
                        }

                        // Get new Instance ID token
                        val token = task.result!!.token

                        // Log and toast
                        Log.d(LOG_TAG, token)
                    }
        }
    }
}
