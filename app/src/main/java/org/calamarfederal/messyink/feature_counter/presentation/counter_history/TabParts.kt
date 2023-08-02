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
import androidx.compose.ui.res.stringResource
import org.calamarfederal.messyink.R

/**
 * # Enum containing all tabs
 *
 * ## Single Source of Truth:
 * - quantity of tabs
 * - tab display names
 * - index of each tab
 */
@Suppress("MemberVisibilityCanBePrivate")
internal enum class CounterHistoryTab(
    private val activeIcon: () -> ImageVector,
    private val inactiveIcon: () -> ImageVector,
) {
    TickGraphs(
        activeIcon = { Icons.Filled.Analytics },
        inactiveIcon = { Icons.Outlined.Analytics }
    ),
    TickLogs(
        activeIcon = { Icons.Filled.DataExploration },
        inactiveIcon = { Icons.Outlined.DataExploration },
    ),
    ;

    val displayName: String
        @Composable
        get() = when (this) {
                TickGraphs -> stringResource(R.string.tick_graph)
                TickLogs -> stringResource(R.string.tick_log)
            }

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
}

@Composable
internal fun CounterHistoryNavBar(
    selectedIndex: Int,
    onChangeSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        for (tab in CounterHistoryTab.entries) {
            NavBarItem(
                tab = tab,
                isSelected = selectedIndex == tab.ordinal,
                onClick = { onChangeSelect(tab.ordinal) },
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
