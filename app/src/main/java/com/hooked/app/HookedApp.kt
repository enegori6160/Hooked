package com.hooked.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hooked.ui.screen.FishDexScreen
import com.hooked.ui.screen.FishingScreen
import com.hooked.ui.screen.MainMenuScreen
import com.hooked.ui.screen.ResultScreen
import com.hooked.ui.screen.ShopScreen
import com.hooked.ui.screen.SplashTitleScreen
import com.hooked.ui.screen.StageSelectScreen
import com.hooked.ui.theme.HookedTheme

@Composable
fun HookedApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val vm: HookedViewModel = viewModel()
    val uiState by vm.uiState.collectAsState()

    HookedTheme {
        NavHost(navController = navController, startDestination = Route.Title.route, modifier = modifier) {
            composable(Route.Title.route) {
                SplashTitleScreen(onStart = { navController.navigate(Route.Menu.route) })
            }
            composable(Route.Menu.route) {
                MainMenuScreen(
                    coin = uiState.progress.coins,
                    onStartFishing = { navController.navigate(Route.StageSelect.route) },
                    onOpenDex = { navController.navigate(Route.Dex.route) },
                    onOpenShop = { navController.navigate(Route.Shop.route) }
                )
            }
            composable(Route.StageSelect.route) {
                StageSelectScreen(stages = uiState.stages, onSelectStage = {
                    vm.startFishingStage(it.id)
                    navController.navigate(Route.Fishing.route)
                }, onBack = { navController.popBackStack() })
            }
            composable(Route.Fishing.route) {
                FishingScreen(state = uiState.fishingUi, onAction = vm::onFishingAction, onBack = {
                    navController.popBackStack(Route.Menu.route, false)
                }, onResult = {
                    navController.navigate(Route.Result.route)
                })
            }
            composable(Route.Result.route) {
                ResultScreen(
                    result = uiState.lastCatch,
                    onNext = {
                        vm.prepareNextCast()
                        navController.popBackStack(Route.Fishing.route, false)
                    },
                    onMenu = {
                        navController.popBackStack(Route.Menu.route, false)
                    },
                    onRewardAction = vm::reserveRewardAction
                )
            }
            composable(Route.Dex.route) {
                FishDexScreen(entries = uiState.dexEntries, onBack = { navController.popBackStack() })
            }
            composable(Route.Shop.route) {
                ShopScreen(
                    equipment = uiState.progress.equipment,
                    coin = uiState.progress.coins,
                    onBuy = vm::buyUpgrade,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
