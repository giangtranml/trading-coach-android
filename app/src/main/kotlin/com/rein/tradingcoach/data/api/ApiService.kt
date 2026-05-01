package com.rein.tradingcoach.data.api

import com.rein.tradingcoach.data.api.models.*
import retrofit2.http.*

interface ApiService {

    @POST("api/v1/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("api/v1/register")
    suspend fun register(@Body body: RegisterRequest): LoginResponse

    @GET("api/v1/me")
    suspend fun getProfile(): UserProfileResponse

    @GET("api/v1/dashboard")
    suspend fun getDashboard(): DashboardResponse

    @GET("api/v1/violations")
    suspend fun getViolations(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
    ): ViolationListResponse

    @GET("api/v1/violations/{id}")
    suspend fun getViolation(@Path("id") id: Int): ViolationResponse

    @GET("api/v1/settings")
    suspend fun getSettings(): UserSettings

    @PUT("api/v1/settings")
    suspend fun updateSettings(@Body body: UserSettings): UserSettings

    @POST("api/v1/devices")
    suspend fun registerDevice(@Body body: DeviceTokenRequest)

    @POST("api/v1/regenerate-api-key")
    suspend fun regenerateApiKey(): ApiKeyResponse
}
