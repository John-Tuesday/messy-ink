package org.calamarfederal.messyink.feature_counter.domain

import org.calamarfederal.messyink.feature_counter.domain.use_case.GetTicksFromTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(
    GetTicksFromTest::class,
)
class UseCaseTestSuite
