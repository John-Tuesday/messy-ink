package org.calamarfederal.messyink.feature_counter.presentation.navigation

import org.calamarfederal.messyink.common.navigation.SubNavNode
import org.calamarfederal.messyink.common.navigation.SubNavOwner

/**
 * # Counter Feature-level [SubNavHost]
 *
 * defines the entry point for the feature
 */
object CounterNavHost : SubNavOwner<CounterNavNode> {
    override val rootRoute: String = "counter_feature"
    override val defaultStart: CounterNavNode = CounterOverviewNode
}

/**
 * # Sealed base class for Navigation Points for use with [CounterNavHost]
 */
sealed interface CounterNavNode : SubNavNode
