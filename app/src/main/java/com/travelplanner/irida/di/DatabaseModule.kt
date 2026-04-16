package com.travelplanner.irida.di

import android.content.Context
import androidx.room.Room
import com.travelplanner.irida.data.PreferencesManager
import com.travelplanner.irida.data.local.IridaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): IridaDatabase =
        Room.databaseBuilder(ctx, IridaDatabase::class.java, "irida_db").build()

    @Provides
    fun provideTripDao(db: IridaDatabase) = db.tripDao()

    @Provides
    fun provideActivityDao(db: IridaDatabase) = db.activityDao()

    @Provides
    fun provideUserDao(db: IridaDatabase) = db.userDao()

    @Provides
    fun provideAccessLogDao(db: IridaDatabase) = db.accessLogDao()

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext ctx: Context) = PreferencesManager(ctx)
}
