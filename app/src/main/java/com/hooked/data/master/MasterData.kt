package com.hooked.data.master

import com.hooked.domain.model.FishMaster
import com.hooked.domain.model.FishRarity
import com.hooked.domain.model.StageMaster

object MasterData {
    val stages = listOf(
        StageMaster(id = "lake", name = "Lake", fishIds = listOf(
            "bluegill", "bass", "carp", "catfish", "goldfish",
            "salmon", "tuna", "puffer", "angler", "golden_carp"
        ))
    )

    val fish = listOf(
        FishMaster("bluegill", "Bluegill", "lake", FishRarity.COMMON, 10, 25, 10, 800, 1800, 4500, 0.8f, 0.2f, 1.0f, 1.0f, "fish_bluegill", 0.8f, false),
        FishMaster("bass", "Largemouth Bass", "lake", FishRarity.UNCOMMON, 20, 60, 22, 700, 1500, 3800, 1.0f, 0.35f, 1.1f, 1.1f, "fish_bass", 1.0f, false),
        FishMaster("carp", "Carp", "lake", FishRarity.COMMON, 25, 70, 18, 900, 2200, 5200, 0.9f, 0.3f, 1.0f, 1.0f, "fish_carp", 1.1f, false),
        FishMaster("catfish", "Catfish", "lake", FishRarity.RARE, 40, 120, 35, 650, 2000, 5000, 1.3f, 0.45f, 1.3f, 1.25f, "fish_catfish", 1.3f, false),
        FishMaster("goldfish", "Goldfish", "lake", FishRarity.COMMON, 8, 18, 8, 1000, 1500, 3500, 0.6f, 0.2f, 0.9f, 0.8f, "fish_goldfish", 0.6f, false),
        FishMaster("salmon", "Salmon", "lake", FishRarity.UNCOMMON, 30, 85, 28, 700, 1800, 4000, 1.1f, 0.4f, 1.2f, 1.1f, "fish_salmon", 1.0f, false),
        FishMaster("tuna", "Tuna", "lake", FishRarity.EPIC, 60, 150, 55, 600, 2200, 4800, 1.5f, 0.55f, 1.4f, 1.35f, "fish_tuna", 1.4f, false),
        FishMaster("puffer", "Pufferfish", "lake", FishRarity.RARE, 18, 35, 30, 750, 1600, 3600, 1.0f, 0.5f, 1.0f, 1.4f, "fish_puffer", 0.9f, false),
        FishMaster("angler", "Anglerfish", "lake", FishRarity.EPIC, 35, 90, 65, 550, 2400, 6000, 1.6f, 0.65f, 1.5f, 1.45f, "fish_angler", 1.2f, false),
        FishMaster("golden_carp", "Golden Carp", "lake", FishRarity.LEGENDARY, 55, 95, 120, 500, 2600, 6800, 1.7f, 0.7f, 1.6f, 1.5f, "fish_golden_carp", 1.2f, true)
    )

    val fishById = fish.associateBy { it.id }
}
