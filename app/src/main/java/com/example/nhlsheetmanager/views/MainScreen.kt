package com.example.nhlsheetmanager.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nhlsheetmanager.models.Player
import com.example.nhlsheetmanager.models.UpdateState

@Composable
fun MainScreen(
    logs: List<String> = emptyList(),
    updateState: UpdateState = UpdateState.UP_TO_DATE,
    onUpdateClick: () -> Unit = {}
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(6.dp)) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            items(logs.reversed()) { line ->
                Text(
                    text = line,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f),
                onClick = onUpdateClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                enabled = updateState == UpdateState.UP_TO_DATE
            ) {
                Text(
                    when (updateState) {
                        UpdateState.FETCHING_PLAYERS -> "Getting all players.."
                        UpdateState.FETCHING_PLAYERS_POINTS -> "Getting updated players points.."
                        UpdateState.UPDATING_PLAYERS_TO_SHEET -> "Writing players to sheet.."
                        UpdateState.UP_TO_DATE -> "UPDATE"
                    }
                )
            }
        }
    }
}

fun convertPlayersToLogsList(players: List<Player>): MutableList<String> {
    val displayTextLines = mutableListOf<String>()

    if (players.isNotEmpty()) {
        var owner = ""
        var ownerAddedPoints = 0

        players.forEach { player ->
            if (player.ownerName != owner) {
                if (owner != "") {
                    val pointsText = if (ownerAddedPoints == 0) {
                        "No points added  :("
                    } else if (ownerAddedPoints > 0) {
                        "Total points added: $ownerAddedPoints"
                    } else {
                        "Error in calculating"
                    }

                    ownerAddedPoints = 0

                    // Make it as a line in the column
                    displayTextLines.add(pointsText)
                }

                owner = player.ownerName

                // Make it as a line in the column
                displayTextLines.add("------------ $owner ------------")
            }

            var text = "${player.playerName}: ${player.playerPreviousPoints}"

            if (player.playerUpdatedPoints > player.playerPreviousPoints) {
                text += " -> ${player.playerUpdatedPoints}"
                ownerAddedPoints += (player.playerUpdatedPoints - player.playerPreviousPoints)
            }

            // Make it as a line in the column
            displayTextLines.add(text)
        }

        // Print conclusion for the last owner
        val pointsText = if (ownerAddedPoints == 0) {
            "No points added  :("
        } else if (ownerAddedPoints > 0) {
            "Total points added: $ownerAddedPoints"
        } else {
            "Error in calculating"
        }

        // Make it as a line in the column
        displayTextLines.add(pointsText)
    }

    return displayTextLines
}

@Preview(showBackground = true)
@Composable
fun PreviewNhlSheetManagerApp() {
    MainScreen(
        logs = convertPlayersToLogsList(
            listOf(
                Player("1", "Connor McDavid", "redweek", 1236, 80, 80),
                Player("1247", "Connor Hyde", "redweek", 1237, 1242, 1244),
                Player("1248", "Connor Dale", "redweek1", 1239, 1243, 1246),
                Player("1245", "Connor Bedard", "redweek1", 1238, 90, 90),
                Player("1250", "Connor Bale", "redweek3", 1240, 100, 100),
                Player("1251", "Leon Draisaitl", "redweek3", 1241, 1254, 1254)
            )
        )
    )
}
