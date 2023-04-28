package org.calamarfederal.messyink.feature_counter.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import org.calamarfederal.messyink.data.CounterDao
import org.calamarfederal.messyink.data.MessyInkDb
import org.calamarfederal.messyink.feature_counter.data.CountersRepoImpl
import org.calamarfederal.messyink.feature_counter.domain.CountersRepo
import org.calamarfederal.messyink.feature_counter.domain.CreateCounterFrom
import org.calamarfederal.messyink.feature_counter.domain.CreateTickFrom
import org.calamarfederal.messyink.feature_counter.domain.GetCounterFlow
import org.calamarfederal.messyink.feature_counter.domain.GetCountersFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksAverageFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksOfFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumByFlow
import org.calamarfederal.messyink.feature_counter.domain.GetTicksSumOfFlow
import org.calamarfederal.messyink.feature_counter.domain.use_case.CreateCounterFromImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.CreateTickFromImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetCounterFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetCountersFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksAverageOfFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksOfFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksSumByFlowImpl
import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksSumOfFlowImpl

/**
 * # Test Binds
 * ## View model scoped
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class TestBindsModules {
   /**
    * Binds CountersRepo Implementation to Interface
    */
   @Binds
   @ViewModelScoped
   abstract fun bindCounterRepo(repoImpl: CountersRepoImpl): CountersRepo
}

/**
 * # Test Provides
 * ## View Model scoped
 */
@Module
@InstallIn(ViewModelComponent::class)
object TestProvidesModule {
    /**
     *
     */
    @Provides
    @ViewModelScoped
    fun provideCountersDao(db: MessyInkDb): CounterDao = db.counterDao()

    /**
     * # Use Cases
     */

    @Provides
    @ViewModelScoped
    fun provideGetCounterFlow(repo: CountersRepo): GetCounterFlow = GetCounterFlowImpl(repo)

    @Provides
    @ViewModelScoped
    fun provideGetCountersFlows(repo: CountersRepo): GetCountersFlow = GetCountersFlowImpl(repo)

    @Provides
    @ViewModelScoped
    fun provideGetTicksOfFlows(repo: CountersRepo): GetTicksOfFlow = GetTicksOfFlowImpl(repo)

    @Provides
    @ViewModelScoped
    fun provideCreateCounterFrom(repo: CountersRepo): CreateCounterFrom = CreateCounterFromImpl(repo)

    @Provides
    @ViewModelScoped
    fun provideCreateTickFrom(repo: CountersRepo): CreateTickFrom= CreateTickFromImpl(repo)

    /**
     * ## Calculation
     */

    @Provides
    @ViewModelScoped
    fun provideGetTicksSumOfFlow(repo: CountersRepo): GetTicksSumOfFlow = GetTicksSumOfFlowImpl(repo)

    @Provides
    @ViewModelScoped
    fun provideGetTicksAverageOfFlow(repo: CountersRepo): GetTicksAverageFlow = GetTicksAverageOfFlowImpl(repo)

    @Provides
    @ViewModelScoped
    fun provideGetSumOfTicksByFlow(repo: CountersRepo): GetTicksSumByFlow = GetTicksSumByFlowImpl(repo)
}
