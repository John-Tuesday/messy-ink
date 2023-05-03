package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import javax.inject.Inject

/**
 * # Counter Details View Model
 *
 * track both summary stats and specific details of [counter]
 */
@HiltViewModel
class CounterDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        /**
         * Key for getting and setting [counter.value.id] in [savedStateHandle]
         */
        const val COUNTER_ID = "counter_id"
    }

    /**
     * Counter being examined.
     *
     * When no valid [UiCounter] can be found from [COUNTER_ID], start a new one?
     */
    val counter: StateFlow<UiCounter> = TODO()


    /**
     * All [UiTick] with [UiTick.parentId] equal to [counter]
     */
    val ticks: StateFlow<List<UiTick>> = TODO()


    /**
     * Sum of [ticks] or `null` when empty
     */
    val tickSum: StateFlow<Double?> = TODO()

    /**
     * Average of [ticks] or `null` when empty
     */
    val tickAverage: StateFlow<Double?> = TODO()
}
