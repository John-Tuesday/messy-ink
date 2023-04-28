package org.calamarfederal.messyink.feature_counter.presentation.game_counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

@Composable
internal fun QuickTickButtons(
    centerSlot: @Composable () -> Unit,
    onAddTick: (Double) -> Unit,
    modifier: Modifier = Modifier,
    mainAmount: Double = 5.00,
    mediumAmount: Double = 2.00,
    smallAmount: Double = 1.00,
    mainWeight: Float = 10f,
    mediumWeight: Float = 7.5f,
    smallWeight: Float = 5f,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        MainTickButton(
            amount = mainAmount,
            onClick = onAddTick,
            modifier = Modifier
                .weight(mainWeight)
                .fillMaxWidth()
        )
        MiddleTickButton(
            amount = mediumAmount, onClick = onAddTick,
            modifier = Modifier
                .weight(mediumWeight)
                .fillMaxWidth()
        )
        SmallestTickButton(
            amount = smallAmount, onClick = onAddTick,
            modifier = Modifier
                .weight(smallWeight)
                .fillMaxWidth()
        )

        Box(modifier = Modifier.weight(mainWeight * 2).fillMaxWidth()) {
            centerSlot()
        }

        SmallestTickButton(
            amount = -smallAmount, onClick = onAddTick,
            modifier = Modifier
                .weight(smallWeight)
                .fillMaxWidth()
        )
        MiddleTickButton(
            amount = -mediumAmount, onClick = onAddTick,
            modifier = Modifier
                .weight(mediumWeight)
                .fillMaxWidth()
        )
        MainTickButton(
            amount = -mainAmount,
            onClick = onAddTick,
            modifier = Modifier
                .weight(mainWeight)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun MainTickButton(
    amount: Double,
    onClick: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = { onClick(amount) },
        modifier = modifier,
    ) {
        Icon(if (amount < 0) Filled.Remove else Filled.Add, "add $amount")
        Text("${amount.absoluteValue}")
    }
}

@Composable
private fun MiddleTickButton(
    amount: Double,
    onClick: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalButton(
        onClick = { onClick(amount) },
        modifier = modifier,
    ) {
        Icon(if (amount < 0) Filled.Remove else Filled.Add, "add $amount")
        Text("${amount.absoluteValue}")
    }
}

@Composable
private fun SmallestTickButton(
    amount: Double,
    onClick: (Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = { onClick(amount) },
        modifier = modifier,
    ) {
        Icon(if (amount < 0) Filled.Remove else Filled.Add, "add $amount")
        Text("${amount.absoluteValue}")
    }
}
