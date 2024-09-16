package com.example.timer.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.timer.R
import com.example.timer.WakeActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val action = intent?.action
        val alarmTriggered = context?.getString(R.string.trigger_alarm_action)

        when (action) {
            alarmTriggered -> {
                val activityIntent = Intent(context, WakeActivity::class.java)
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(activityIntent)
            }
        }
    }
}