package org.calamarfederal.messyink.feature_counter.presentation.navigation

import org.calamarfederal.messyink.common.navigation.SubNavNode

/**
 * # Sealed base class for Navigation Points for use with [CounterNavHost]
 */
internal sealed class CounterNavNode(override val route: String) : SubNavNode {

}
