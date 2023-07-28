package org.calamarfederal.messyink.feature_counter.domain

import org.calamarfederal.messyink.feature_counter.data.model.Tick

/**
 * Create a [Tick] with a given amount and set the time values to the current time
 */
fun interface SimpleCreateTickUseCase {
    /**
     * Create [Tick] with [amount] and [parentId] and inject current time into the time values
     */
    suspend operator fun invoke(amount: Double, parentId: Long): Tick
}
