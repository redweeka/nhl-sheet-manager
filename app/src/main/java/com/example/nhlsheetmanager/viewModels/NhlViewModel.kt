package com.example.nhlsheetmanager.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhlsheetmanager.data.Player
import com.example.nhlsheetmanager.repositories.NhlRepository
import com.example.nhlsheetmanager.repositories.SheetsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NhlViewModel(private val nhlRepository: NhlRepository, private val sheetsRepository: SheetsRepository) : ViewModel(), NhlUpdater {
    private val TAG = this::class.simpleName
    private val _updatedPlayers = MutableStateFlow<List<Player>>(emptyList())
    override val updatedPlayers: StateFlow<List<Player>> get() = _updatedPlayers

    override fun updatePlayers() {
        viewModelScope.launch(Dispatchers.IO) {
            // Get players list from sheets
            val players = sheetsRepository.getPlayers()

            // Get players updated points
            players.forEach { player ->
                player.playerUpdatedPoints = nhlRepository.getPlayerPointsById(player.playerId)
            }

            launch {
                // Update players in sheets
                sheetsRepository.updatePlayers(players)
            }

            // Update local players list
            _updatedPlayers.value = players
        }
    }
}