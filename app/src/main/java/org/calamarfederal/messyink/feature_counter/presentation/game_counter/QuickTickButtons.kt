package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.common.compose.toStringAllowShorten
import kotlin.math.absoluteValue

@Composable
internal fun CompactTickButtons(
    centerSlot: @Composable () -> Unit,
    onAddTick: (Double) -> Unit,
    modifier: Modifier = Modifier,
    primaryWeight: Float = 10f,
    primaryAmount: Double = 5.00,
    secondaryWeight: Float = 7.5f,
    secondaryAmount: Double = 1.00,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Button(
            onClick = { onAddTick(primaryAmount) },
            modifier = Modifier
                .weight(primaryWeight)
                .fillMaxWidth(),
        ) {
            Icon(Icons.Filled.Add, "add $primaryAmount")
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(primaryAmount.toStringAllowShorten(), style = textStyle)
        }
        FilledTonalButton(
            onClick = { onAddTick(secondaryAmount) },
            modifier = Modifier
                .weight(secondaryWeight)
                .fillMaxWidth(),
        ) {
            Icon(Icons.Filled.Add, "add $secondaryAmount")
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(secondaryAmount.toStringAllowShorten(), style = textStyle)
        }
        Box(modifier = Modifier.weight(primaryWeight * 2)) {
            centerSlot()
        }
        FilledTonalButton(
            onClick = { onAddTick(-secondaryAmount) },
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            modifier = Modifier
                .weight(secondaryWeight)
                .fillMaxWidth(),
        ) {
            Icon(Icons.Filled.Remove, "subtract $secondaryAmount")
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(secondaryAmount.toStringAllowShorten(), style = textStyle)
        }
        Button(
            onClick = { onAddTick(-primaryAmount) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
            ),
            modifier = Modifier
                .weight(primaryWeight)
                .fillMaxWidth(),
        ) {
            Icon(Icons.Filled.Remove, "subtract $primaryAmount")
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(primaryAmount.toStringAllowShorten(), style = textStyle)
        }
    }
}

