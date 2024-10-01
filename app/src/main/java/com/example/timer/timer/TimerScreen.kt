package com.example.timer.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.timer.decomposeTime

@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    timerValue: Float,
    onStartTimer: (Float) -> Unit,
    onResetTimer: () -> Unit
) {
    Column(
        modifier.background(Color.DarkGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val (hours, minutes, seconds) = timerValue.decomposeTime()
        Row {
            Text(text = hours.toString(), fontSize = 100.sp, color = Color.White)
            Text(text = ":", fontSize = 100.sp, color = Color.White)
            Text(text = minutes.toString(), fontSize = 100.sp, color = Color.White)
            Text(text = ":", fontSize = 100.sp, color = Color.White)
            Text(text = seconds.toString(), fontSize = 100.sp, color = Color.White)
        }

        Button(onClick = {
            onStartTimer(61f)
        }) {
            Text(text = "START", fontSize = 32.sp)
        }

        Button(onClick = {
            onResetTimer()
        }) {
            Text(text = "RESTART", fontSize = 32.sp)
        }
    }
}

@Preview
@Composable
private fun TimerScreenPreview() {
    TimerScreen(Modifier.fillMaxSize(), 320f, {}) {}
}