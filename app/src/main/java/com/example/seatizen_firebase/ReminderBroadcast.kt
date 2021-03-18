package com.example.seatizen_firebase

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider

public class ReminderBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
         val builder: NotificationCompat.Builder = NotificationCompat.Builder(context,"01")
             .setSmallIcon(R.mipmap.ic_launcher)
             .setContentTitle("Seatizen - Reminder!!")
             .setContentText("Download your report before it is gone")
             .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(context)){
            notify(101,builder.build())
        }
    }

}