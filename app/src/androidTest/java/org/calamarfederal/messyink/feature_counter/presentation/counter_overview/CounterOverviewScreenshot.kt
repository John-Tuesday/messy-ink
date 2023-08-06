package org.calamarfederal.messyink.feature_counter.presentation.counter_overview

import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.calamarfederal.messyink.MainActivity
import org.calamarfederal.messyink.feature_counter.data.source.database.CounterTickDao
import org.calamarfederal.messyink.feature_counter.data.insertPrettyData
import org.calamarfederal.messyink.saveScreenshot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class CounterOverviewScreenshot {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var dao: CounterTickDao

    private val screenshotFilename: String = "overview.png"

    @Before
    fun setUp() {
        hiltRule.inject()

        runBlocking { dao.insertPrettyData(composeRule.activity.resources) }
    }

    @Test
    fun `Screenshot Overview Screen`() {
        val img = composeRule.onRoot().captureToImage()
        saveScreenshot(screenshotFilename, img.asAndroidBitmap())
    }
}
