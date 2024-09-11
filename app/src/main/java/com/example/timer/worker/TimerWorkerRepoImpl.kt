package com.example.timer.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.timer.TIMER_DEFAULT_VALUE
import kotlinx.coroutines.flow.map

class TimerWorkerRepoImpl(
    ctx: Context,
) : TimerWorkerRepository {
    private val workManager = WorkManager.getInstance(ctx)
    private val requestBuilder = OneTimeWorkRequestBuilder<TimerWorker>().setId(TimerWorker.uuid)
    private val workInfo = workManager.getWorkInfosForUniqueWorkFlow(TimerWorker.name)

    override var workerTimerValue = workInfo.map {
        it.last().progress.getFloat(TimerWorker.KEY_TIMER_OUTPUT, TIMER_DEFAULT_VALUE)
    }

    override var workerTimerState = workInfo.map {
        it.last().progress.getInt(TimerWorker.KEY_TIMER_STATE, TimerState.UNKNOWN.id)
            .toTimerState()
    }

    private var isTimerRunning = false

    override suspend fun startStopTimer(initialTimerValue: Float) {
        if (!isTimerRunning) startTimer(initialTimerValue)
        else pauseTimer()
    }

    override suspend fun resetTimer() {
        sendCommand(TimerWorkerCommand.RESET_TIMER)
        isTimerRunning = false
    }

    private fun startTimer(initialTimerValue: Float) {
        val workRequest = requestBuilder
            .setInputData(
                 workDataOf(
                     TimerWorker.KEY_TIMER_INPUT to initialTimerValue,
                     TimerWorker.KEY_TIMER_COMMAND to TimerWorkerCommand.START_TIMER.id
                 )
            )
            .build()
        workManager.enqueueUniqueWork(TimerWorker.name, ExistingWorkPolicy.KEEP, workRequest)
        isTimerRunning = true

    }

    private fun pauseTimer() {
        sendCommand(TimerWorkerCommand.PAUSE_TIMER)
        isTimerRunning = false
    }

    private fun sendCommand(command: TimerWorkerCommand) {
        val workRequest = requestBuilder
            .setInputData(workDataOf(
                TimerWorker.KEY_TIMER_COMMAND to command.id,
            ))
            .build()
        workManager.enqueueUniqueWork(TimerWorker.name, ExistingWorkPolicy.REPLACE, workRequest)
    }
}




