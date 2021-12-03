package com.example.assignment2_MAD_19012021092

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && context != null) {
            val str1 = intent.getStringExtra("Service1")
            if (str1 == null) {
            } else if (str1 == "Start" || str1 == "Stop") {
                val intentService = Intent(context, AlarmService::class.java)
                intentService.putExtra("Service1", intent.getStringExtra("Service1"))
                if (str1 == "Start") {
                    context.startService(intentService)
                } else if (str1 == "Stop")
                    context.stopService(intentService)
            }
        }
    }
}