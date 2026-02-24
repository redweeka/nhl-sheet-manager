package com.example.nhlsheetmanager.data.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.nhlsheetmanager.R
import com.example.nhlsheetmanager.models.NOTIFICATION_CHANNEL_ID
import com.example.nhlsheetmanager.models.UPDATE_WORKER_ID

class NhlSheetUpdateWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    private val TAG = this::class.simpleName

    override fun doWork(): Result {
        return try {
            updateNhlSheet()

            showNotification("NHL sheet updater #$UPDATE_WORKER_ID", "Sheet was updated!")

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun updateNhlSheet() {

    }

    private fun showNotification(title: String, message: String) {
        (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            val channelId = "channel_$NOTIFICATION_CHANNEL_ID"

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
