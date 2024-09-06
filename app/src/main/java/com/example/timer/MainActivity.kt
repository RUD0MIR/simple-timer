package com.example.timer

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.example.timer.permission.PermissionDialog
import com.example.timer.permission.PostNotificationPermission
import com.example.timer.timer.TimerScreen
import com.example.timer.timer.TimerViewModel
import com.example.timer.worker.TimerWorkerRepoImpl

class MainActivity : ComponentActivity() {
    private val timerVm = TimerViewModel(TimerWorkerRepoImpl(this))
    private val mainVm = MainViewModel()
    private val requiredPermission = PostNotificationPermission()

    private val requiredPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            mainVm.onPermissionResult(
                permission = requiredPermission,
                isGranted = granted
            )
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShowPermissionsDialog()
            TimerScreen(
                Modifier.fillMaxSize(),
                timerVm.timerValue.collectAsState().value.roundToString(),
                onStartTimer = timerVm::startStopTimer
            )
        }
    }

    @Composable
    fun ShowPermissionsDialog() {
        mainVm.visiblePermissionDialogQueue
            .reversed()
            .forEach { permission ->
                PermissionDialog(
                    permission = requiredPermission,
                    isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                        permission.id
                    ),
                    onDismiss = mainVm::dismissDialog,
                    onOkClick = {
                        mainVm.dismissDialog()
                    },
                    onGoToAppSettingsClick = ::openAppSettings
                )
            }
    }

    override fun onStart() {
        super.onStart()
        if (checkSelfPermission(requiredPermission.id) == PackageManager.PERMISSION_GRANTED) {
            return
        }

        requiredPermissionRequest.launch(
            requiredPermission.id
        )
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

