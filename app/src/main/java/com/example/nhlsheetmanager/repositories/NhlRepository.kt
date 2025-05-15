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
        var player: JsonObject? = null
        val url = "https://api-web.nhle.com/v1/player/$playerId/landing"

        val request = Request.Builder()
            .url(url)
            .build()

        try {
            OkHttpClient().newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()

                    player = responseBody?.let {
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

        return player
    }

    // Extract and return player points from nhl player json (detailed with debug logs due to nhl unstable apis)
    fun getPlayerPointsById(playerId: String): Int {
        var playerPoints = 0

        val jsonData = getPlayerById(playerId)

        if (jsonData != null) {
            val featuredStats = jsonData.getAsJsonObject("featuredStats")

            if (featuredStats != null) {
                val regularSeason = featuredStats.getAsJsonObject("regularSeason")

                if (regularSeason != null) {
                    val subSeason = regularSeason.getAsJsonObject("subSeason")

                    if (subSeason != null) {
                        subSeason.get("points")?.let { points ->
                            playerPoints = points.asInt
                        }
                    } else {
                        Log.e(TAG, "getPlayerPointsById: The 'subSeason' field does not exist in the regularSeason JSON data.")
                    }
                } else {
                    Log.e(TAG, "getPlayerPointsById: The 'regularSeason' field does not exist in the JSON data.")
                }

                val playoffs = featuredStats.getAsJsonObject("playoffs")

                if (playoffs != null) {
                    val subSeason = playoffs.getAsJsonObject("subSeason")

                    if (subSeason != null) {
                        subSeason.get("points")?.let { points ->
                            playerPoints += points.asInt
                        }
                    } else {
                        Log.e(TAG, "getPlayerPointsById: The 'subSeason' field does not exist in the playoffs JSON data.")
                    }
                } else {
                    Log.e(TAG, "getPlayerPointsById: The 'playoffs' field does not exist in the JSON data.")
                }
            } else {
                Log.e(TAG, "getPlayerPointsById: The 'featuredStats' field does not exist in the JSON data.")
            }
        } else {
            Log.e(TAG, "getPlayerPointsById: Player data not found")
        }

        return playerPoints
    }
}
