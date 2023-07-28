package org.calamarfederal.messyink.feature_counter.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.BINARY

/**
 * Dispatcher for disk or network I/O
 *
 * for example: use with Room
 */
@Qualifier
@Retention(BINARY)
annotation class IODispatcher

/**
 * Dispatcher for CPU intensive work, off the Main (ui) thread
 */
@Qualifier
@Retention(BINARY)
annotation class DefaultDispatcher

/**
 * Provider for each Counter-feature ViewModel
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatcherProviderModule {

    /**
     * Dispatcher for disk or network I/O
     *
     * for example: use with Room
     */
    @Provides
    @IODispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Dispatcher for CPU intensive work, off the Main (ui) thread
     */
    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
