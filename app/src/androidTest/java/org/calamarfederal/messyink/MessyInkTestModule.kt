package org.calamarfederal.messyink

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.calamarfederal.messyink.common.navigation.SubNavOwner
import org.calamarfederal.messyink.data.MessyInkDb
import org.calamarfederal.messyink.navigation.MessyInkNavHost
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
