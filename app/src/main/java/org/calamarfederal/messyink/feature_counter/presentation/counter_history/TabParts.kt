package org.calamarfederal.messyink.feature_counter.presentation.counter_history

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.DataExploration
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.DataExploration
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * # Enum containing all tabs
 *
 * ## Single Source of Truth:
 * - quantity of tabs
 * - tab display names
 * - index of each tab
 */
internal enum class CounterHistoryTab(
    val displayName: String,
    private val activeIcon: () -> ImageVector,
    private val inactiveIcon: () -> ImageVector,
) {
    TickGraphs(
        displayName = "Tick Graphs",
        activeIcon = { Icons.Filled.Analytics },
        inactiveIcon = { Icons.Outlined.Analytics }
    ),
    TickLogs(
        displayName = "Tick Logs",
        activeIcon = { Icons.Filled.DataExploration },
        inactiveIcon = { Icons.Outlined.DataExploration },
    ),
    ;

    /**
     * mapped tab index of this
     *
     * alias for [::ordinal]
     */
    val index: Int get() = ordinal

    /**
     * Icon representing this tab when it is selected
     *
     * defined using a getter so as to not store the [ImageVector] constantly
     */
    val iconActive: ImageVector get() = activeIcon()

    /**
     * Icon representing this tab when it is not selected
     *
     * defined using a getter so as to not store the [ImageVector] constantly
     */
    val iconInactive: ImageVector get() = inactiveIcon()

    companion object {
        /**
         * total number of tabs
         *
         * convenience method for: `CounterDetailsTab.values().size`
         */
        val size: Int get() = CounterHistoryTab.values().size

        /**
         * Get the corresponding [CounterHistoryTab] from [index] or `null`
         */
        fun fromIndexOrNull(index: Int): CounterHistoryTab? = when (index) {
            TickGraphs.index -> TickGraphs
            TickLogs.index   -> TickLogs
            else             -> null
        }

        /**
         * Get the corresponding [CounterHistoryTab] from [index] or throw [IndexOutOfBoundsException]
         */
        fun fromIndex(index: Int): CounterHistoryTab = fromIndexOrNull(index)
            ?: throw (IndexOutOfBoundsException("index: $index does not map to a valid object"))
    }
}

@Composable
internal fun CounterHistoryNavBar(
    selectedIndex: Int,
    onChangeSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        for (tab in CounterHistoryTab.values()) {
            NavBarItem(
                tab = tab,
                isSelected = selectedIndex == tab.index,
                onClick = { onChangeSelect(tab.index) },
            )
        }
    }
}

@Composable
private fun RowScope.NavBarItem(
    tab: CounterHistoryTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    alwaysShowLabel: Boolean = true,
) {
    NavigationBarItem(
        modifier = modifier,
        alwaysShowLabel = alwaysShowLabel,
        selected = isSelected,
        onClick = onClick,
        label = { Text(tab.displayName) },
        icon = {
            Icon(
                if (isSelected)
                    tab.iconActive
                else
                    tab.iconInactive,
                null,
            )
        },
    )
}
