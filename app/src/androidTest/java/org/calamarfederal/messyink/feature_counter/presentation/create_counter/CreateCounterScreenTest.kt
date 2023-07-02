package org.calamarfederal.messyink.feature_counter.presentation.create_counter

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.calamarfederal.messyink.MainActivity
import org.calamarfederal.messyink.OnCreateHookImpl
import org.calamarfederal.messyink.feature_counter.data.generateCounters
import org.calamarfederal.messyink.feature_counter.data.toCounter
import org.calamarfederal.messyink.feature_counter.domain.use_case.toUI
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounterSupport

import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * # Create Counter Screen
 * ## Unit Tests
 */
class CreateCounterScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var counterSupportState: MutableStateFlow<UiCounterSupport>
    private lateinit var onCancel: MutableStateFlow<() -> Unit>
    private lateinit var onDone: MutableStateFlow<() -> Unit>

    private val submitButtonNode get() = composeRule.onNodeWithTag(CreateCounterTestTags.SubmitButton)
    private val closeButtonNode get() = composeRule.onNodeWithTag(CreateCounterTestTags.CloseButton)
    private val titleFieldNode get() = composeRule.onNodeWithTag(CreateCounterTestTags.TitleTextField)

    @Before
    fun setUp() {
        counterSupportState = MutableStateFlow(UiCounterSupport())
        onCancel = MutableStateFlow({})
        onDone = MutableStateFlow({})

        composeRule.setContent {
            val counterSupport by counterSupportState.collectAsState()
            val cancel by onCancel.collectAsState()
            val done by onDone.collectAsState()

            CreateCounterScreen(
                counterSupport = counterSupport,
                onNameChange = {},
                onCancel = cancel,
                onDone = done,
            )
        }
    }

    @Test
    fun `Disable submit button when error`() {
        counterSupportState.value = UiCounterSupport(nameError = true)
        submitButtonNode.assertIsNotEnabled()
    }

    @Test
    fun `Enable submit button when no error`() {
        counterSupportState.value = UiCounterSupport(nameError = false)
        submitButtonNode.assertIsEnabled()
        counterSupportState.value = UiCounterSupport(nameHelp = "help text", nameError = false)
        submitButtonNode.assertIsEnabled()
    }

    @Test
    fun `Close button enabled`() {
        closeButtonNode.assertIsEnabled()
    }

    @Test
    fun `Title field shows error message when error`() {
        val helpMessage = "help message"
        counterSupportState.value = UiCounterSupport(nameHelp = helpMessage, nameError = true)

        val semantics = titleFieldNode.fetchSemanticsNode().config

        assert(semantics.contains(SemanticsProperties.Error))
        titleFieldNode.assertTextContains(helpMessage)
        assert(semantics[SemanticsProperties.Error] == helpMessage)
    }

    @Test
    fun `Title field not marked error when no error`() {
        counterSupportState.value = UiCounterSupport(nameError = false)
        assert(
            !titleFieldNode.fetchSemanticsNode().config.contains(SemanticsProperties.Error)
        )
        val helpMessage = "help message"
        counterSupportState.value = UiCounterSupport(nameHelp = helpMessage, nameError = false)
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

        counterSupportState.update { it.copy(nameError = false) }

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

        counterSupportState.value = UiCounterSupport(nameError = true)
        assert(
            titleFieldNode.fetchSemanticsNode().config.contains(SemanticsProperties.Error)
        )
        titleFieldNode.performImeAction()

        assert(!hasSubmitted)
    }
}
