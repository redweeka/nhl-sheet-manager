package com.example.nhlsheetmanager.di

import com.example.nhlsheetmanager.domain.repositories.NhlRepository
import com.example.nhlsheetmanager.domain.repositories.SheetsRepository
import com.example.nhlsheetmanager.domain.repositories.repositoriesInterfaces.NhlRepositoryInterface
import com.example.nhlsheetmanager.domain.repositories.repositoriesInterfaces.SheetsRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {
    @Binds
    @Singleton
    abstract fun bindNhlRepository(nhlRepository: NhlRepository): NhlRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindSheetsRepository(sheetsRepository: SheetsRepository): SheetsRepositoryInterface
}