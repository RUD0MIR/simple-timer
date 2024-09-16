package com.example.timer.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.timer.TIMER_DEFAULT_VALUE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import java.util.Timer

class TimerWorkerRepoImpl(
    ctx: Context,
) : TimerWorkerRepository {
    private val workManager = WorkManager.getInstance(ctx)
    private val requestBuilder = OneTimeWorkRequestBuilder<TimerWorker>().setId(TimerWorker.uuid)
    private val workInfo = workManager.getWorkInfosForUniqueWorkFlow(TimerWorker.name)

    override var workerTimerValue = workInfo.map {
        it.last().progress.getFloat(TimerWorker.KEY_TIMER_OUTPUT, TIMER_DEFAULT_VALUE)
    }

    private var timerState = TimerState.IDLE

    override suspend fun startStopTimer(initialTimerValue: Float) {
        when(timerState) {
            TimerState.IDLE -> {
                timerState = TimerState.RUNNING
                startTimer(initialTimerValue)
            }
            TimerState.RUNNING -> {
                timerState = TimerState.PAUSED
                pauseTimer(initialTimerValue)
            }
            TimerState.PAUSED -> {
                timerState = TimerState.RUNNING
                startTimer(initialTimerValue)
            }
            else -> {}
        }
    }

    override suspend fun resetTimer() {
        val workRequest = requestBuilder
            .setInputData(workDataOf(
                TimerWorker.KEY_TIMER_STATE to TimerState.IDLE.id
            ))
            .build()
        workManager.enqueueUniqueWork(TimerWorker.name, ExistingWorkPolicy.REPLACE, workRequest)

        timerState = TimerState.IDLE
    }

    private fun startTimer(initialTimerValue: Float) {
        val workRequest = requestBuilder
            .setInputData(
                 workDataOf(
                     TimerWorker.KEY_TIMER_INPUT to initialTimerValue,
                     TimerWorker.KEY_TIMER_STATE to TimerState.RUNNING.id
                 )
            )
            .build()
        workManager.enqueueUniqueWork(TimerWorker.name, ExistingWorkPolicy.REPLACE, workRequest)
    }

    private fun pauseTimer(initialTimerValue: Float) {
        val workRequest = requestBuilder
            .setInputData(
                workDataOf(
                    TimerWorker.KEY_TIMER_INPUT to initialTimerValue,
                    TimerWorker.KEY_TIMER_STATE to TimerState.PAUSED.id
                )
            )
            .build()
        workManager.enqueueUniqueWork(TimerWorker.name, ExistingWorkPolicy.REPLACE, workRequest)
    }
}




