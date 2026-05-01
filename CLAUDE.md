# CLAUDE.md — trading-coach-android

Native Android app for Rein, the real-time trading discipline monitor.
Phase 13 of 13. Mirrors the iOS app (trading-coach-ios) in features and UX.

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material3
- **Architecture:** MVVM + Repository
- **DI:** Hilt
- **Networking:** Retrofit + OkHttp
- **Push:** Firebase Cloud Messaging (FCM)
- **Auth storage:** EncryptedSharedPreferences (Android Keystore)
- **Async:** Kotlin Coroutines + StateFlow
- **Navigation:** Compose Navigation + NavigationRouter

## Project Structure

```
app/src/main/kotlin/com/rein/tradingcoach/
  data/
    api/           ApiClient.kt, ApiService.kt, models/
    auth/          AuthManager.kt
    push/          FcmService.kt
  ui/
    screens/
      auth/        LoginScreen.kt, RegisterScreen.kt
      dashboard/   DashboardScreen.kt, DashboardViewModel.kt, AccountCard.kt, DisciplineScoreRing.kt
      violations/  ViolationHistoryScreen.kt, ViolationHistoryViewModel.kt,
                   ViolationDetailScreen.kt, ViolationDetailViewModel.kt, ViolationRowCard.kt
      settings/    SettingsScreen.kt, SettingsViewModel.kt
      onboarding/  ApiKeyDisplayScreen.kt, EaSetupGuideScreen.kt
    navigation/    NavGraph.kt, NavigationRouter.kt
    theme/         Theme.kt, Color.kt, Type.kt
  MainActivity.kt
  ReinApplication.kt
```

## Architecture Patterns

**MVVM + StateFlow** mirrors iOS @MainActor ViewModels:
```kotlin
@HiltViewModel
class XyzViewModel @Inject constructor(...) : ViewModel() {
    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun load() { viewModelScope.launch { /* ... */ } }
}
```

**iOS → Android pattern mapping:**

| iOS | Android |
|-----|---------|
| `@Published var x: T` | `MutableStateFlow<T>` |
| `actor APIClient` | `ApiService` (Retrofit suspend funs) |
| `AuthManager` (Keychain) | `AuthManager` (EncryptedSharedPreferences) |
| 401 → logout | OkHttp `Authenticator` |
| `NavigationRouter` | `NavigationRouter` singleton (StateFlow) |
| `debounceTask?.cancel()` | `debounceJob?.cancel()` + `delay(500)` |
| Infinite scroll `onAppear` | `LazyColumn` + `LaunchedEffect(lastItem)` |

## API Endpoints

Same REST API as iOS (all JWT Bearer except auth):

| Endpoint | Method |
|----------|--------|
| `/api/v1/login` | POST |
| `/api/v1/register` | POST |
| `/api/v1/me` | GET |
| `/api/v1/dashboard` | GET |
| `/api/v1/violations` | GET |
| `/api/v1/violations/{id}` | GET |
| `/api/v1/settings` | GET / PUT |
| `/api/v1/devices` | POST (platform: "android", fcm_token) |
| `/api/v1/regenerate-api-key` | POST |

## Environment

Base URL in `ApiClient.kt`:
- DEBUG: `http://10.0.2.2:8000` (Android emulator → localhost)
- RELEASE: `https://api.tradingcoach.app`

FCM config: `google-services.json` in `app/` directory (gitignored — provide per environment).

## Design Tokens

Color palette in `ui/theme/Color.kt` matches `Theme.swift` exactly:
- `TcBlue` = #3B82F6 (primary action)
- `TcGreen` = #10B981 (discipline score > 70)
- `TcAmber` = #F59E0B (score 40–70)
- `TcRed` = #EF4444 (score < 40, high severity)
- `TcPageBg` = #F1F5F9
- `TcCardBg` = #FFFFFF
- `TcTextPrimary` = #0F172A
- `TcTextSecondary` = #475569
- `TcBorder` = #CBD5E1

## FCM Push + Deep Links

1. `FcmService.onNewToken(token)` → `POST /api/v1/devices` with `platform: "android"`
2. `FcmService.onMessageReceived(msg)` → extracts `violation_id` from `msg.data`
3. `NavigationRouter.pendingViolationId.value = violationId`
4. `MainActivity` collects the StateFlow → navigates to `ViolationDetailScreen`

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew lint                   # Lint check
./gradlew test                   # Unit tests
./gradlew installDebug           # Install on connected device/emulator
```

## Agents

Use `engineering-lead` from the orchestrator for Android feature work.
No separate Android-specific agent exists for Phase 13.
