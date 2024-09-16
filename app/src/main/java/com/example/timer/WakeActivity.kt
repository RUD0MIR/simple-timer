package com.example.timer

import android.app.Activity
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.timer.ui.theme.TIMERTheme


class WakeActivity : ComponentActivity() {

    private var ringtone: Ringtone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        ringtone = RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))

        setContent {
            TIMERTheme {
                val context = LocalContext.current
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Alarm is ringing",
                        color = Color.White,
                        fontSize = 32.sp
                    )
                    Button(onClick = {
                        (context as Activity).finish()
                    }) {
                        Text(text = "Stop alarm")
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        unlockScreen()
        ringtone?.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarmSound()
    }

    private fun stopAlarmSound() {
        ringtone?.stop()
        ringtone = null
    }

    private fun unlockScreen() {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    }
}

