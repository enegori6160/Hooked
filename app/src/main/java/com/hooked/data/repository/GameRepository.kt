package com.hooked.data.repository

import com.hooked.data.local.SaveDataStore
import com.hooked.data.master.MasterData
import com.hooked.data.model.SaveData
import com.hooked.domain.model.DexEntry
import com.hooked.domain.model.Equipment
import com.hooked.domain.model.EquipmentType
import com.hooked.domain.model.FishRecord
import com.hooked.domain.model.PlayerProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepository(private val saveDataStore: SaveDataStore) {

    val progressFlow: Flow<PlayerProgress> = saveDataStore.saveFlow.map { it.toProgress() }

    suspend fun saveProgress(progress: PlayerProgress) {
        saveDataStore.write(progress.toSaveData())
    }

    fun getStages() = MasterData.stages

    fun getFishByStage(stageId: String) = MasterData.fish.filter { it.stageId == stageId }

    fun getFish(id: String) = MasterData.fishById[id]

    fun buildDex(progress: PlayerProgress): List<DexEntry> {
        return MasterData.fish.map { fish ->
            val record = progress.fishRecords[fish.id] ?: FishRecord(fish.id)
            DexEntry(
                fishId = fish.id,
                name = fish.name,
                rarity = fish.rarity,
                discovered = record.discovered,
                bestSizeCm = record.bestSizeCm,
                catches = record.catches
            )
        }
    }

    private fun SaveData.toProgress(): PlayerProgress {
        val equipmentMap = EquipmentType.entries.associateWith { type ->
            val lvl = equipmentLevels[type.name] ?: 1
            Equipment(type = type, level = lvl, nextCost = 50 * lvl)
        }
        val records = (fishBestSize.keys + fishCatches.keys + discoveredFish).associateWith { id ->
            FishRecord(
                fishId = id,
                discovered = discoveredFish.contains(id),
                bestSizeCm = fishBestSize[id] ?: 0,
                catches = fishCatches[id] ?: 0
            )
        }
        return PlayerProgress(
            coins = coins,
            unlockedStages = unlockedStages,
            equipment = equipmentMap,
            fishRecords = records,
            soundEnabled = soundEnabled
        )
    }

    private fun PlayerProgress.toSaveData(): SaveData {
        return SaveData(
            coins = coins,
            unlockedStages = unlockedStages,
            equipmentLevels = equipment.mapValues { it.value.level },
            fishBestSize = fishRecords.mapValues { it.value.bestSizeCm },
            fishCatches = fishRecords.mapValues { it.value.catches },
            discoveredFish = fishRecords.filterValues { it.discovered }.keys,
            soundEnabled = soundEnabled
        )
    }
}
