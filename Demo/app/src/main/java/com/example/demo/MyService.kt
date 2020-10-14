package com.example.demo

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyService : FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d("CloudMessage", "$p0")
    }
    
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.d("m1","$p0")
        p0?.notification?.body?.let {
            sendMessage(it)
        }
    }

    fun sendMessage(message : String){
        val intent = Intent("MyMessage")
        intent.putExtra("message",message)
        sendBroadcast(intent)
    }
}