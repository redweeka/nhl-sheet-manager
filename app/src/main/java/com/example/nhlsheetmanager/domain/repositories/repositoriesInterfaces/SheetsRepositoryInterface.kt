package com.example.nhlsheetmanager.domain.repositories.repositoriesInterfaces

import com.example.nhlsheetmanager.models.Player

interface SheetsRepositoryInterface {
    fun getPlayers(): List<Player>

    fun updateRemotePlayerPoints(row: Int, playerUpdatedPoints: Int)
}