package com.travelplanner.irida.di

import com.travelplanner.irida.data.repository.AuthRepositoryImpl
import com.travelplanner.irida.data.repository.TripRepositoryImpl
import com.travelplanner.irida.data.repository.UserRepositoryImpl
import com.travelplanner.irida.domain.AuthRepository
import com.travelplanner.irida.domain.TripRepository
import com.travelplanner.irida.domain.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindTripRepository(impl: TripRepositoryImpl): TripRepository

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
