package com.example.nhlsheetmanager.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.nhlsheetmanager.data.repositories.NhlRepository
import com.example.nhlsheetmanager.data.repositories.SheetsRepository
import com.example.nhlsheetmanager.data.workers.NhlSheetUpdateWorker
import com.example.nhlsheetmanager.models.NetworkUtils.isNetworkAvailable
import com.example.nhlsheetmanager.models.PermissionsUtils.handlePermissions
import com.example.nhlsheetmanager.ui.theme.NhlSheetManagerTheme
import com.example.nhlsheetmanager.viewModels.NhlViewModel
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val TAG = this::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)

        val nhlRepository = NhlRepository()
        val sheetsRepository = SheetsRepository(resources)
        val nhlViewModel = NhlViewModel(nhlRepository, sheetsRepository)

        setContent {
            NhlSheetManagerTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val updatedPlayers by nhlViewModel.updatedPlayers.collectAsState()
                    val updateState by nhlViewModel.updateState.collectAsState()

                    // Making the log lines
                    val logs = remember(updatedPlayers) {
                        convertPlayersToLogsList(updatedPlayers)
                    }

                    MainScreen(
                        logs = logs,
                        updateState = updateState,
                        onUpdateClick = {
                            if (isNetworkAvailable(this)) {
                                nhlViewModel.updatePlayers()
                            } else {
                                Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        handlePermissions(
            activity = this,
            onPermissionsGranted = {
                scheduleNhlSheetUpdateWork(this)
            }
        )
    }

    private fun scheduleNhlSheetUpdateWork(context: Context) {
        Toast.makeText(context, "Scheduling!", Toast.LENGTH_LONG).show()

        val nhlSheetUpdateRequest = OneTimeWorkRequestBuilder<NhlSheetUpdateWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS).build()

        WorkManager.getInstance(context).enqueue(nhlSheetUpdateRequest)
    }
}
