package com.example.nhlsheetmanager.repositories

import android.content.res.Resources
import com.example.nhlsheetmanager.R
import com.example.nhlsheetmanager.data.Player
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import java.io.InputStream

class SheetsRepository(val resources: Resources) {
    private val TAG = this::class.simpleName

    private val SCOPES = listOf("https://www.googleapis.com/auth/spreadsheets")
    private val SPREADSHEET_ID = "17iSalI1ZMa_aJQ68Q0ZNJmBqL_cMfrrooLHnRLBDuDY"
    private val START_DATA_ROW_INDEX = 2
    private val END_DATA_ROW_INDEX = 50
    private val START_DATA_COLUMN_INDEX = "A"
    private val END_DATA_COLUMN_INDEX = "D"
    private val COLUMNS_SUM = 4
    private val POINTS_COLUMN_INDEX = "C"

    val sheetsService = createSheetsService()

    fun getPlayers(): List<Player> {
        val players = mutableListOf<Player>()
        var ownerName = ""

        for (row in START_DATA_ROW_INDEX..(END_DATA_ROW_INDEX+1)) {
            val range = "sheet1!$START_DATA_COLUMN_INDEX$row:$END_DATA_COLUMN_INDEX$row"
            val query = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, range)
            val rawFullData = query.execute().getValues()

            // All columns filled with values means that this is a player row (got ID)
            if (rawFullData != null && rawFullData.isNotEmpty() && rawFullData[0].size == COLUMNS_SUM) {
                val rowValues = rawFullData[0]

                // B column is the player name
                val playerName = rowValues[1].toString()

                // C column is the player points
                val playerPreviousPoints = rowValues[2].toString().toInt()

                // D column is the player id
                val playerId = rowValues[3].toString()

                players.add(Player(playerId, playerName, ownerName, row, playerPreviousPoints, playerPreviousPoints))
            } else if (rawFullData != null && rawFullData[0] != null && rawFullData[0].size >= 2) {
                val rowValues = rawFullData[0]
                ownerName = rowValues[1].toString()
            }
        }

        return players
    }

    fun updatePlayers(players: List<Player>) {
        players.forEach { player ->
            if (player.playerUpdatedPoints > player.playerPreviousPoints) {
                // Wrap the data so it feat the sheet convention. i.e. [[data]]
                val valueRange = ValueRange().setValues(listOf(listOf(player.playerUpdatedPoints)))

                sheetsService.spreadsheets().values().update(
                    SPREADSHEET_ID,
                    "sheet1!$POINTS_COLUMN_INDEX${player.row}",
                    valueRange
                ).setValueInputOption("USER_ENTERED").execute()
            }
        }
    }

    private fun loadGoogleCredentials(): GoogleCredentials {
        val inputStream: InputStream = resources.openRawResource(R.raw.credentials)

        return GoogleCredentials.fromStream(inputStream).createScoped(SCOPES)
    }

    private fun createSheetsService(): Sheets {
        val googleCredentials = loadGoogleCredentials()
        val transport = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()

        // Wrap GoogleCredentials in HttpCredentialsAdapter
        val requestInitializer: HttpRequestInitializer = HttpCredentialsAdapter(googleCredentials)

        return Sheets.Builder(transport, jsonFactory, requestInitializer)
            .setApplicationName("Google Sheets API Kotlin")
            .build()
    }
}