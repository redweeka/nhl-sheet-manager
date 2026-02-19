package com.example.nhlsheetmanager.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhlsheetmanager.data.Player
import com.example.nhlsheetmanager.data.UpdateState
import com.example.nhlsheetmanager.repositories.NhlRepository
import com.example.nhlsheetmanager.repositories.SheetsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NhlViewModel(private val nhlRepository: NhlRepository, private val sheetsRepository: SheetsRepository) : ViewModel(), NhlUpdater {
    private val TAG = this::class.simpleName

    private val _updatedPlayers = MutableStateFlow<MutableList<Player>>(mutableListOf())
    override val updatedPlayers: StateFlow<List<Player>> get() = _updatedPlayers

    private val _updateState = MutableStateFlow(UpdateState.UP_TO_DATE)
    override val updatedState: StateFlow<UpdateState> get() = _updateState

    override fun updatePlayers() {
        viewModelScope.launch(Dispatchers.IO) {
            _updateState.value = UpdateState.FETCHING_PLAYERS
            val notUpdatedPlayers = sheetsRepository.getPlayers()

            _updateState.value = UpdateState.FETCHING_PLAYERS_POINTS
            updatePlayersOneByOne(notUpdatedPlayers)

            _updateState.value = UpdateState.UPDATING_PLAYERS_TO_SHEET
            sheetsRepository.updateRemotePlayers(updatedPlayers.value)

            _updateState.value = UpdateState.UP_TO_DATE
        }
    }

    private fun updatePlayersOneByOne(notUpdatedPlayers: List<Player>) {
        notUpdatedPlayers.forEach { player ->
            player.playerUpdatedPoints = nhlRepository.getPlayerCurrentSeasonPointsById(player.playerId)

            _updatedPlayers.value = _updatedPlayers.value.toMutableList().apply {
                add(player)
            }
        }
    }
}