package org.calamarfederal.messyink.common.stateflow

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

/**
 * # ObservableMap < KeyType, ValueType >
 *
 *     - convenient wrapper of a MutableStateFlow<Map<K,V>>
 *
 */

class ObservableMap<K, V>(
    private val _mapState: MutableStateFlow<Map<K, V>>,
    private val _changeEvents: MutableSharedFlow<Pair<K, V?>>,
) : Map<K, V> by _mapState.value {
    constructor(
        initial: Map<K, V> = mapOf(),
        replay: Int = 5,
        buffered: Int = 0,
        strategy: BufferOverflow = BufferOverflow.DROP_OLDEST,
    ) : this(
        _mapState = MutableStateFlow(initial),
        _changeEvents = MutableSharedFlow(replay = replay, extraBufferCapacity = buffered, onBufferOverflow = strategy),
    ) {}

    val changeEvents = _changeEvents.asSharedFlow()
    val mapState = _mapState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun reset(map: Map<K, V>) {
        _mapState.value = map
        _changeEvents.resetReplayCache()
    }

    fun update(key: K, value: V): Boolean {
        _mapState.update { it.toMutableMap().apply { put(key, value) } }
        return _changeEvents.tryEmit(Pair(key, value))
    }

    suspend fun updateEmit(key: K, value: V) {
        _mapState.update { it.toMutableMap().apply { put(key, value) } }
        _changeEvents.emit(Pair(key, value))
    }

    fun remove(key: K): Boolean {
        _mapState.update { it.toMutableMap().apply { remove(key) } }
        return _changeEvents.tryEmit(Pair(key, null))
    }

    suspend fun removeEmit(key: K) {
        _mapState.update { it.toMutableMap().apply { remove(key) } }
        _changeEvents.emit(Pair(key, null))
    }

    private fun mutateNoEmit(key: K, block: (V?) -> V?): Pair<K, V?> {
        var newValue: V? = null
        _mapState.updateAndGet { old ->
            old.toMutableMap().apply {
                val localNew = block(getValue(key))
                if (localNew != null) put(key, localNew)
                else remove(key)
                newValue = localNew
            }
        }
        return Pair(key, newValue)
    }

    fun mutate(key: K, block: (V?) -> V?): Boolean {
        return _changeEvents.tryEmit(mutateNoEmit(key, block))
    }

    suspend fun mutateEmit(key: K, block: (V?) -> V?) {
        _changeEvents.emit(mutateNoEmit(key, block))
    }
}

