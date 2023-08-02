package org.calamarfederal.messyink.feature_counter.presentation

import org.calamarfederal.messyink.feature_counter.presentation.counter_history.CounterHistoryIntegrationTest
import org.calamarfederal.messyink.feature_counter.presentation.counter_history.CounterHistoryScreenTest
import org.calamarfederal.messyink.feature_counter.presentation.counter_overview.CounterOverviewIntegrationTest
import org.calamarfederal.messyink.feature_counter.presentation.counter_overview.CounterOverviewScreenTest
import org.calamarfederal.messyink.feature_counter.presentation.create_counter.CreateCounterScreenIntegrationTest
import org.calamarfederal.messyink.feature_counter.presentation.create_counter.CreateCounterScreenTest
import org.calamarfederal.messyink.feature_counter.presentation.game_counter.GameCounterScreenIntegrationTest
import org.calamarfederal.messyink.feature_counter.presentation.game_counter.GameCounterScreenUnitTest
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterFeatureNavigationTests
import org.calamarfederal.messyink.feature_counter.presentation.tick_edit.TickEditScreenUnitTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(
    PresentationUnitTestSuite::class,
    PresentationIntegrationTestSuite::class,
)
class PresentationTestSuite

@RunWith(Suite::class)
@SuiteClasses(
    CounterHistoryIntegrationTest::class,
    CounterOverviewIntegrationTest::class,
    CreateCounterScreenIntegrationTest::class,
    GameCounterScreenIntegrationTest::class,
)
class PresentationIntegrationTestSuite

@RunWith(Suite::class)
@SuiteClasses(
    TickEditScreenUnitTest::class,
    CounterHistoryScreenTest::class,
    CounterOverviewScreenTest::class,
    CreateCounterScreenTest::class,
    CounterFeatureNavigationTests::class,
    GameCounterScreenUnitTest::class,
)
class PresentationUnitTestSuite
