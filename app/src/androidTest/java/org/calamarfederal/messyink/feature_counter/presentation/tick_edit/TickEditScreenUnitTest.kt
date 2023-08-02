package org.calamarfederal.messyink.feature_counter.presentation.tick_edit

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.calamarfederal.messyink.common.presentation.compose.LocalTimeZone
import org.calamarfederal.messyink.common.presentation.format.DateTimeFormat
import org.calamarfederal.messyink.common.presentation.format.formatToString
import org.calamarfederal.messyink.feature_counter.data.model.Tick
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
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var editTickState: MutableEditTickUiState
    private lateinit var editAmountHelpState: MutableState<TickAmountHelp>
    private lateinit var startTick: Tick
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
        editAmountHelpState = mutableStateOf(TickAmountHelp.NoHelp)

        val testDate = LocalDateTime(
            year = 2020,
            monthNumber = 5,
            dayOfMonth = 7,
            hour = 8,
            minute = 22,
            second = 29
        ).toInstant(timeZone)
        startTick = Tick(
            amount = 1.23,
            timeForData = testDate + 1.days,
            timeModified = testDate,
            timeCreated = testDate,
            parentId = 1L,
            id = 2L,
        )
        editTickState =
            mutableEditTickUiStateOf(startTick, MutableEditTickUiState { editAmountHelpState })

        composeRule.setContent {
            CompositionLocalProvider(LocalTimeZone provides timeZone) {

                EditTickScreen(
                    editTickState = editTickState,
                    onChangeAmount = { editTickState.amountInput = it },
                    onDone = { onDoneCalled.value = true },
                    onClose = {},
                )
            }
        }
    }

    @Test
    fun `Submit button is disabled when error and enabled when not`() {
        editAmountHelpState.value = TickAmountHelp.BlankHelp
        onSubmitButton.assertIsNotEnabled()

        editAmountHelpState.value = TickAmountHelp.NoHelp
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
    fun `Amount field is focused and highlighting all in text field on start`() {
        val selected = onAmountField
            .assertIsFocused()
            .fetchSemanticsNode()
            .config[SemanticsProperties.TextSelectionRange]
        val expected = TextRange(0, editTickState.amountInput.text.length)
        assert(expected.length > 0)
        assert(selected.start == expected.start)
        assert(selected.end == expected.end)
    }

    @Test
    fun `Amount field receives ime actions correctly`() {
        val expect = "othertest"
        onAmountField.performTextClearance()
        onAmountField.performTextInput(expect)
        onAmountField.assertTextContains(expect)
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

        editAmountHelpState.value = TickAmountHelp.BlankHelp

        onDoneCalled.value = false
        onAmountField.performImeAction()
        assert(!onDoneCalled.value)
        onSubmitButton.assertIsNotEnabled()
    }

    @Test
    fun `Amount and Time Fields are initialized`() {
        onAmountField.assertTextContains(editTickState.amountInput.text)
        onTimeForDataNode.assertTextContains(
            editTickState
                .timeForData
                .toLocalDateTime(timeZone)
                .formatToString(dateTimeFormat),
        )
        onTimeModifiedNode.assertTextContains(
            editTickState
                .timeModified
                .toLocalDateTime(timeZone)
                .formatToString(dateTimeFormat),
        )
        onTimeCreatedNode.assertTextContains(
            editTickState
                .timeCreated
                .toLocalDateTime(timeZone)
                .formatToString(dateTimeFormat),
        )
    }

    @Test
    fun `Amount error and help text are shown`() {
        editAmountHelpState.value = TickAmountHelp.BlankHelp
        val helpText = editAmountHelpState.value.getHelp(composeRule.activity)!!
        onAmountField.assertTextContains(helpText)
        onAmountField.assertHasError()
    }

    @Test
    fun `Time For Data error and help text are shown`() {
        editTickState.timeForDataHelpState = TickTimeHelp.InvalidHelp
        val helpText = TickTimeHelp.InvalidHelp.getHelp(composeRule.activity)!!
        onTimeForDataNode.assertTextContains(helpText)
        onTimeForDataNode.assertHasError()
    }

    @Test
    fun `Time Modified error and help text are shown`() {
        editTickState.timeModifiedHelpState = TickTimeHelp.InvalidHelp
        val helpText = TickTimeHelp.InvalidHelp.getHelp(composeRule.activity)!!
        onTimeModifiedNode.assertTextContains(helpText)
        onTimeModifiedNode.assertHasError()
    }

    @Test
    fun `Time Created error and help text are shown`() {
        editTickState.timeCreatedHelpState = TickTimeHelp.InvalidHelp
        val helpText = TickTimeHelp.InvalidHelp.getHelp(composeRule.activity)!!
        onTimeCreatedNode.assertTextContains(helpText)
        onTimeCreatedNode.assertHasError()
    }
}
