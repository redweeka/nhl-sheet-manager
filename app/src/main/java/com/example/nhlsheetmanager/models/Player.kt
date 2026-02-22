package com.example.nhlsheetmanager.models

class Player(val playerId: String,
             val playerName: String,
             val ownerName: String,
             val row: Int,
             val playerPreviousPoints: Int,
             var playerUpdatedPoints: Int)