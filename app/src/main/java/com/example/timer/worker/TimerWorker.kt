package com.example.timer.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.timer.R
import com.example.timer.TIMER_DEFAULT_VALUE
import com.example.timer.timer.TimerNotification
import kotlinx.coroutines.delay


class TimerWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private var timerValue = inputData.getFloat(KEY_TIMER_INPUT, TIMER_DEFAULT_VALUE)
    private val notification = TimerNotification(timerValue, applicationContext)

    private var timerState = TimerState.IDLE

    override suspend fun doWork(): Result {
        try {
            startTimer()
        } catch (e: Exception) {
            Log.e(TAG, "exception in doWork(): ${e.message}")
            return Result.failure()
        }
        return Result.success()
    }

    private suspend fun startTimer() {
        updateTimerState(TimerState.RUNNING)

        while (timerState == TimerState.RUNNING) {
            if (timerValue <= 0f) {
                updateTimerState(TimerState.IDLE)
            }

            delay(100L)

            timerValue -= 0.1f

            updateTimerValue()
        }
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

        Log.d(TAG, timerState.name)

        when (timerState) {
            TimerState.RUNNING -> notification.update(timerValue)
            TimerState.IDLE -> notification.hide()
            else -> {}
        }
    }

    companion object {
        const val KEY_TIMER_OUTPUT = "timerValue"
        const val KEY_TIMER_INPUT = "timerValue"
        const val KEY_TIMER_STATE = "timerState"
        val name = "TimerWorker"

        private const val TAG = "TimerWorker"
    }
}

enum class TimerState(val id: Int) {
    IDLE(0),
    RUNNING(1),
    UNKNOWN(-1)
}

fun Int.toTimerState() = TimerState.entries.find { it.id == this } ?: TimerState.UNKNOWN
