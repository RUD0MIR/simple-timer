package com.example.timer.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun TimerScreen(
    modifier: Modifier = Modifier,
    timerValue: String,
    onStartTimer: (Float) -> Unit
) {
    Column(
        modifier.background(Color.DarkGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = timerValue, fontSize = 128.sp, color = Color.White)

        Button(onClick = {
            onStartTimer(10f)
        }) {
            Text(text = "START", fontSize = 32.sp)
        }
    }
}

@Preview
@Composable
private fun TimerScreenPreview() {
    TimerScreen(Modifier.fillMaxSize(), "60") {}
}