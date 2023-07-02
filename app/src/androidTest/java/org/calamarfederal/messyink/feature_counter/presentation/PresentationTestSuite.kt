package org.calamarfederal.messyink.feature_counter.presentation

import org.calamarfederal.messyink.feature_counter.presentation.counter_history.CounterHistoryScreenTest
import org.calamarfederal.messyink.feature_counter.presentation.counter_overview.CounterOverviewScreenTest
import org.calamarfederal.messyink.feature_counter.presentation.create_counter.CreateCounterScreenTest
import org.calamarfederal.messyink.feature_counter.presentation.game_counter.GameCounterScreenTest
import org.calamarfederal.messyink.feature_counter.presentation.navigation.CounterFeatureNavigationTests
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(
    CounterHistoryScreenTest::class,
    CounterOverviewScreenTest::class,
    CreateCounterScreenTest::class,
    CounterFeatureNavigationTests::class,
    GameCounterScreenTest::class,
)
class PresentationTestSuite
