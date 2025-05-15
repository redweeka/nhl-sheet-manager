package com.example.nhlsheetmanager.data

class Player(val playerId: String,
             val playerName: String,
             val ownerName: String,
             val row: Int,
             val playerPreviousPoints: Int,
             var playerUpdatedPoints: Int)