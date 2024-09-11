package com.example.timer.worker

enum class TimerWorkerCommand(val id: Int) {
    START_TIMER(1),
    PAUSE_TIMER(2),
    RESET_TIMER(0),
    NO_COMMAND(-1),
}

enum class TimerState(val id: Int) {
    IDLE(0),
    PAUSED(2),
    RUNNING(1),
    UNKNOWN(-1)
}

fun Int.toTimerState() = TimerState.entries.find { it.id == this } ?: TimerState.UNKNOWN