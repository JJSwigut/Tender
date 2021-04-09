package com.jjswigut.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jjswigut.data.remote.RemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRestaurantRepository(
        remoteDataSource: RemoteDataSource
    ) = RestaurantRepository(remoteDataSource)

    @Singleton
    @Provides
    fun provideFireStoreRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ) = FirestoreRepository(firestore, firebaseAuth)

    @Provides
    fun provideFireStore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}