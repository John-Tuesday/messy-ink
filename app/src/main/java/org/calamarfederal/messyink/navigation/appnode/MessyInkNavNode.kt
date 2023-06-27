package org.calamarfederal.messyink.navigation.appnode

import org.calamarfederal.messyink.common.navigation.SubNavNode


/**
 * Top level 'feature' node
 */
sealed class MessyInkNavNode constructor(
    override val route: String,
) : SubNavNode {
    companion object
}
