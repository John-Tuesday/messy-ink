package org.calamarfederal.messyink.common.navigation


/**
 * # Navigation Sub-graph Owner
 *
 * intended to be implemented as a stateless object at the Feature-Level
 *
 */
interface NavigationRoot<T : NavigationNode> {
    /**
     * route to this, i.e. the owner of the nested graph
     *
     * should be const
     */
    val rootRoute: String

    /**
     * The typical first screen / subcomponent when entering this
     *
     * should be const
     */
    val defaultStart: T
}
