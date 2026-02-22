package com.example.nhlsheetmanager.data.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.nhlsheetmanager.R

class NhlSheetUpdateWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    private val TAG = this::class.simpleName

    override fun doWork(): Result {
        updateNhlSheet()

        showNotification("NHL sheet updater", "Sheet was updated!")

        return Result.success()
    }

    private fun updateNhlSheet() {

    }

    private fun showNotification(title: String, message: String) {
        (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            val channelId = "channel1352"

            val channel = NotificationChannel(
                channelId,
                "NhlSheetUpdater",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(applicationContext, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .build()

            notify(1, notification)
        }
    }
}