package com.hooked.domain.usecase

import com.hooked.data.repository.GameRepository
import com.hooked.domain.model.CatchResult
import com.hooked.domain.model.FishMaster
import com.hooked.domain.model.PlayerProgress
import kotlin.random.Random

class RunFishingLoopUseCase(private val repository: GameRepository) {

    fun drawFish(stageId: String, rareBoost: Boolean = false): FishMaster {
        val fishList = repository.getFishByStage(stageId)
        val weighted = fishList.flatMap { fish ->
            val weight = when (fish.rarity.name) {
                "COMMON" -> if (rareBoost) 2 else 4
                "UNCOMMON" -> if (rareBoost) 3 else 2
                "RARE" -> if (rareBoost) 3 else 1
                "EPIC" -> if (rareBoost) 2 else 1
                else -> if (rareBoost) 1 else 0
            }
            List(weight.coerceAtLeast(1)) { fish }
        }
        return weighted.random()
    }

    fun biteWaitMs(fish: FishMaster): Long = Random.nextLong(fish.biteTimeMinMs, fish.biteTimeMaxMs)

    fun buildResult(fish: FishMaster, progress: PlayerProgress, success: Boolean, failReason: String? = null): CatchResult {
        val size = Random.nextInt(fish.minSize, fish.maxSize + 1)
        val currentBest = progress.fishRecords[fish.id]?.bestSizeCm ?: 0
        val newRecord = success && size > currentBest
        return CatchResult(
            fishId = fish.id,
            fishName = fish.name,
            sizeCm = size,
            rarity = fish.rarity,
            coin = if (success) fish.coinReward else 0,
            isNewRecord = newRecord,
            isSuccess = success,
            failReason = failReason
        )
    }
}
