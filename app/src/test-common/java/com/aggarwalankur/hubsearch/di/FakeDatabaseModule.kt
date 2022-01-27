package com.aggarwalankur.hubsearch.di

import android.content.Context
import androidx.room.Room
import com.aggarwalankur.hubsearch.data.local.UserDao
import com.aggarwalankur.hubsearch.data.local.UsersDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton


@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object FakeDatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context) =
        Room.inMemoryDatabaseBuilder(appContext, UsersDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    @Singleton
    @Provides
    fun provideUserDaoFromDatabase(usersDatabase : UsersDatabase) : UserDao {
        return usersDatabase.usersDao()
    }
}
