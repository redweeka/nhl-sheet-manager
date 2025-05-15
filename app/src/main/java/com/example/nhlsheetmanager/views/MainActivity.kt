package com.example.nhlsheetmanager.views

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.CONNECTIVITY_SERVICE
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nhlsheetmanager.repositories.NhlRepository
import com.example.nhlsheetmanager.repositories.SheetsRepository
import com.example.nhlsheetmanager.ui.theme.MyAppTheme
import com.example.nhlsheetmanager.viewModels.NhlUpdater
import com.example.nhlsheetmanager.viewModels.NhlViewModel
import com.example.nhlsheetmanager.viewModels.PreviewMockViewModel


class MainActivity : AppCompatActivity() {
    private val TAG = this::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)

        val nhlRepository = NhlRepository()
        val sheetsRepository = SheetsRepository(resources)
        val nhlViewModel = NhlViewModel(nhlRepository, sheetsRepository)

        setContent {
            MyAppTheme {
                Surface(
                    //modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(nhlViewModel)
                }
            }
        }
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    return connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }?: run {
        false
    }
}

@Composable
fun MainScreen(nhlViewModel: NhlUpdater) {
    val context = LocalContext.current
    val updatedPlayers by nhlViewModel.updatedPlayers.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    // Making the log lines
    val playersLines = remember(updatedPlayers) {
        val result = mutableListOf<String>()

        if (updatedPlayers.isNotEmpty()) {
            var owner = ""
            var points = 0

            updatedPlayers.forEach { player ->
                if (player.ownerName != owner) {
                    if (owner != "") {
                        val pointsText = if (points == 0) {
                            "No points added  :("
                        } else if (points > 0) {
                            "Total points added: $points"
                        } else {
                            "Error in calculating"
                        }

                        points = 0

                        // Make it as a line in the column
                        result.add(pointsText)
                    }

                    owner = player.ownerName

                    // Make it as a line in the column
                    result.add("------------ $owner ------------")
                }

                var text = "${player.playerName}: ${player.playerPreviousPoints}"

                if (player.playerUpdatedPoints > player.playerPreviousPoints) {
                    text += " -> ${player.playerUpdatedPoints}"
                    points += (player.playerUpdatedPoints - player.playerPreviousPoints)
                }

                // Make it as a line in the column
                result.add(text)
            }

            // Print conclusion for the last owner
            val pointsText = if (points == 0) {
                "No points added  :("
            } else if (points > 0) {
                "Total points added: $points"
            } else {
                "Error in calculating"
            }

            // Make it as a line in the column
            result.add(pointsText)

            // Announce end of update
            isLoading = false
        }

        result
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(6.dp)
    ) {
        // Scrollable area takes up 50% of the screen
        val halfScreen = LocalConfiguration.current.screenHeightDp.dp * 0.5f

        Column(
            modifier = Modifier
                .height(halfScreen)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            playersLines.forEach { line ->
                Text(
                    text = line,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Box(
            modifier = Modifier
                .height(halfScreen)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f),
                onClick = {
                    if (isNetworkAvailable(context)) {
                        playersLines.clear()
                        isLoading = true
                        nhlViewModel.updatePlayers()
                    } else {
                        Toast.makeText(context, "No network connection", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Updating.." else "Update Data")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUpdateButton() {
    MainScreen(PreviewMockViewModel())
}