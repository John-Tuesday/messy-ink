package org.calamarfederal.messyink.feature_counter.presentation.create_counter

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.calamarfederal.messyink.MainActivity
import org.calamarfederal.messyink.OnCreateHookImpl
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounterSupport

import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * # Create Counter Screen
 * ## Unit Tests
 */
@HiltAndroidTest
class CreateCounterScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val testContent = CreateCounterTestContent()

    @BindValue
    val contentSetter: OnCreateHookImpl = testContent

    private var otherCounterSupport: UiCounterSupport?
        get() = testContent.otherCounterSupport
        set(value) {
            testContent.otherCounterSupport = value
        }

    private var onCancelHook: () -> Unit
        get() = testContent.onCancelHook
        set(value) {
            testContent.onCancelHook = value
        }
    private var onDoneHook: () -> Unit
        get() = testContent.onDoneHook
        set(value) {
            testContent.onDoneHook = value
        }

    private val submitButtonNode get() = composeRule.onNodeWithTag(CreateCounterTestTags.SubmitButton)
    private val closeButtonNode get() = composeRule.onNodeWithTag(CreateCounterTestTags.CloseButton)
    private val titleFieldNode get() = composeRule.onNodeWithTag(CreateCounterTestTags.TitleTextField)

    @Before
    fun setUp() {
        hiltRule.inject()

        otherCounterSupport = null
        onCancelHook = {}
        onDoneHook = {}
    }

    @Test
    fun `Disable submit button when error`() {
        otherCounterSupport = UiCounterSupport(nameError = true)
        submitButtonNode.assertIsNotEnabled()
    }

    @Test
    fun `Enable submit button when no error`() {
        otherCounterSupport = UiCounterSupport(nameError = false)
        submitButtonNode.assertIsEnabled()
        otherCounterSupport = UiCounterSupport(nameHelp = "help text", nameError = false)
        submitButtonNode.assertIsEnabled()
    }

    @Test
    fun `Close button enabled`() {
        closeButtonNode.assertIsEnabled()
    }

    @Test
    fun `Title field shows error message when error`() {
        val helpMessage = "help message"
        otherCounterSupport = UiCounterSupport(nameHelp = helpMessage, nameError = true)

        val semantics = titleFieldNode.fetchSemanticsNode().config

        assert(semantics.contains(SemanticsProperties.Error))
        titleFieldNode.assertTextContains(helpMessage)
        assert(semantics[SemanticsProperties.Error] == helpMessage)
    }

    @Test
    fun `Title field not marked error when no error`() {
        otherCounterSupport = UiCounterSupport(nameError = false)
        assert(
            !titleFieldNode.fetchSemanticsNode().config.contains(SemanticsProperties.Error)
        )
        val helpMessage = "help message"
        otherCounterSupport = UiCounterSupport(nameHelp = helpMessage, nameError = false)
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
        onDoneHook = { hasSubmitted = true }

        titleFieldNode.performTextInput("valid title")
        assert(
            !titleFieldNode.fetchSemanticsNode().config.contains(SemanticsProperties.Error)
        )
        titleFieldNode.performImeAction()

        assert(hasSubmitted)
    }

    @Test
    fun `Title field ime action will not submit when invalid`() {
        var hasSubmitted = false
        onDoneHook = { hasSubmitted = true }

        otherCounterSupport = UiCounterSupport(nameError = true)
        assert(
            titleFieldNode.fetchSemanticsNode().config.contains(SemanticsProperties.Error)
        )
        titleFieldNode.performImeAction()

        assert(!hasSubmitted)
    }
}
