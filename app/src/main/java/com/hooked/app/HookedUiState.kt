package com.hooked.app

import com.hooked.domain.model.CatchResult
import com.hooked.domain.model.DexEntry
import com.hooked.domain.model.FightState
import com.hooked.domain.model.PlayerProgress
import com.hooked.domain.model.StageMaster
import com.hooked.game.fishing.FishingPhase

data class FishingUiState(
    val phase: FishingPhase = FishingPhase.Idle,
    val currentStageId: String = "lake",
    val selectedFishName: String = "",
    val instruction: String = "Press OK to cast",
    val biteProgress: Float = 0f,
    val hookWindowProgress: Float = 1f,
    val fightState: FightState? = null,
    val canGoResult: Boolean = false
)

data class HookedUiState(
    val progress: PlayerProgress = PlayerProgress(),
    val stages: List<StageMaster> = emptyList(),
    val fishingUi: FishingUiState = FishingUiState(),
    val lastCatch: CatchResult? = null,
    val dexEntries: List<DexEntry> = emptyList()
)
