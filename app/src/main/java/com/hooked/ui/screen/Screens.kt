package com.hooked.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hooked.app.FishingAction
import com.hooked.app.FishingUiState
import com.hooked.domain.model.CatchResult
import com.hooked.domain.model.DexEntry
import com.hooked.domain.model.Equipment
import com.hooked.domain.model.EquipmentType
import com.hooked.domain.model.FishState
import com.hooked.domain.model.RewardAction
import com.hooked.domain.model.StageMaster
import com.hooked.game.fishing.FishingPhase
import com.hooked.ui.component.GaugeBar
import com.hooked.ui.component.TVButton

@Composable
fun SplashTitleScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0A0E1A)).padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("HOOKED!", style = MaterialTheme.typography.displayLarge, color = Color(0xFFFFD54F))
        Text("Retro Fishing on TV", style = MaterialTheme.typography.headlineMedium)
        TVButton("START", onStart)
    }
}

@Composable
fun MainMenuScreen(coin: Int, onStartFishing: () -> Unit, onOpenDex: () -> Unit, onOpenShop: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Coins: $coin", style = MaterialTheme.typography.headlineSmall)
        TVButton("Go Fishing", onStartFishing)
        TVButton("FishDex", onOpenDex)
        TVButton("Shop", onOpenShop)
    }
}

@Composable
fun StageSelectScreen(stages: List<StageMaster>, onSelectStage: (StageMaster) -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(28.dp)) {
        Text("Select Stage", style = MaterialTheme.typography.headlineMedium)
        stages.forEach { stage -> TVButton(stage.name) { onSelectStage(stage) } }
        TVButton("Back", onBack)
    }
}

@Composable
fun FishingScreen(state: FishingUiState, onAction: (FishingAction) -> Unit, onBack: () -> Unit, onResult: () -> Unit) {
    LaunchedEffect(state.canGoResult) {
        if (state.canGoResult) onResult()
    }
    Column(modifier = Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Stage: ${state.currentStageId.uppercase()}", style = MaterialTheme.typography.headlineSmall)
        Text(state.instruction, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        when (state.phase) {
            FishingPhase.Idle -> TVButton("CAST (OK)") { onAction(FishingAction.Cast) }
            FishingPhase.WaitingBite -> GaugeBar("Bite Timer", state.biteProgress, Color.Cyan)
            FishingPhase.HookWindow -> {
                GaugeBar("Hook Window", state.hookWindowProgress, Color.Yellow)
                TVButton("HOOK NOW (OK)") { onAction(FishingAction.Hook) }
            }
            FishingPhase.Fighting -> {
                val fight = state.fightState
                if (fight != null) {
                    Text("Fish: ${fight.fishName}")
                    Text("State: ${fight.fishState}", color = if (fight.fishState == FishState.STRUGGLING) Color.Red else Color.Green)
                    GaugeBar("Distance", 1f - fight.fishDistanceToCatch / 100f, Color(0xFF66BB6A))
                    GaugeBar("Line", fight.lineDurability / 100f, Color(0xFFEF5350))
                    GaugeBar("Escape", fight.fishEscapeMeter / 100f, Color(0xFFFFA726))
                    TVButton("REEL TAP (OK)") { onAction(FishingAction.ReelTap) }
                }
            }
            else -> Text("...")
        }
        TVButton("Back to Menu", onBack)
    }
}

@Composable
fun ResultScreen(result: CatchResult?, onNext: () -> Unit, onMenu: () -> Unit, onRewardAction: (RewardAction) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (result == null) {
            Text("No Result")
        } else {
            Text(if (result.isSuccess) "Caught!" else "Failed", style = MaterialTheme.typography.displaySmall)
            Text("Fish: ${result.fishName}")
            Text("Size: ${result.sizeCm} cm")
            Text("Rarity: ${result.rarity}")
            Text("Coins: ${result.coin}")
            if (result.isNewRecord) Text("NEW RECORD!", color = Color.Yellow)
            result.failReason?.let { Text("Reason: $it", color = Color.Red) }

            Text("Reward Ads (stub)")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TVButton("Retry") { onRewardAction(RewardAction.RETRY_ESCAPE) }
                TVButton("2x") { onRewardAction(RewardAction.DOUBLE_REWARD) }
                TVButton("RareUp") { onRewardAction(RewardAction.BOOST_RARE_RATE) }
            }
        }
        TVButton("Fish Again", onNext)
        TVButton("Menu", onMenu)
    }
}

@Composable
fun FishDexScreen(entries: List<DexEntry>, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("FishDex", style = MaterialTheme.typography.headlineMedium)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(entries) { entry ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(if (entry.discovered) entry.name else "????", fontWeight = FontWeight.Bold)
                            Text("Rarity: ${entry.rarity}")
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Best: ${entry.bestSizeCm}cm")
                            Text("Caught: ${entry.catches}")
                        }
                    }
                }
            }
        }
        TVButton("Back", onBack)
    }
}

@Composable
fun ShopScreen(equipment: Map<EquipmentType, Equipment>, coin: Int, onBuy: (EquipmentType) -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Shop (MVP)", style = MaterialTheme.typography.headlineMedium)
        Text("Coins: $coin")
        equipment.values.forEach { eq ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${eq.type} Lv.${eq.level} (Cost ${eq.nextCost})")
                TVButton("Upgrade") { onBuy(eq.type) }
            }
        }
        TVButton("Back", onBack)
    }
}
