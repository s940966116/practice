package com.example.demo

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class MyReceiver(val context: Context) : BroadcastReceiver() {
    lateinit var manager : NotificationManager
    lateinit var builder : Notification.Builder
    override fun onReceive(context: Context, intent: Intent) {
        intent.extras?.let {
            noti(intent)
            manager.notify(0,builder.build())
            showDialog(intent)
        }
    }
    fun noti(intent: Intent?){
        manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("m1","m1",NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
            builder = Notification.Builder(context,"m1")
        }else{
            builder = Notification.Builder(context)
        }

        builder.setSmallIcon(R.drawable.p1)
            .setContentTitle(intent?.getStringExtra("message"))
            .setContentText(intent?.getStringExtra("message"))
            .setAutoCancel(true)
    }

    fun showDialog(intent: Intent?){
        AlertDialog.Builder(context)
            .setTitle("alert")
            .setMessage(intent!!.getStringExtra("message"))
            .setPositiveButton("ok"){
                dialog, which ->  dialog.cancel()
            }.show()
    }
}
