package com.example.nhlsheetmanager.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nhlsheetmanager.domain.repositories.repositoriesInterfaces.NhlRepositoryInterface
import com.example.nhlsheetmanager.domain.repositories.repositoriesInterfaces.SheetsRepositoryInterface
import com.example.nhlsheetmanager.models.Player
import com.example.nhlsheetmanager.models.UpdateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NhlViewModel @Inject constructor(
    private val nhlRepository: NhlRepositoryInterface,
    private val sheetsRepository: SheetsRepositoryInterface
) : ViewModel() {
    private val TAG = this::class.simpleName

    private val _updatedPlayers = MutableStateFlow<MutableList<Player>>(mutableListOf())
    val updatedPlayers: StateFlow<List<Player>> get() = _updatedPlayers

    private val _updateState = MutableStateFlow(UpdateState.UP_TO_DATE)
    val updateState: StateFlow<UpdateState> get() = _updateState

    fun updatePlayers() {
        _updateState.value = UpdateState.FETCHING_PLAYERS

        viewModelScope.launch(Dispatchers.IO) {
            val notUpdatedPlayers = sheetsRepository.getPlayers()

            _updateState.value = UpdateState.FETCHING_PLAYERS_POINTS
            updatePlayersOneByOne(notUpdatedPlayers)

            _updateState.value = UpdateState.UPDATING_PLAYERS_TO_SHEET
            sheetsRepository.updateRemotePlayers(updatedPlayers.value)

            _updateState.value = UpdateState.UP_TO_DATE
        }
    }

    private fun updatePlayersOneByOne(notUpdatedPlayers: List<Player>) {
        _updatedPlayers.value = mutableListOf()

        notUpdatedPlayers.forEach { player ->
            player.playerUpdatedPoints =
                nhlRepository.getPlayerCurrentSeasonPointsById(player.playerId)

            _updatedPlayers.value = _updatedPlayers.value.toMutableList().apply {
                add(player)
            }
        }
    }
}
