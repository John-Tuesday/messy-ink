package org.calamarfederal.messyink

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.calamarfederal.messyink.feature_counter.data.source.database.MessyInkDb
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [MessyInkModule::class]
)
object MessyInkTestModule {
    @Provides
    @Singleton
    fun provideMessyInkDb(app: Application): MessyInkDb = Room
        .inMemoryDatabaseBuilder(app, MessyInkDb::class.java)
        .build()
}
