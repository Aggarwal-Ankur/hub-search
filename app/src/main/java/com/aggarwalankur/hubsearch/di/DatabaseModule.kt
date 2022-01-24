package com.aggarwalankur.hubsearch.di

import android.content.Context
import androidx.room.Room
import com.aggarwalankur.hubsearch.data.local.UserDao
import com.aggarwalankur.hubsearch.data.local.UsersDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): UsersDatabase {
        return Room
            .databaseBuilder(appContext, UsersDatabase::class.java, UsersDatabase.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideUserDaoFromDatabase(usersDatabase : UsersDatabase) : UserDao {
        return usersDatabase.usersDao()
    }
}