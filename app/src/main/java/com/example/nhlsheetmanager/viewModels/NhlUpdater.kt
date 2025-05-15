package com.example.nhlsheetmanager.viewModels

import com.example.nhlsheetmanager.data.Player
import kotlinx.coroutines.flow.StateFlow

interface NhlUpdater {
    val updatedPlayers: StateFlow<List<Player>>
    fun updatePlayers()
}