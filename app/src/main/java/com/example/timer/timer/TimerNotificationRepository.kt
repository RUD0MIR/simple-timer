package com.example.timer.timer

interface TimerNotificationRepository {

    fun update(timerValue: Float)
    fun hide()
}