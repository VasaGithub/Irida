package com.travelplanner.irida.di

import android.content.Context
import androidx.room.Room
import com.travelplanner.irida.data.PreferencesManager
import com.travelplanner.irida.data.local.IridaDatabase
import com.travelplanner.irida.data.local.MIGRATION_1_2
import com.travelplanner.irida.data.local.MIGRATION_2_3
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
        Room.databaseBuilder(ctx, IridaDatabase::class.java, "irida_db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()

    @Provides
    fun provideTripDao(db: IridaDatabase) = db.tripDao()

    @Provides
    fun provideActivityDao(db: IridaDatabase) = db.activityDao()

    @Provides
    fun provideUserDao(db: IridaDatabase) = db.userDao()

    @Provides
    fun provideAccessLogDao(db: IridaDatabase) = db.accessLogDao()

    @Provides
    fun provideTripImageDao(db: IridaDatabase) = db.tripImageDao()

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext ctx: Context) = PreferencesManager(ctx)
}
