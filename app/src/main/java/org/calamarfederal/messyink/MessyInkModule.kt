package org.calamarfederal.messyink

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.calamarfederal.messyink.data.MessyInkDb
import javax.inject.Singleton

/**
 * # Application-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object MessyInkModule {
    /**
     * Build Room database, scoped to the application
     */
    @Provides
    @Singleton
    fun provideMessyInkDb(app: Application): MessyInkDb =
        Room.databaseBuilder(context = app, klass = MessyInkDb::class.java, name = "messy-ink-database")
            .fallbackToDestructiveMigration().build()
}
