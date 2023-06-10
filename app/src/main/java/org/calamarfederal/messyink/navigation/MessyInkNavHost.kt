package org.calamarfederal.messyink.navigation

import org.calamarfederal.messyink.common.navigation.SubNavOwner
import org.calamarfederal.messyink.navigation.appnode.FeatureCounter
import org.calamarfederal.messyink.navigation.appnode.MessyInkNavNode

object MessyInkNavHost : SubNavOwner<MessyInkNavNode> {
    override val rootRoute: String = "messy_ink_root"
    override val defaultStart: MessyInkNavNode = FeatureCounter
}

