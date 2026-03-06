package com.hooked.util

import com.hooked.domain.model.RewardAction

interface RewardedAdsGateway {
    suspend fun showRewarded(action: RewardAction): Boolean
}

class StubRewardedAdsGateway : RewardedAdsGateway {
    override suspend fun showRewarded(action: RewardAction): Boolean = true
}
