package org.calamarfederal.messyink.feature_counter.presentation.create_counter

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * # Create Counter Screen
 * ## Unit Tests
 */
@RunWith(AndroidJUnit4::class)
class CreateCounterScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var counterNameTextState: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue())
    private var counterNameText by counterNameTextState
    private var counterNameErrorState: MutableState<Boolean> = mutableStateOf(false)
    private var counterNameError by counterNameErrorState
    private var counterNameHelpState: MutableState<String?> = mutableStateOf(null)
    private var counterNameHelp by counterNameHelpState
    private lateinit var onCancel: MutableStateFlow<() -> Unit>
    private lateinit var onDone: MutableStateFlow<() -> Unit>

    private val submitButtonNode get() = composeRule.onNodeWithTag(CreateCounterTestTags.SubmitButton)
    private val closeButtonNode get() = composeRule.onNodeWithTag(CreateCounterTestTags.CloseButton)
    private val titleFieldNode get() = composeRule.onNodeWithTag(CreateCounterTestTags.TitleTextField)

    @Before
    fun setUp() {
        counterNameTextState = mutableStateOf(TextFieldValue(text = "Test"))
        counterNameErrorState = mutableStateOf(false)
        counterNameHelpState = mutableStateOf(null)

        onCancel = MutableStateFlow({})
        onDone = MutableStateFlow({})

        composeRule.setContent {
            val cancel by onCancel.collectAsState()
            val done by onDone.collectAsState()

            CreateCounterScreen(
                counterName = counterNameText,
                counterNameError = counterNameError,
                counterNameHelp = counterNameHelp,
                isEditCounter = false,
                onNameChange = {},
                onCancel = cancel,
                onDone = done,
            )
        }
    }

    @Test
    fun `Disable submit button when error`() {
        counterNameError = true
        submitButtonNode.assertIsNotEnabled()
    }

    @Test
    fun `Enable submit button when no error`() {
        counterNameError = false
        submitButtonNode.assertIsEnabled()
        counterNameError = false
        counterNameHelp = "help text"
        submitButtonNode.assertIsEnabled()
    }

    @Test
    fun `Close button enabled`() {
        closeButtonNode.assertIsEnabled()
    }

    @Test
    fun `Title field shows error message when error`() {
        val helpMessage = "help message"
        counterNameHelp = helpMessage
        counterNameError = true

        val semantics = titleFieldNode.fetchSemanticsNode().config

        assert(semantics.contains(SemanticsProperties.Error))
        titleFieldNode.assertTextContains(helpMessage)
        assert(semantics[SemanticsProperties.Error] == helpMessage)
    }

    @Test
    fun `Title field not marked error when no error`() {
        counterNameError = false
        assert(
            !titleFieldNode.fetchSemanticsNode().config.contains(SemanticsProperties.Error)
        )
        val helpMessage = "help message"
        counterNameHelp = helpMessage
        counterNameError = false
        assert(
            !titleFieldNode.fetchSemanticsNode().config.contains(SemanticsProperties.Error)
        )
    }

    @Test
    fun `On start the first field is focused`() {
        titleFieldNode.assertIsFocused()
    }

    @Test
    fun `Title field Ime action is submit when valid`() {
        var hasSubmitted = false
        onDone.value = { hasSubmitted = true }

        counterNameError = false

        assert(
            !titleFieldNode.fetchSemanticsNode().config.contains(SemanticsProperties.Error)
        )
        titleFieldNode.performImeAction()

        assert(hasSubmitted)
    }

    @Test
    fun `Title field ime action will not submit when invalid`() {
        var hasSubmitted = false
        onDone.value = { hasSubmitted = true }

        counterNameError = true
        assert(
            titleFieldNode.fetchSemanticsNode().config.contains(SemanticsProperties.Error)
        )
        titleFieldNode.performImeAction()

        assert(!hasSubmitted)
    }
}
