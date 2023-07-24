package org.calamarfederal.messyink.feature_counter.presentation.tick_edit

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.printToString
import androidx.compose.ui.text.input.ImeAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.common.presentation.compose.LocalTimeZone
import org.calamarfederal.messyink.common.presentation.format.DateTimeFormat
import org.calamarfederal.messyink.common.presentation.format.formatToString
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTickSupport
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.days

fun SemanticsNodeInteraction.assertHasError() {
    assert(
        fetchSemanticsNode().config.getOrNull(SemanticsProperties.Error) != null
    ) {
        "Could not find Error\n${printToString()}"
    }
}

@RunWith(AndroidJUnit4::class)
class TickEditScreenUnitTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var tickSupportState: MutableStateFlow<UiTickSupport>
    private lateinit var onDoneCalled: MutableStateFlow<Boolean>

    private val timeZone = TimeZone.UTC
    private val dateTimeFormat = DateTimeFormat()

    private val onSubmitButton get() = composeRule.onNodeWithTag(EditTickTestTags.SubmitButton)
    private val onAmountField get() = composeRule.onNodeWithTag(EditTickTestTags.AmountField)
    private val onTimeForDataNode get() = composeRule.onNodeWithTag(EditTickTestTags.TimeForDataField)
    private val onTimeModifiedNode get() = composeRule.onNodeWithTag(EditTickTestTags.TimeModifiedField)
    private val onTimeCreatedNode get() = composeRule.onNodeWithTag(EditTickTestTags.TimeCreatedField)

    @Before
    fun setUp() {
        onDoneCalled = MutableStateFlow(false)

        val parentId = 1L
        tickSupportState = MutableStateFlow(UiTickSupport(parentId = parentId))

        composeRule.setContent {
            val tickSupport by tickSupportState.collectAsState()
            CompositionLocalProvider(LocalTimeZone provides timeZone) {
                EditTickScreen(
                    uiTickSupport = tickSupport,
                    onChangeTick = {},
                    onDone = { onDoneCalled.value = true },
                    onClose = {},
                )
            }
        }
    }

    @Test
    fun `Submit button is disabled when error and enabled when not`() {
        tickSupportState.update { it.copy(amountError = true) }
        onSubmitButton.assertIsNotEnabled()

        tickSupportState.update {
            it.copy(
                amountError = false,
                timeForDataError = false,
                timeModifiedError = false,
                timeCreatedError = false,
            )
        }
        onSubmitButton.assertIsEnabled()
    }

    @Test
    fun `Close button is present and enabled`() {
        composeRule.onNodeWithTag(EditTickTestTags.CloseButton)
            .assertIsEnabled()
            .assertHasClickAction()
    }

    @Test
    fun `Time Fields are clickable`() {
        onTimeForDataNode.assertHasClickAction()
        onTimeModifiedNode.assertHasClickAction()
        onTimeCreatedNode.assertHasClickAction()
    }

    @Test
    fun `Amount field is focused on start`() {
        onAmountField.assertIsFocused()
    }

    @Test
    fun `Amount field ImeAction is Done and acts identical to submit button`() {
        assert(
            ImeAction.Done == onAmountField.fetchSemanticsNode().config[SemanticsProperties.ImeAction]
        )

        onDoneCalled.value = false
        onAmountField.performImeAction()
        assert(onDoneCalled.value)
        onDoneCalled.value = false
        onSubmitButton.performClick()
        assert(onDoneCalled.value)

        tickSupportState.update { it.copy(amountError = true) }

        onDoneCalled.value = false
        onAmountField.performImeAction()
        assert(!onDoneCalled.value)
        onSubmitButton.assertIsNotEnabled()
    }

    @Test
    fun `Amount and Time Fields are initialized`() {
        val testDate = LocalDateTime(
            year = 2020,
            monthNumber = 5,
            dayOfMonth = 7,
            hour = 8,
            minute = 22,
            second = 29
        ).toInstant(timeZone)
        val support = tickSupportState.updateAndGet {
            UiTickSupport(
                amountInput = "abcdef",
                timeForDataInput = testDate + 1.days,
                timeModifiedInput = testDate + 300.days,
                timeCreatedInput = testDate,
                parentId = it.parentId,
            )
        }
        onAmountField.assertTextContains(support.amountInput)
        onTimeForDataNode.assertTextContains(
            support
                .timeForDataInput
                .toLocalDateTime(timeZone)
                .formatToString(dateTimeFormat),
        )
        onTimeModifiedNode.assertTextContains(
            support
                .timeModifiedInput
                .toLocalDateTime(timeZone)
                .formatToString(dateTimeFormat),
        )
        onTimeCreatedNode.assertTextContains(
            support
                .timeCreatedInput
                .toLocalDateTime(timeZone)
                .formatToString(dateTimeFormat),
        )
    }

    @Test
    fun `Amount error and help text are shown`() {
        val helpText = "help help help"
        tickSupportState.update { it.copy(amountError = true, amountHelp = helpText) }
        onAmountField.assertTextContains(helpText)
        onAmountField.assertHasError()
    }

    @Test
    fun `Time For Data error and help text are shown`() {
        val helpText = "help help help"
        tickSupportState.update { it.copy(timeForDataError = true, timeForDataHelp = helpText) }
        onTimeForDataNode.assertTextContains(helpText)
        onTimeForDataNode.assertHasError()
    }

    @Test
    fun `Time Modified error and help text are shown`() {
        val helpText = "help help help"
        tickSupportState.update { it.copy(timeModifiedError = true, timeModifiedHelp = helpText) }
        onTimeModifiedNode.assertTextContains(helpText)
        onTimeModifiedNode.assertHasError()
    }

    @Test
    fun `Time Created error and help text are shown`() {
        val helpText = "help help help"
        tickSupportState.update { it.copy(timeCreatedError = true, timeCreatedHelp = helpText) }
        onTimeCreatedNode.assertTextContains(helpText)
        onTimeCreatedNode.assertHasError()
    }
}
