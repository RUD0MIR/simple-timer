package com.example.timer.permission

import android.Manifest

sealed interface Permission {
    val id: String
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class PostNotificationPermission() : Permission {
    override val id = Manifest.permission.POST_NOTIFICATIONS

    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if(isPermanentlyDeclined) {
            "It seems you declined notification permission. " +
                    "You can go to the app settings to grant it."
        } else {
            "This app needs notification permission to show countdown timer in foreground"
        }
    }
}