package com.example.timer.worker

import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.timer.broadcast.AlarmReceiver
import com.example.timer.R
import com.example.timer.TIMER_DEFAULT_VALUE
import com.example.timer.timer.TimerNotification
import kotlinx.coroutines.delay
import java.util.UUID


class TimerWorker(private val ctx: Context, params: WorkerParameters) :
    CoroutineWorker(ctx, params) {

    private var timerValue = TIMER_DEFAULT_VALUE
    private val notification = TimerNotification(ctx)
    private val workManager = WorkManager.getInstance(ctx)
    private var timerState = TimerState.UNKNOWN.id

    override suspend fun doWork(): Result {
        try {
            timerState = inputData.getInt(KEY_TIMER_STATE, TimerState.UNKNOWN.id)
            timerValue = inputData.getFloat(KEY_TIMER_INPUT, TIMER_DEFAULT_VALUE)

            when (timerState) {
                TimerState.IDLE.id -> cancelWork()
                TimerState.PAUSED.id -> pauseWork()
            }
            setForeground(createForegroundInfo(timerValue))
            doTimerWork()
        } catch (e: Exception) {
            Log.e(TAG, "exception in doWork(): ${e.message}")
            return Result.failure()
        }
        return Result.success()
    }

    private fun cancelWork() {
        timerValue = 0f
        workManager.cancelUniqueWork(name)
    }

    private fun pauseWork() {
        workManager.cancelUniqueWork(name)
    }

    private suspend fun doTimerWork() {
        while (timerState == TimerState.RUNNING.id) {
            if (timerValue <= 0f) {
                onWorkFinished()
            }

            delay(100L)

            timerValue -= 0.1f

            updateTimerValue()
        }
    }

    private fun onWorkFinished() {
        val alarmIntent = Intent(ctx, AlarmReceiver::class.java).apply {
            action = ctx.getString(R.string.trigger_alarm_action)
        }
        ctx.sendBroadcast(alarmIntent)

        cancelWork()
    }

    private suspend fun updateTimerValue() {
        setProgress(
            workDataOf(KEY_TIMER_OUTPUT to timerValue)
        )
//        if (timerState == TimerState.RUNNING.id) {
//            notification.update(timerValue)
//        }
    }

    private fun createForegroundInfo(timerValue: Float): ForegroundInfo {
        val foregroundInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            notification.createNotificationChannel()
            ForegroundInfo(
                notification.id,
                notification.buildNotification(timerValue),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(notification.id, notification.buildNotification(timerValue))
        }

        return foregroundInfo
    }

    companion object {
        const val KEY_TIMER_OUTPUT = "timerValue"
        const val KEY_TIMER_INPUT = "timerValue"
        const val KEY_TIMER_STATE = "timerState"
        const val KEY_TIMER_COMMAND = "timerCommand"
        val name = "TimerWorker"
        val uuid = UUID.randomUUID()
        private const val TAG = "TimerWorker"
    }
}


