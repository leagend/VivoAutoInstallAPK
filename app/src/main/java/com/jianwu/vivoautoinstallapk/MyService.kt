package com.jianwu.vivoautoinstallapk

import android.app.*
import android.app.Notification.PRIORITY_MAX
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.widget.Toast
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.PRIORITY_HIGH
import android.support.v4.app.NotificationCompat.PRIORITY_MIN


enum class MSG_TYPE {
    START,
    STOP,
}

// Handler that receives messages from the thread
private class ServiceHandler(looper: Looper) : Handler(looper) {
    override fun handleMessage(msg: Message) {
    }
}

class MyService : Service() {
    private lateinit var mServiceLooper: Looper
    private var mServiceHandler: ServiceHandler? = null


    override fun onCreate() {
        startForeground()

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
//        val thread = HandlerThread("MyService")
//        thread.start()
//
//        // Get the HandlerThread's Looper and use it for our Handler
//        mServiceLooper = thread.looper
//        mServiceHandler = ServiceHandler(mServiceLooper)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service started", Toast.LENGTH_SHORT).show()

        // If we get killed, after returning from here, restart
        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
    }

    private fun startForeground() {
        val channelId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel("my_service", "My Background Service")
                } else {
                    // If earlier version channel ID is not used
                    // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                    ""
                }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0)

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentTitle("My Vivo Installer")
                .setContentText("hello world ...")
                .setContentIntent(pendingIntent)
                .build()
        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        chan.description = "Hello notification channel"
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
}
