package com.hooked.app

sealed class Route(val route: String) {
    data object Title : Route("title")
    data object Menu : Route("menu")
    data object StageSelect : Route("stage_select")
    data object Fishing : Route("fishing")
    data object Result : Route("result")
    data object Dex : Route("dex")
    data object Shop : Route("shop")
}
