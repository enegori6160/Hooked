package com.hooked.domain.model

enum class FishRarity { COMMON, UNCOMMON, RARE, EPIC, LEGENDARY }

enum class FishState { CALM, STRUGGLING }

enum class RewardAction {
    RETRY_ESCAPE,
    BOOST_RARE_RATE,
    RESTORE_LINE,
    DOUBLE_REWARD
}

data class FishMaster(
    val id: String,
    val name: String,
    val stageId: String,
    val rarity: FishRarity,
    val minSize: Int,
    val maxSize: Int,
    val coinReward: Int,
    val hookWindowMs: Long,
    val biteTimeMinMs: Long,
    val biteTimeMaxMs: Long,
    val fightStrength: Float,
    val struggleFrequency: Float,
    val escapeSpeed: Float,
    val lineDamageMultiplier: Float,
    val spriteName: String,
    val shadowSize: Float,
    val isLegendary: Boolean
)

data class StageMaster(
    val id: String,
    val name: String,
    val fishIds: List<String>
)

enum class EquipmentType { ROD, REEL, LINE, LURE }

data class Equipment(
    val type: EquipmentType,
    val level: Int,
    val nextCost: Int
)

data class FishRecord(
    val fishId: String,
    val discovered: Boolean = false,
    val bestSizeCm: Int = 0,
    val catches: Int = 0
)

data class PlayerProgress(
    val coins: Int = 100,
    val unlockedStages: Set<String> = setOf("lake"),
    val equipment: Map<EquipmentType, Equipment> = EquipmentType.entries.associateWith {
        Equipment(type = it, level = 1, nextCost = 50)
    },
    val fishRecords: Map<String, FishRecord> = emptyMap(),
    val soundEnabled: Boolean = true
)

data class FightState(
    val fishName: String,
    val fishState: FishState = FishState.CALM,
    val fishDistanceToCatch: Float = 100f,
    val fishEscapeMeter: Float = 0f,
    val lineDurability: Float = 100f,
    val timeLeftMs: Long = 30_000
)

data class CatchResult(
    val fishId: String,
    val fishName: String,
    val sizeCm: Int,
    val rarity: FishRarity,
    val coin: Int,
    val isNewRecord: Boolean,
    val isSuccess: Boolean,
    val failReason: String? = null
)

data class DexEntry(
    val fishId: String,
    val name: String,
    val rarity: FishRarity,
    val discovered: Boolean,
    val bestSizeCm: Int,
    val catches: Int
)
