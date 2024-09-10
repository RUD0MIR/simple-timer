package com.example.timer.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timer.TIMER_DEFAULT_VALUE
import com.example.timer.worker.TimerWorkerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel(
    private val repo: TimerWorkerRepository
) : ViewModel() {

    private val _timerValue = MutableStateFlow(0f)
    val timerValue = _timerValue.asStateFlow()

    fun startStopTimer(initialValue: Float) {
        if (_timerValue.value == 0f) {
            _timerValue.value = initialValue
        }

        viewModelScope.launch {
            repo.startStopTimer(_timerValue.value)

            repo.workerTimerValue.collect {
                if (it != TIMER_DEFAULT_VALUE) {
                    _timerValue.value = it
                }
            }
        }
    }

    fun resetTimer() {
        viewModelScope.launch {
            repo.resetTimer()
            _timerValue.value = 0f
        }
    }
}