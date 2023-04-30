package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.core.util.toRange
import kotlinx.coroutines.flow.map
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

internal enum class CounterDetailsTab(val displayName: String) {
    TestScreen("Test Screen"), TickDetails("Tick Details"), GameCounter("Game View");
}

@Composable
private fun TestIndicatorBar(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .padding(4.dp)
            .fillMaxSize()
            .border(
                2.dp,
                MaterialTheme.colorScheme.onPrimaryContainer,
                shape = MaterialTheme.shapes.small
            )
    )
}

internal fun testIndicator(
    selectedIndex: Int,
    scrollState: LazyListState,
    onChangeSelect: (Int) -> Unit,
): @Composable (List<TabPosition>) -> Unit = { tabPositions ->
    Box(Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val percent by remember(selectedIndex) {
            derivedStateOf {
                (scrollState.firstVisibleItemScrollOffset / scrollState.layoutInfo.visibleItemsInfo.first().size.toFloat()).times(
//                    scrollState.firstVisibleItemIndex - selectedIndex + 1
                    1
                )
            }
        }
        val clampedOffset by remember(selectedIndex) {
            derivedStateOf {
                with(density) {
                    (tabPositions[selectedIndex].left + tabPositions[selectedIndex].width.times(
                        if (scrollState.firstVisibleItemIndex == selectedIndex) percent
                        else percent - 1
                    ))
                        .coerceIn(tabPositions.first().left .. tabPositions.last().left)
                        .roundToPx()
                }
            }
        }

        val visibleIndex by remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
        LaunchedEffect(selectedIndex, percent) {
            println("$percent")
            if (selectedIndex == visibleIndex && percent > .50)
                onChangeSelect(selectedIndex + 1)
            else if (selectedIndex != visibleIndex && percent < .50)
                onChangeSelect(visibleIndex)
        }

        Divider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .width(tabPositions[selectedIndex].width)
                .padding(4.dp)
                .offset { IntOffset(x = clampedOffset, y = 0) })
    }
}

@Composable
internal fun CounterDetailsTabRow(
    selectedIndex: Int,
    onChangeSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    indicator: (@Composable (List<TabPosition>) -> Unit)? = null,
) {
    TabRow(
        modifier = modifier,
        selectedTabIndex = selectedIndex,
        indicator = indicator ?: {},
    ) {
        for (t in CounterDetailsTab.values()) {
            t.TabLabel(selectedIndex = selectedIndex, onClick = { onChangeSelect(t.ordinal) })
        }
    }
}

@Composable
private fun CounterDetailsTab.TabLabel(
    selectedIndex: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Tab(
        selected = selectedIndex == ordinal,
        onClick = onClick,
        modifier = modifier,
        text = { Text(name) },
        selectedContentColor = MaterialTheme.colorScheme.inversePrimary,
        unselectedContentColor = MaterialTheme.colorScheme.primary
    )
}
