package com.example.nhlsheetmanager.domain.repositories.repositoriesInterfaces

interface NhlRepositoryInterface {
    fun getPlayerCurrentSeasonPointsById(playerId: String): Int
}