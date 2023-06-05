package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

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

@Composable
internal fun CounterDetailsNavBar(
    selectedIndex: Int,
    onChangeSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        for (tab in CounterDetailsTab.values()) {
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
    tab: CounterDetailsTab,
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

/**
 * # Enum containing all tabs
 *
 * ## Single Source of Truth:
 * - quantity of tabs
 * - tab display names
 * - index of each tab
 */
internal enum class CounterDetailsTab(
    val displayName: String,
    private val activeIcon: () -> ImageVector,
    private val inactiveIcon: () -> ImageVector,
) {
    TickGraphs(
        displayName = "Tick Graphs",
        activeIcon = { Icons.Filled.Analytics },
        inactiveIcon = { Icons.Outlined.Analytics }
    ),
    TickDetails(
        displayName = "Tick Details",
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
        val size: Int get() = CounterDetailsTab.values().size

        /**
         * Get the corresponding [CounterDetailsTab] from [index] or `null`
         */
        fun fromIndexOrNull(index: Int): CounterDetailsTab? = when (index) {
            TickGraphs.index -> TickGraphs
            TickDetails.index -> TickDetails
            else -> null
        }

        /**
         * Get the corresponding [CounterDetailsTab] from [index] or throw [IndexOutOfBoundsException]
         */
        fun fromIndex(index: Int): CounterDetailsTab = fromIndexOrNull(index)
            ?: throw (IndexOutOfBoundsException("index: $index does not map to a valid object"))
    }
}

