package com.example.timer

fun Float.roundToString() = this.toInt().toString()

fun Float.decomposeTime(): Triple<Int, Int, Int> {
    val seconds = this.toInt() % 60
    val totalMinutes = this.toInt() / 60
    val minutes = totalMinutes % 60
    val hours = totalMinutes / 60

    return Triple(hours, minutes, seconds)
}