package com.example.timer.worker

import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface TimerWorkerRepository {
    val workerTimerValue: Flow<Float>
    val workerTimerState: Flow<TimerState>
    suspend fun startStopTimer(initialTimerValue: Float)
}