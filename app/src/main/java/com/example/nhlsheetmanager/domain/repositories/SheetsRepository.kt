package com.example.nhlsheetmanager.domain.repositories

import com.example.nhlsheetmanager.domain.repositories.repositoriesInterfaces.SheetsRepositoryInterface
import com.example.nhlsheetmanager.models.COLUMNS_SUM
import com.example.nhlsheetmanager.models.END_DATA_COLUMN_INDEX
import com.example.nhlsheetmanager.models.END_DATA_ROW_INDEX
import com.example.nhlsheetmanager.models.POINTS_COLUMN_INDEX
import com.example.nhlsheetmanager.models.Player
import com.example.nhlsheetmanager.models.SPREADSHEET_ID
import com.example.nhlsheetmanager.models.START_DATA_COLUMN_INDEX
import com.example.nhlsheetmanager.models.START_DATA_ROW_INDEX
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SheetsRepository @Inject constructor(
    private val sheetsService: Sheets
) : SheetsRepositoryInterface {
    private val TAG = this::class.simpleName

    override fun getPlayers(): List<Player> {
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
                val playerPreviousPoints = try {
                    rowValues[2].toString().toInt()
                } catch (e: NumberFormatException) {
                    0
                }

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

    override fun updateRemotePlayerPoints(row: Int, playerUpdatedPoints: Int) {
        val sheetsDataStructure = listOf(listOf(playerUpdatedPoints))
        val valueRange = ValueRange().setValues(sheetsDataStructure)

        sheetsService.spreadsheets().values().update(
            SPREADSHEET_ID,
            "sheet1!$POINTS_COLUMN_INDEX$row",
            valueRange
        ).setValueInputOption("USER_ENTERED").execute()
    }
}
