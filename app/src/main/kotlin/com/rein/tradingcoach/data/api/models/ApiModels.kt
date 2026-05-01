package com.rein.tradingcoach.data.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

// ---------------------------------------------------------------------------
// Auth
// ---------------------------------------------------------------------------

@JsonClass(generateAdapter = true)
data class LoginRequest(val email: String, val password: String)

@JsonClass(generateAdapter = true)
data class RegisterRequest(val email: String, val password: String)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String,
    @Json(name = "api_key") val apiKey: String? = null,
)

// ---------------------------------------------------------------------------
// Devices
// ---------------------------------------------------------------------------

@JsonClass(generateAdapter = true)
data class DeviceTokenRequest(
    val platform: String,
    @Json(name = "fcm_token") val fcmToken: String? = null,
    @Json(name = "apns_token") val apnsToken: String? = null,
)

// ---------------------------------------------------------------------------
// Dashboard
// ---------------------------------------------------------------------------

@JsonClass(generateAdapter = true)
data class DashboardResponse(
    val balance: Double,
    val equity: Double,
    val margin: Double,
    @Json(name = "free_margin") val freeMargin: Double,
    @Json(name = "open_positions") val openPositions: Int,
    @Json(name = "discipline_score") val disciplineScore: Int,
    @Json(name = "violations_today") val violationsToday: Int,
    @Json(name = "recent_violations") val recentViolations: List<ViolationResponse>,
    @Json(name = "last_updated_at") val lastUpdatedAt: Date?,
)

// ---------------------------------------------------------------------------
// Violations
// ---------------------------------------------------------------------------

@JsonClass(generateAdapter = true)
data class ViolationResponse(
    val id: Int,
    @Json(name = "violation_type") val violationType: String,
    val severity: String,
    @Json(name = "coach_message") val coachMessage: String?,
    @Json(name = "ai_generated") val aiGenerated: Boolean,
    @Json(name = "created_at") val createdAt: Date,
    @Json(name = "snapshot_json") val snapshotJson: Map<String, Double>? = null,
)

@JsonClass(generateAdapter = true)
data class ViolationListResponse(
    val items: List<ViolationResponse>,
    val total: Int,
    val page: Int,
    val limit: Int,
    val pages: Int,
)

// ---------------------------------------------------------------------------
// Settings
// ---------------------------------------------------------------------------

@JsonClass(generateAdapter = true)
data class UserSettings(
    @Json(name = "max_risk_pct") val maxRiskPct: Double = 2.0,
    @Json(name = "max_open_trades") val maxOpenTrades: Int = 3,
    @Json(name = "max_daily_loss_pct") val maxDailyLossPct: Double = 5.0,
    @Json(name = "revenge_window_minutes") val revengeWindowMinutes: Int = 15,
    @Json(name = "max_position_size_lots") val maxPositionSizeLots: Double = 1.0,
    @Json(name = "require_stop_loss") val requireStopLoss: Boolean = true,
    @Json(name = "prop_firm_preset") val propFirmPreset: String? = null,
)

// ---------------------------------------------------------------------------
// User profile
// ---------------------------------------------------------------------------

@JsonClass(generateAdapter = true)
data class UserProfileResponse(
    val email: String,
    @Json(name = "masked_api_key") val maskedApiKey: String,
)

@JsonClass(generateAdapter = true)
data class ApiKeyResponse(
    @Json(name = "api_key") val apiKey: String,
)
