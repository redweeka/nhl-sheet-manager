package com.example.nhlsheetmanager.models

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat

object PermissionsUtils {
    fun handlePermissions(
        activity: Activity,
        onPermissionsGranted: () -> Unit = {}
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionStatus = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            )

            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                onPermissionsGranted()
            } else {
                val canAskPermission = shouldShowRequestPermissionRationale(activity, Manifest.permission.POST_NOTIFICATIONS)

                if (canAskPermission) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        1353
                    )
                } else {
                    activity.openNotificationSettings()
                }
            }
        } else {
            onPermissionsGranted()
        }
    }

    private fun Context.openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }

        startActivity(intent)
    }
}