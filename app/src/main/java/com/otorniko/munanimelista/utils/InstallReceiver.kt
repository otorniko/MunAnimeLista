package com.otorniko.munanimelista.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import android.util.Log

class InstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -1)) {
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                val confirmIntent = getParcelable(intent)

                if (confirmIntent != null) {
                    confirmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(confirmIntent)
                }
            }

            PackageInstaller.STATUS_SUCCESS -> {
                Log.d("InstallReceiver", "Update installed successfully")
            }

            else -> {
                val msg = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
                Log.e("InstallReceiver", "Install failed ($status): $msg")
            }
        }
    }

    /**
     * Helper to retrieve the specific 'Intent.EXTRA_INTENT' safely across Android versions.
     * We hardcode the name 'Intent.EXTRA_INTENT' here to fix the "SameParameterValue" warning.
     */
    private fun getParcelable(intent: Intent): Intent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // New Type-Safe API (Android 13+)
            intent.getParcelableExtra(Intent.EXTRA_INTENT, Intent::class.java)
        } else {
            // Old API (Deprecated but required for older phones)
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Intent.EXTRA_INTENT) as? Intent
        }
    }
}