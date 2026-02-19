package com.example.nhlsheetmanager.repositories

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class NhlRepository {
    private val TAG = this::class.simpleName

    // Function to get raw player data
    private fun getPlayerById(playerId: String): JsonObject? {
        var playerJson: JsonObject? = null
        val url = "https://api-web.nhle.com/v1/player/$playerId/landing"

        val request = Request.Builder()
            .url(url)
            .build()

        try {
            OkHttpClient().newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()

                    playerJson = responseBody?.let {
                        Gson().fromJson(it, JsonObject::class.java)
                    }
                } else {
                    Log.e(TAG, "getPlayerById: Failed to retrieve data. Status code: ${response.code}")
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "getPlayerById: Network error: ", e)
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "getPlayerById: JSON parsing error: ", e)
        }

        return playerJson
    }

    private fun getCurrentSeason(): Long? {
        var currentSeason: Long? = null
        val url = "https://api-web.nhle.com/v1/season"

        val request = Request.Builder()
            .url(url)
            .build()

        try {
            OkHttpClient().newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()

                    currentSeason = responseBody?.let {
                        Gson().fromJson(it, Array<Long>::class.java)
                    }?.lastOrNull()
                } else {
                    Log.e(TAG, "getCurrentSeason: Failed to retrieve data. Status code: ${response.code}")
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "getCurrentSeason: Network error: ", e)
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "getCurrentSeason: JSON parsing error: ", e)
        }

        return currentSeason
    }

    // Extract and return player points from nhl player json
    fun getPlayerCurrentSeasonPointsById(playerId: String): Int {
        var playerPoints = 0

        val jsonData = getPlayerById(playerId)

        if (jsonData != null) {
            val featuredStats = jsonData.getAsJsonObject("featuredStats")

            if (featuredStats != null) {
                val currentSeason = getCurrentSeason()
                val season = featuredStats.get("season").asLong
                val regularSeason = featuredStats.getAsJsonObject("regularSeason")
                val playoffs = featuredStats.getAsJsonObject("playoffs")

                val isCurrentSeason = if (currentSeason != null) {
                    season == currentSeason
                } else {
                    true
                }

                if (isCurrentSeason) {
                    if (regularSeason != null) {
                        val subSeason = regularSeason.getAsJsonObject("subSeason")

                        if (subSeason != null) {
                            subSeason.get("points")?.let { points ->
                                playerPoints = points.asInt
                            }
                        } else {
                            Log.e(TAG, "getPlayerCurrentSeasonPointsById: The 'subSeason' field does not exist in the regularSeason JSON data.")
                        }
                    } else {
                        Log.e(TAG, "getPlayerCurrentSeasonPointsById: The 'regularSeason' field does not exist in the JSON data.")
                    }

                    if (playoffs != null) {
                        val subSeason = playoffs.getAsJsonObject("subSeason")

                        if (subSeason != null) {
                            subSeason.get("points")?.let { points ->
                                playerPoints += points.asInt
                            }
                        } else {
                            Log.e(TAG, "getPlayerCurrentSeasonPointsById: The 'subSeason' field does not exist in the playoffs JSON data.")
                        }
                    } else {
                        Log.e(TAG, "getPlayerCurrentSeasonPointsById: The 'playoffs' field does not exist in the JSON data.")
                    }
                } else {
                    Log.e(TAG, "getPlayerCurrentSeasonPointsById: The player has not participated in this season")
                }
            } else {
                Log.e(TAG, "getPlayerCurrentSeasonPointsById: The 'featuredStats' field does not exist in the JSON data.")
            }
        } else {
            Log.e(TAG, "getPlayerCurrentSeasonPointsById: Player data not found")
        }

        return playerPoints
    }
}
