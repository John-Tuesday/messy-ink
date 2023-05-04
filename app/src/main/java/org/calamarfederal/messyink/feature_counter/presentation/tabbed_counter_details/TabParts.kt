package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.DataExploration
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details.CounterDetailsTab.GameCounter
import org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details.CounterDetailsTab.TestScreen
import org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details.CounterDetailsTab.TickDetails

/**
 * # Enum containing all tabs
 *
 * ## Single Source of Truth:
 * - quantity of tabs
 * - tab display names
 * - index of each tab
 */
internal enum class CounterDetailsTab(val displayName: String) {
    GameCounter("Game View"),
    TickDetails("Tick Details"),
    TestScreen("Test Screen"),
    ;

    /**
     * mapped tab index of this
     *
     * alias for [::ordinal]
     */
    val index: Int get() = ordinal

    companion object {
        /**
         * total number of tabs
         *
         * convenience method for: `CounterDetailsTab.values().size`
         */
        val size: Int get() = CounterDetailsTab.values().size

        /**
         * Get the corresponding [CounterDetailsTab] from [index] or `null`
         */
        fun fromIndexOrNull(index: Int): CounterDetailsTab? = when (index) {
            TestScreen.index -> TestScreen
            TickDetails.index -> TickDetails
            GameCounter.index -> GameCounter
            else -> null
        }

        /**
         * Get the corresponding [CounterDetailsTab] from [index] or throw [IndexOutOfBoundsException]
         */
        fun fromIndex(index: Int): CounterDetailsTab = fromIndexOrNull(index)
            ?: throw (IndexOutOfBoundsException("index: $index does not map to a valid object"))
    }
}

@Composable
internal fun CounterDetailsNavBar(
    selectedIndex: Int,
    onChangeSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
    ) {
        for (tab in CounterDetailsTab.values()) {
            NavBarItem(
                tab = tab,
                selectedIndex = selectedIndex,
                onClick = { onChangeSelect(tab.index) },
            )
        }
    }
}

@Composable
private fun RowScope.NavBarItem(
    tab: CounterDetailsTab,
    selectedIndex: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    alwaysShowLabel: Boolean = false,
) {
    NavigationBarItem(
        modifier = modifier,
        alwaysShowLabel = alwaysShowLabel,
        selected = selectedIndex == tab.index,
        onClick = onClick,
//        label = { Text(tab.displayName) },
        icon = {
            when (tab) {
                GameCounter -> Icon(Icons.Filled.ImportExport, "game mode screen")
                TickDetails -> Icon(Icons.Filled.DataExploration, "details of each tick")
                TestScreen  -> Icon(Icons.Filled.Analytics, "test screen")
            }
        },
    )
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
