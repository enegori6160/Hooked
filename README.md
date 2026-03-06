# Hooked! MVP (Google TV / Android TV)

Retro 2D fishing MVP built with Kotlin + Jetpack Compose.

## Build

1. Install Android Studio Iguana+ and Android SDK 34.
2. Run:

```bash
./gradlew assembleDebug
```

3. Install on TV / emulator:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## MVP Scope

- Title / Main menu / Stage select
- Fishing loop (Cast -> Bite -> Hook -> Fight)
- Result screen with size + rarity + coins + new record
- FishDex with discovered/undiscovered and records
- Simple shop upgrades
- Save/Load with DataStore
- Reward action hooks for future ad insertion

## Directory layout

- `app/src/main/java/com/hooked/app`: app root, navigation, ViewModel, UI state
- `app/src/main/java/com/hooked/data`: save/store/master/repository
- `app/src/main/java/com/hooked/domain`: core models + usecases
- `app/src/main/java/com/hooked/game`: fishing state and fight engine
- `app/src/main/java/com/hooked/ui`: screens/components/theme
- `app/src/main/java/com/hooked/util`: ad gateway interface stubs

## TODO (post-MVP)

- Integrate real sound effects + pixel assets
- Add ad SDK implementation for `RewardedAdsGateway`
- Expand stages (River/Ocean/Tropical/Ice/Deep Sea)
- Time-of-day and event fish tables
- Add retry flow tied to rewarded ad result
