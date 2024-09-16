package com.example.timer.worker

import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface TimerWorkerRepository {
    val workerTimerValue: Flow<Float>
    suspend fun startStopTimer(initialTimerValue: Float)
    suspend fun resetTimer()
}