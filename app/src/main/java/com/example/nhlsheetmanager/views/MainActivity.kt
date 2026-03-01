package com.example.nhlsheetmanager.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.nhlsheetmanager.domain.workers.NhlSheetUpdateWorker
import com.example.nhlsheetmanager.models.NetworkUtils.isNetworkAvailable
import com.example.nhlsheetmanager.models.PermissionsUtils.handlePermissions
import com.example.nhlsheetmanager.models.TimeUtils.getInitialDelayForHourUtc
import com.example.nhlsheetmanager.models.UPDATE_WORKER_ID
import com.example.nhlsheetmanager.viewModels.NhlViewModel
import com.example.nhlsheetmanager.views.theme.NhlSheetManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = this::class.simpleName

    private val nhlViewModel: NhlViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)

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
                Toast.makeText(this, "Scheduling!", Toast.LENGTH_LONG).show()
                scheduleDaily10AmWorker(this)
            }
        )
    }
}

fun scheduleDaily10AmWorker(context: Context) {
    val delay = getInitialDelayForHourUtc()

    val dailyRequest = PeriodicWorkRequestBuilder<NhlSheetUpdateWorker>(
        24, TimeUnit.HOURS, 15, TimeUnit.MINUTES
    )
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "nhl_10utc_updater_schedule_$UPDATE_WORKER_ID",
        ExistingPeriodicWorkPolicy.KEEP,
        dailyRequest
    )
}
