package com.example.nhlsheetmanager.di

import android.content.Context
import android.content.res.Resources
import com.example.nhlsheetmanager.R
import com.example.nhlsheetmanager.models.SPREADSHEET_SCOPE
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleModule {
    @Provides
    @Singleton
    fun provideSheetsService(@ApplicationContext context: Context): Sheets {
        val transport = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()
        val googleCredentials = loadGoogleCredentials(context.resources)
        val requestInitializer = HttpCredentialsAdapter(googleCredentials)

        return Sheets.Builder(transport, jsonFactory, requestInitializer)
            .setApplicationName("Google Sheets API Kotlin")
            .build()
    }

    private fun loadGoogleCredentials(resources: Resources): GoogleCredentials {
        val inputStream = resources.openRawResource(R.raw.credentials)

        return GoogleCredentials.fromStream(inputStream).createScoped(listOf(SPREADSHEET_SCOPE))
    }
}