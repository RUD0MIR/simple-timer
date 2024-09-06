package com.example.timer.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.timer.TIMER_DEFAULT_VALUE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class TimerWorkerRepoImpl(
    ctx: Context,
) : TimerWorkerRepository {
    private val workManager = WorkManager.getInstance(ctx)
    private val requestBuilder = OneTimeWorkRequestBuilder<TimerWorker>()
    private val workInfo = workManager.getWorkInfosForUniqueWorkFlow(TimerWorker.name)

    override val workerTimerValue = workInfo.map {
        it.last().progress.getFloat(TimerWorker.KEY_TIMER_OUTPUT, TIMER_DEFAULT_VALUE)
    }

    override val workerTimerState = workInfo.map {
        it.last().progress.getInt(TimerWorker.KEY_TIMER_STATE, TimerState.UNKNOWN.id)
            .toTimerState()
    }

    private var isTimerRunning = false

    override suspend fun startStopTimer(initialTimerValue: Float) {
        if (!isTimerRunning) startWork(initialTimerValue)
        else pauseWork()

        isTimerRunning = !isTimerRunning
    }

    private fun startWork(initialTimerValue: Float) {
        val workRequest = requestBuilder.setInputData(initialTimerValue.asInputData()).build()
        workManager.enqueueUniqueWork(TimerWorker.name, ExistingWorkPolicy.KEEP, workRequest)
    }

    private fun pauseWork() {
        workManager.cancelUniqueWork(TimerWorker.name)
    }

    private fun Float.asInputData(): Data {
        val builder = Data.Builder()
        builder.putFloat(TimerWorker.KEY_TIMER_INPUT, this)
        return builder.build()
    }
}




