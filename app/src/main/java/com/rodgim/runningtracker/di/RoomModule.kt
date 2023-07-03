package com.rodgim.runningtracker.di

import android.content.Context
import androidx.room.Room
import com.rodgim.runningtracker.data.db.RunDao
import com.rodgim.runningtracker.data.db.RunningDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

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
}
