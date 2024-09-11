package com.example.timer.timer

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationCompat.Builder
import com.example.timer.R
import com.example.timer.decomposeTime
import com.example.timer.roundToString

class TimerNotification(private val context: Context) : TimerNotificationRepository {
    val id = 1

    private val defaultTitleId = R.string.timer_notification_title
    private val channelId = "TIMER_NOTIFICATION"
    private val channelName: CharSequence = "Timer notification"
    private val channelDescription = "Shows notification with countdown timer"

    @SuppressLint("MissingPermission")
    override fun update(timerValue: Float) {
        NotificationManagerCompat
            .from(context)
            .notify(id, buildNotification(timerValue))
    }

    override fun hide() {
        NotificationManagerCompat
            .from(context)
            .cancel(id)
    }

    fun buildNotification(timerValue: Float): Notification {
        val (h, min, s) = timerValue.decomposeTime()

        return Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_timer_24)
            .setContentTitle(context.getString(defaultTitleId))
            .setContentText(("$h:$min:$s"))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        val name = channelName
        val description = channelDescription
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = description

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }
}

