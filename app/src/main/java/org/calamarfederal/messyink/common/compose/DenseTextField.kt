package org.calamarfederal.messyink.common.compose

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DenseTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,

    // Option Begin
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = TextFieldDefaults.outlinedTextFieldPadding(),
    enabled: Boolean = true,
    singleLine: Boolean = true,
    isError: Boolean = false,

    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,

    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,

    textStyle: TextStyle = LocalTextStyle.current,
    denseTextFieldColors: TextFieldColors = outlinedTextFieldColors(),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    innerContainer: @Composable () -> Unit = {}
) {
    val mergedTextStyle = LocalTextStyle.current.copy(
        color = if(textStyle.color.isUnspecified) LocalContentColor.current else textStyle.color
    ).merge(textStyle)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = mergedTextStyle,
        enabled = enabled,
        singleLine = singleLine,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        interactionSource = interactionSource,
        modifier = modifier
    ) {
        TextFieldDefaults.OutlinedTextFieldDecorationBox(
            value.text,
            innerTextField = it,
            label = label,
            placeholder = placeholder,
            trailingIcon = trailingIcon,
            isError = isError,
            enabled = enabled,
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            supportingText = supportingText,
            interactionSource = interactionSource,
            contentPadding = contentPadding,
            colors = denseTextFieldColors,
            container = innerContainer
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DenseTextField(
    value: String,
    onValueChange: (String) -> Unit,

    // Option Begin
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = TextFieldDefaults.outlinedTextFieldPadding(),
    enabled: Boolean = true,
    singleLine: Boolean = true,
    isError: Boolean = false,

    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,

    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,

    textStyle: TextStyle = LocalTextStyle.current,
    denseTextFieldColors: TextFieldColors = outlinedTextFieldColors(),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    innerContainer: @Composable () -> Unit = {}
) {
    val mergedTextStyle = LocalTextStyle.current.copy(
        color = if (textStyle.color.isUnspecified) LocalContentColor.current else textStyle.color
    ).merge(textStyle)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = mergedTextStyle,
        enabled = enabled,
        singleLine = singleLine,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        interactionSource = interactionSource,
        modifier = modifier
    ) {
        TextFieldDefaults.OutlinedTextFieldDecorationBox(
            value,
            innerTextField = it,
            label = label,
            placeholder = placeholder,
            trailingIcon = trailingIcon,
            isError = isError,
            enabled = enabled,
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            supportingText = supportingText,
            interactionSource = interactionSource,
            contentPadding = contentPadding,
            colors = denseTextFieldColors,
            container = innerContainer
        )
    }
}

