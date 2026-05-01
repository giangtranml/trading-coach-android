package com.rein.tradingcoach.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor(@ApplicationContext context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "rein_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    private val _isLoggedIn = MutableStateFlow(prefs.contains(KEY_JWT))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    val token: String? get() = prefs.getString(KEY_JWT, null)

    fun saveToken(jwt: String) {
        prefs.edit().putString(KEY_JWT, jwt).apply()
        _isLoggedIn.value = true
    }

    fun logout() {
        prefs.edit().remove(KEY_JWT).apply()
        _isLoggedIn.value = false
    }

    companion object {
        private const val KEY_JWT = "jwt_token"
    }
}
