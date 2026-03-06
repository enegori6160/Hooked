package com.hooked.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SaveData(
    val coins: Int = 100,
    val unlockedStages: Set<String> = setOf("lake"),
    val equipmentLevels: Map<String, Int> = mapOf(
        "ROD" to 1,
        "REEL" to 1,
        "LINE" to 1,
        "LURE" to 1
    ),
    val fishBestSize: Map<String, Int> = emptyMap(),
    val fishCatches: Map<String, Int> = emptyMap(),
    val discoveredFish: Set<String> = emptySet(),
    val soundEnabled: Boolean = true
)
