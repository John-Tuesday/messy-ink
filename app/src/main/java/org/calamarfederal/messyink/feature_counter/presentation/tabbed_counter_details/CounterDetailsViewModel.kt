package org.calamarfederal.messyink.feature_counter.presentation.tabbed_counter_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import org.calamarfederal.messyink.feature_counter.presentation.state.UiCounter
import org.calamarfederal.messyink.feature_counter.presentation.state.UiTick
import javax.inject.Inject

@HiltViewModel
class CounterDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val counter: StateFlow<UiCounter> = TODO()
    val ticks: StateFlow<List<UiTick>> = TODO()
    val tickSum: StateFlow<Double?> = TODO()
    val tickAverage: StateFlow<Double?> = TODO()
}
