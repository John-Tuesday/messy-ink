package org.calamarfederal.messyink.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.calamarfederal.messyink.data.MessyInkDb
import javax.inject.Singleton

/**
 * # App Level Test Module
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [MessyInkTestModule::class]
)
class MessyInkTestModule {
    @Provides
    @Singleton
    fun providesTestDbInMemory(app: Application): MessyInkDb = Room
        .inMemoryDatabaseBuilder(app, MessyInkDb::class.java)
        .build()
}
