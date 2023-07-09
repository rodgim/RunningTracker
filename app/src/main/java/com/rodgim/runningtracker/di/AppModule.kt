package com.rodgim.runningtracker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.rodgim.runningtracker.data.datasources.LocalMainDataSource
import com.rodgim.runningtracker.data.datasources.LocalSetupDataSource
import com.rodgim.runningtracker.data.datasources.RoomLocalMainDataSource
import com.rodgim.runningtracker.data.datasources.SharedPreferencesLocalSetupDataSource
import com.rodgim.runningtracker.data.db.RunDao
import com.rodgim.runningtracker.data.db.RunningDatabase
import com.rodgim.runningtracker.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        RunningDatabase::class.java,
        Constants.RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(
        runningDatabase: RunningDatabase
    ): RunDao = runningDatabase.getRunDao()

    @Singleton
    @Provides
    fun provideLocalMainDataSource(runDao: RunDao): LocalMainDataSource = RoomLocalMainDataSource(runDao)

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideLocalSetupDataSource(
        sharedPreferences: SharedPreferences
    ): LocalSetupDataSource = SharedPreferencesLocalSetupDataSource(sharedPreferences)
}
