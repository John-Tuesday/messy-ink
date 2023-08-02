package org.calamarfederal.messyink.feature_counter.di

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.calamarfederal.messyink.feature_counter.data.repository.CounterRepository
import org.calamarfederal.messyink.feature_counter.data.repository.CounterRepositoryImpl
import org.calamarfederal.messyink.feature_counter.data.repository.CounterTickRepository
import org.calamarfederal.messyink.feature_counter.data.repository.CounterTickRepositoryImpl
import org.calamarfederal.messyink.feature_counter.data.repository.TickRepository
import org.calamarfederal.messyink.feature_counter.data.repository.TickRepositoryImpl
import org.calamarfederal.messyink.feature_counter.data.repository.di.CounterModule
import org.calamarfederal.messyink.feature_counter.data.repository.di.CounterTickModule
import org.calamarfederal.messyink.feature_counter.data.repository.di.TickModule
import javax.inject.Singleton

//@Module
//@TestInstallIn(
//    components = [SingletonComponent::class],
//    replaces = [CounterModule::class],
//)
//abstract class CounterTestModule {
//    @Binds
//    abstract fun counterRepo(impl: CounterRepositoryImpl): CounterRepository
//
//}
//
//@Module
//@TestInstallIn(
//    components = [SingletonComponent::class],
//    replaces = [TickModule::class],
//)
//abstract class TickTestModule {
//    @Binds
//    abstract fun tickRepo(impl: TickRepositoryImpl): TickRepository
//}
//
//@Module
//@TestInstallIn(
//    components = [SingletonComponent::class],
//    replaces = [CounterTickModule::class],
//)
//abstract class CounterTickTestModule {
//    @Binds
//    abstract fun counterTickRepo(impl: CounterTickRepositoryImpl): CounterTickRepository
//}
