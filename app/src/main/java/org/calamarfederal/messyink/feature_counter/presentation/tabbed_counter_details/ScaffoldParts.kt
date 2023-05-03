package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpOffset.Companion
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp

internal enum class CounterDetailsTab(val displayName: String) {
    TestScreen("Test Screen"), TickDetails("Tick Details"), GameCounter("Game View");

    companion object {
        val size: Int get() = CounterDetailsTab.values().size

        fun fromIndexOrNull(index: Int): CounterDetailsTab? = when (index) {
            TestScreen.ordinal -> TestScreen
            TickDetails.ordinal -> TickDetails
            GameCounter.ordinal -> GameCounter
            else -> null
        }

        fun fromIndex(index: Int): CounterDetailsTab = fromIndexOrNull(index)!!
    }
}

@Composable
private fun BasicIndicator(
    width: Dp,
    offset: Density.() -> IntOffset,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    thickness: Dp = 2.dp,
) {
    Box(Modifier.fillMaxSize()) {
        Divider(
            thickness = thickness,
            color = color,
            modifier = modifier
                .align(alignment = Alignment.BottomStart)
                .width(width)
                .padding(4.dp)
                .offset(offset)
        )
    }
}

internal fun tabIndicator(
    selectedIndex: Int,
    currentIndex: Int,
    offsetFraction: Float,
    onChangeSelect: (Int) -> Unit,
): @Composable (List<TabPosition>) -> Unit = { tabPositions ->
    LaunchedEffect(currentIndex, selectedIndex) {
        if (currentIndex != selectedIndex) onChangeSelect(currentIndex)
    }

    BasicIndicator(
        width = tabPositions[currentIndex].width,
        offset = {
            IntOffset(
                x = (tabPositions[currentIndex].left + tabPositions[currentIndex].width * offsetFraction)
                    .coerceIn(tabPositions.first().left, tabPositions.last().left)
                    .roundToPx(),
                y = 0
            )
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
private fun pageIndicator(
    selectedIndex: Int,
    onChangeSelect: (Int) -> Unit,
    pagerState: PagerState,
): @Composable (List<TabPosition>) -> Unit = { tabPositions ->
    val currentPage by remember { derivedStateOf(pagerState::currentPage) }
    val percent by remember { derivedStateOf(pagerState::currentPageOffsetFraction) }

//    BasicIndicator(
//        width = tabPositions[currentPage].width,
//        offset = {
//            IntOffset(
//                x = (tabPositions[currentPage].left + tabPositions[currentPage].width * percent)
//                    .coerceIn(tabPositions.first().left, tabPositions.last().left)
//                    .roundToPx(),
//                y = 0
//            )
//        }
//    )
}

internal fun scrollIndicator(
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
