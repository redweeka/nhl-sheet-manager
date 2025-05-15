package com.example.nhlsheetmanager.viewModels

import com.example.nhlsheetmanager.data.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreviewMockViewModel : NhlUpdater {
    private val _fakePlayers = MutableStateFlow(
        listOf(
            Player("1", "Connor McDavid", "redweek", 1236, 80, 80),
            Player("1247", "Connor Hyde", "redweek", 1237, 1242, 1244),
            Player("1245", "Connor Bedard", "redweek1", 1238, 90, 90),
            Player("1248", "Connor Dale", "redweek1", 1239, 1243, 1246),
            Player("1250", "Connor Bale", "redweek3", 1240, 100, 100),
            Player("1251", "Leon Draisaitl", "redweek3", 1241, 1254, 1254)
        )
    )

    override val updatedPlayers: StateFlow<List<Player>> = _fakePlayers

    override fun updatePlayers() {
        // No-op or simulate update logic
    }
}