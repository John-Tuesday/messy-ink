package org.calamarfederal.messyink.feature_counter.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.calamarfederal.messyink.feature_counter.data.model.NOID
import org.calamarfederal.messyink.feature_counter.data.model.Tick
import org.calamarfederal.messyink.feature_counter.data.repository.TickRepository
import org.calamarfederal.messyink.feature_counter.di.CurrentTime
import org.calamarfederal.messyink.feature_counter.di.IODispatcher
import javax.inject.Inject

class SimpleCreateTickUseCaseImpl @Inject constructor(
    private val repo: TickRepository,
    @CurrentTime
    private val getTime: GetTime,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : SimpleCreateTickUseCase {
    override suspend fun invoke(amount: Double, parentId: Long): Tick {
        require(parentId != NOID)
        val time = getTime()
        return withContext(ioDispatcher) {
            repo.createTick(
                Tick(
                    amount = amount,
                    timeModified = time,
                    timeCreated = time,
                    timeForData = time,
                    parentId = parentId,
                    id = NOID,
                )
            )
        }

    }
}
