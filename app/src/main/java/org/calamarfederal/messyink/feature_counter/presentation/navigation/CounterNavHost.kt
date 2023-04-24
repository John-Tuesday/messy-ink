package org.calamarfederal.messyink.feature_counter.presentation.navigation

import org.calamarfederal.messyink.common.navigation.SubNavOwner

object CounterNavHost : SubNavOwner<CounterNavNode> {
    override val rootRoute: String = "counter_feature"
    override val defaultStart: CounterNavNode = GameCounterNode
}
