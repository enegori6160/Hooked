package com.hooked.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hooked.data.local.SaveDataStore
import com.hooked.data.repository.GameRepository
import com.hooked.domain.model.CatchResult
import com.hooked.domain.model.Equipment
import com.hooked.domain.model.FishRecord
import com.hooked.domain.model.RewardAction
import com.hooked.domain.usecase.RunFishingLoopUseCase
import com.hooked.game.battle.FightEngine
import com.hooked.game.fishing.FishingPhase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.min

class HookedViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = GameRepository(SaveDataStore(app.applicationContext))
    private val runFishingLoopUseCase = RunFishingLoopUseCase(repository)
    private val fightEngine = FightEngine()

    private val _uiState = MutableStateFlow(
        HookedUiState(stages = repository.getStages())
    )
    val uiState: StateFlow<HookedUiState> = _uiState.asStateFlow()

    private var waitingJob: Job? = null
    private var hookJob: Job? = null
    private var fightJob: Job? = null
    private var tapPower: Float = 0f
    private var rewardReserved: RewardAction? = null

    init {
        viewModelScope.launch {
            repository.progressFlow.collect { progress ->
                _uiState.update {
                    it.copy(
                        progress = progress,
                        dexEntries = repository.buildDex(progress)
                    )
                }
            }
        }
    }

    fun startFishingStage(stageId: String) {
        _uiState.update {
            it.copy(
                fishingUi = it.fishingUi.copy(currentStageId = stageId, phase = FishingPhase.Idle, instruction = "Press OK to cast")
            )
        }
    }

    fun onFishingAction(action: FishingAction) {
        when (action) {
            FishingAction.Cast -> castLine()
            FishingAction.Hook -> hookFish()
            FishingAction.ReelTap -> tapPower = (tapPower + 0.15f).coerceAtMost(1f)
        }
    }

    private fun castLine() {
        val current = _uiState.value.fishingUi
        if (current.phase != FishingPhase.Idle) return

        val fish = runFishingLoopUseCase.drawFish(current.currentStageId, rewardReserved == RewardAction.BOOST_RARE_RATE)
        val waitMs = runFishingLoopUseCase.biteWaitMs(fish)
        _uiState.update {
            it.copy(
                fishingUi = it.fishingUi.copy(
                    phase = FishingPhase.WaitingBite,
                    selectedFishName = fish.name,
                    instruction = "Waiting for a bite...",
                    biteProgress = 0f
                )
            )
        }

        waitingJob?.cancel()
        waitingJob = viewModelScope.launch {
            val total = waitMs.toFloat()
            var elapsed = 0L
            while (elapsed < waitMs) {
                delay(50)
                elapsed += 50
                _uiState.update { ui ->
                    ui.copy(fishingUi = ui.fishingUi.copy(biteProgress = (elapsed / total).coerceIn(0f, 1f)))
                }
            }
            openHookWindow(fish.id)
        }
    }

    private fun openHookWindow(fishId: String) {
        val fish = repository.getFish(fishId) ?: return
        val window = fish.hookWindowMs
        _uiState.update {
            it.copy(
                fishingUi = it.fishingUi.copy(
                    phase = FishingPhase.HookWindow,
                    instruction = "BITE! Press OK now!",
                    hookWindowProgress = 1f
                )
            )
        }
        hookJob?.cancel()
        hookJob = viewModelScope.launch {
            var left = window
            while (left > 0) {
                delay(33)
                left -= 33
                _uiState.update { ui ->
                    ui.copy(fishingUi = ui.fishingUi.copy(hookWindowProgress = (left.toFloat() / window).coerceAtLeast(0f)))
                }
            }
            failAndBuildResult(fishId, "Missed the hook timing")
        }
    }

    private fun hookFish() {
        val ui = _uiState.value.fishingUi
        if (ui.phase != FishingPhase.HookWindow) return
        hookJob?.cancel()
        val fish = repository.getFishByStage(ui.currentStageId).firstOrNull { it.name == ui.selectedFishName } ?: return
        val lineLevel = _uiState.value.progress.equipment[com.hooked.domain.model.EquipmentType.LINE]?.level ?: 1
        var fightState = fightEngine.initial(fish, lineLevel)

        _uiState.update {
            it.copy(fishingUi = it.fishingUi.copy(phase = FishingPhase.Fighting, instruction = "Tap OK to reel. Rest during STRUGGLING!", fightState = fightState))
        }

        fightJob?.cancel()
        fightJob = viewModelScope.launch {
            while (true) {
                delay(100)
                fightState = fightEngine.tick(fightState, fish, tapPower, 100)
                tapPower *= 0.85f
                _uiState.update { state ->
                    state.copy(fishingUi = state.fishingUi.copy(fightState = fightState))
                }

                when {
                    fightState.fishDistanceToCatch <= 0f -> {
                        completeCatch(fish.id)
                        return@launch
                    }

                    fightState.fishEscapeMeter >= 100f -> {
                        failAndBuildResult(fish.id, "Fish escaped")
                        return@launch
                    }

                    fightState.lineDurability <= 0f -> {
                        failAndBuildResult(fish.id, "Line snapped")
                        return@launch
                    }

                    fightState.timeLeftMs <= 0 -> {
                        failAndBuildResult(fish.id, "Timed out")
                        return@launch
                    }
                }
            }
        }
    }

    private fun completeCatch(fishId: String) {
        val fish = repository.getFish(fishId) ?: return
        val result = runFishingLoopUseCase.buildResult(fish, _uiState.value.progress, success = true)
        applyResult(result)
    }

    private fun failAndBuildResult(fishId: String, reason: String) {
        val fish = repository.getFish(fishId) ?: return
        val result = runFishingLoopUseCase.buildResult(fish, _uiState.value.progress, success = false, failReason = reason)
        applyResult(result)
    }

    private fun applyResult(result: CatchResult) {
        viewModelScope.launch {
            val current = _uiState.value.progress
            val currentRecord = current.fishRecords[result.fishId] ?: FishRecord(result.fishId)
            val updatedRecord = currentRecord.copy(
                discovered = currentRecord.discovered || result.isSuccess,
                bestSizeCm = if (result.isSuccess) maxOf(currentRecord.bestSizeCm, result.sizeCm) else currentRecord.bestSizeCm,
                catches = currentRecord.catches + if (result.isSuccess) 1 else 0
            )
            val rewardMultiplier = if (rewardReserved == RewardAction.DOUBLE_REWARD) 2 else 1
            val updatedProgress = current.copy(
                coins = current.coins + (result.coin * rewardMultiplier),
                fishRecords = current.fishRecords + (result.fishId to updatedRecord)
            )
            rewardReserved = null
            repository.saveProgress(updatedProgress)
            _uiState.update {
                it.copy(
                    lastCatch = result.copy(coin = result.coin * rewardMultiplier),
                    fishingUi = it.fishingUi.copy(phase = FishingPhase.Finished, canGoResult = true, instruction = "Result ready")
                )
            }
        }
    }

    fun prepareNextCast() {
        waitingJob?.cancel(); hookJob?.cancel(); fightJob?.cancel()
        tapPower = 0f
        _uiState.update {
            it.copy(
                fishingUi = it.fishingUi.copy(
                    phase = FishingPhase.Idle,
                    instruction = "Press OK to cast",
                    biteProgress = 0f,
                    hookWindowProgress = 1f,
                    fightState = null,
                    canGoResult = false
                )
            )
        }
    }

    fun buyUpgrade(type: com.hooked.domain.model.EquipmentType) {
        viewModelScope.launch {
            val progress = _uiState.value.progress
            val eq = progress.equipment[type] ?: return@launch
            if (progress.coins < eq.nextCost) return@launch

            val next = Equipment(type, eq.level + 1, 50 * (eq.level + 1))
            val newMap = progress.equipment + (type to next)
            repository.saveProgress(progress.copy(coins = progress.coins - eq.nextCost, equipment = newMap))
        }
    }

    fun reserveRewardAction(action: RewardAction) {
        // TODO(ad): replace with RewardedAdsGateway.show(action) callback result.
        rewardReserved = action
        if (action == RewardAction.RESTORE_LINE) {
            _uiState.update {
                val fight = it.fishingUi.fightState ?: return@update it
                it.copy(fishingUi = it.fishingUi.copy(fightState = fight.copy(lineDurability = min(100f, fight.lineDurability + 40f))))
            }
        }
    }

}

enum class FishingAction { Cast, Hook, ReelTap }
