package com.aggarwalankur.hubsearch.di

import com.aggarwalankur.hubsearch.network.FakeGithubSearchService
import com.aggarwalankur.hubsearch.network.GithubSearchService
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class]
)
abstract class TestGihubSearchServiceModule {
    @Singleton
    @Binds
    abstract fun bindSearchService(githubSearchService: FakeGithubSearchService): GithubSearchService
}