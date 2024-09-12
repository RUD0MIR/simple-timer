package com.example.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log

private const val TAG = "AlarmReceiver"

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val alarm = RingtoneManager.getRingtone(context, sound)

        val action = intent?.action
        val alarmTriggered = context?.getString(R.string.alarm_triggered_action)
        val alarmCanceled = context?.getString(R.string.cancel_alarm_action)

        when(action) {
            alarmCanceled -> {
                if(alarm.isPlaying){
                    Log.d(TAG, "${alarm.isPlaying}")

                    alarm.stop()
                }
            }
            alarmTriggered -> {
                alarm.play()

                val activityIntent = Intent(context, WakeActivity::class.java)
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(activityIntent)
            }
        }
    }
}