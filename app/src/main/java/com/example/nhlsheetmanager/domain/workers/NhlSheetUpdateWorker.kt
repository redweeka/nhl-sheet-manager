package com.example.nhlsheetmanager.domain.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.nhlsheetmanager.R
import com.example.nhlsheetmanager.domain.repositories.repositoriesInterfaces.NhlRepositoryInterface
import com.example.nhlsheetmanager.domain.repositories.repositoriesInterfaces.SheetsRepositoryInterface
import com.example.nhlsheetmanager.models.NOTIFICATION_CHANNEL_ID
import com.example.nhlsheetmanager.models.Player
import com.example.nhlsheetmanager.models.UPDATE_WORKER_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NhlSheetUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val nhlRepository: NhlRepositoryInterface,
    private val sheetsRepository: SheetsRepositoryInterface
) : Worker(context, workerParameters) {
    private val TAG = this::class.simpleName

    override fun doWork(): Result {
        return try {
            val notUpdatedPlayers = sheetsRepository.getPlayers()

            if (notUpdatedPlayers.isNotEmpty()) {
                showNotification("Got ${notUpdatedPlayers.size} players")

                val updatedPlayers = updateLocalPlayersPoints(notUpdatedPlayers)

                if (updatedPlayers.isNotEmpty()) {
                    showNotification("Got ${updatedPlayers.size} updated players")

                    updateRemotePlayersPoints(updatedPlayers)

                    showNotification("Sheet was updated!")
                } else {
                    showNotification("Can't retrieve updated players")
                }
            } else {
                showNotification("Can't retrieve player")
            }

            Result.success()
        } catch (e: Exception) {
            showNotification("Error in ${e.message}")

            Result.retry()
        }
    }

    private fun updateRemotePlayersPoints(localPlayers: List<Player>) {
        localPlayers.forEach { player ->
            if (player.playerUpdatedPoints > player.playerPreviousPoints) {
                sheetsRepository.updateRemotePlayerPoints(player.row, player.playerUpdatedPoints)
            }
        }
    }

    private fun updateLocalPlayersPoints(localPlayers: List<Player>): List<Player> {
        return localPlayers.map { player ->
            player.apply {
                playerUpdatedPoints = nhlRepository.getPlayerCurrentSeasonPointsById(player.playerId)
            }
        }
    }

    private fun showNotification(message: String) {
        (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            val channelId = "channel_$NOTIFICATION_CHANNEL_ID"

            val channel = NotificationChannel(
                channelId,
                "NhlSheetUpdater",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(applicationContext, channelId)
                .setContentTitle("NHL sheet updater #$UPDATE_WORKER_ID")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .build()

            notify(1, notification)
        }
    }
}
