package com.example.nhlsheetmanager.viewModels

import com.example.nhlsheetmanager.data.Player
import com.example.nhlsheetmanager.data.UpdateState
import kotlinx.coroutines.flow.StateFlow

interface NhlUpdater {
    val updatedPlayers: StateFlow<List<Player>>
    val updatedState: StateFlow<UpdateState>
    fun updatePlayers()
}