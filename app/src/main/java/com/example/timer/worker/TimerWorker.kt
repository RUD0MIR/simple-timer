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
import com.example.timer.AlarmReceiver
import com.example.timer.R
import com.example.timer.TIMER_DEFAULT_VALUE
import com.example.timer.WakeActivity
import com.example.timer.decomposeTime
import com.example.timer.timer.TimerNotification
import kotlinx.coroutines.delay
import java.util.UUID


class TimerWorker(private val ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private var timerValue = TIMER_DEFAULT_VALUE
    private val notification = TimerNotification(ctx)
    private var currentCommand = TimerWorkerCommand.NO_COMMAND.id
    private val workManager = WorkManager.getInstance(ctx)
    private var timerState = TimerState.IDLE

    override suspend fun doWork(): Result {
        try {
            currentCommand = inputData.getInt(KEY_TIMER_COMMAND, TimerWorkerCommand.NO_COMMAND.id)

            when (currentCommand) {
                TimerWorkerCommand.START_TIMER.id -> startTimer()
                TimerWorkerCommand.PAUSE_TIMER.id -> pauseTimer()
                TimerWorkerCommand.RESET_TIMER.id -> resetTimer()
            }
            setForeground(createForegroundInfo(timerValue))
            startTimerWork()
        } catch (e: Exception) {
            Log.e(TAG, "exception in doWork(): ${e.message}")
            return Result.failure()
        }
        return Result.success()
    }

    private suspend fun startTimer() {
        timerValue = inputData.getFloat(KEY_TIMER_INPUT, TIMER_DEFAULT_VALUE)
        updateTimerState(TimerState.RUNNING)
    }

    private suspend fun pauseTimer() {
        updateTimerState(TimerState.PAUSED)
    }

    private suspend fun resetTimer() {
        updateTimerState(TimerState.IDLE)
        timerValue = 0f
        workManager.cancelUniqueWork(name)
    }

    private suspend fun startTimerWork() {
        while (timerState == TimerState.RUNNING) {
            if (timerValue <= 0f) {
                onTimerFinished()
            }

            delay(100L)

            timerValue -= 0.1f

            updateTimerValue()
        }
    }

    private suspend fun onTimerFinished() {
        updateTimerState(TimerState.IDLE)

        val alarmIntent = Intent(ctx, AlarmReceiver::class.java).apply {
            action = ctx.getString(R.string.alarm_triggered_action)
        }
        ctx.sendBroadcast(alarmIntent)

        resetTimer()
    }

    private suspend fun updateTimerValue() {
        setProgress(
            workDataOf(KEY_TIMER_OUTPUT to timerValue)
        )
        if (timerState == TimerState.RUNNING) {
            notification.update(timerValue)
        }
    }

    private suspend fun updateTimerState(state: TimerState) {
        timerState = state
        setProgress(
            workDataOf(KEY_TIMER_STATE to timerState.id)
        )
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


