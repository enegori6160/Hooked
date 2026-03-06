package com.hooked.game.battle

import com.hooked.domain.model.FightState
import com.hooked.domain.model.FishMaster
import com.hooked.domain.model.FishState
import kotlin.random.Random

class FightEngine {
    fun initial(fish: FishMaster, lineBonus: Int): FightState {
        return FightState(
            fishName = fish.name,
            fishDistanceToCatch = 100f,
            fishEscapeMeter = 0f,
            lineDurability = 100f + (lineBonus * 8f),
            timeLeftMs = 30_000
        )
    }

    fun tick(current: FightState, fish: FishMaster, tapPower: Float, dtMs: Long): FightState {
        val nextState = if (Random.nextFloat() < fish.struggleFrequency * (dtMs / 1000f)) {
            FishState.STRUGGLING
        } else {
            FishState.CALM
        }

        val pullPower = tapPower.coerceIn(0f, 1f)
        val calmMultiplier = if (nextState == FishState.CALM) 1f else 0.45f
        val distanceDecrease = pullPower * fish.fightStrength * calmMultiplier * 12f * (dtMs / 1000f)

        val overPull = (pullPower - 0.6f).coerceAtLeast(0f)
        val lineDamageBase = overPull * 8f * fish.lineDamageMultiplier
        val struggleDamage = if (nextState == FishState.STRUGGLING) pullPower * 14f * fish.lineDamageMultiplier else 0f

        val escapeIncrease = ((1f - pullPower) * fish.escapeSpeed * 7f + if (nextState == FishState.STRUGGLING) 1.3f else 0f) * (dtMs / 1000f)
        val escapeDecrease = (pullPower * 2.2f) * (dtMs / 1000f)

        return current.copy(
            fishState = nextState,
            fishDistanceToCatch = (current.fishDistanceToCatch - distanceDecrease).coerceAtLeast(0f),
            fishEscapeMeter = (current.fishEscapeMeter + escapeIncrease - escapeDecrease).coerceIn(0f, 100f),
            lineDurability = (current.lineDurability - lineDamageBase - struggleDamage).coerceAtLeast(0f),
            timeLeftMs = (current.timeLeftMs - dtMs).coerceAtLeast(0)
        )
    }
}
