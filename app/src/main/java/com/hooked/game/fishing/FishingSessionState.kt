package com.hooked.game.fishing

import com.hooked.domain.model.FishMaster

sealed class FishingPhase {
    data object Idle : FishingPhase()
    data object Casting : FishingPhase()
    data object WaitingBite : FishingPhase()
    data object HookWindow : FishingPhase()
    data object Fighting : FishingPhase()
    data object Finished : FishingPhase()
}

data class FishingSessionState(
    val phase: FishingPhase = FishingPhase.Idle,
    val selectedFish: FishMaster? = null,
    val biteWaitMs: Long = 0,
    val hookWindowLeftMs: Long = 0,
    val instruction: String = "Press OK to cast"
)
