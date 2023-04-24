package org.calamarfederal.messyink.common.compose

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun Placeholder(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current + TextStyle(
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.Light
    ),
) = Text(
    text = text,
    style = style,
    modifier = modifier,
)
