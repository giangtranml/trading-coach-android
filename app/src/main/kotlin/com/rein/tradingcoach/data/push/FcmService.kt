package com.rein.tradingcoach.data.push

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rein.tradingcoach.MainActivity
import com.rein.tradingcoach.R
import com.rein.tradingcoach.ReinApplication
import com.rein.tradingcoach.data.api.ApiService
import com.rein.tradingcoach.data.api.models.DeviceTokenRequest
import com.rein.tradingcoach.ui.navigation.NavigationRouter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

    @Inject lateinit var apiService: ApiService

    override fun onNewToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                apiService.registerDevice(
                    DeviceTokenRequest(platform = "android", fcmToken = token)
                )
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: "Rein Alert"
        val body = message.notification?.body ?: message.data["body"] ?: ""
        val violationId = message.data["violation_id"]?.toIntOrNull()

        if (violationId != null) {
            NavigationRouter.pendingViolationId.value = violationId
        }

        showNotification(title, body, violationId)
    }

    private fun showNotification(title: String, body: String, violationId: Int?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            violationId?.let { putExtra(EXTRA_VIOLATION_ID, it) }
        }
        val pendingIntent = PendingIntent.getActivity(
            this, violationId ?: 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(this, ReinApplication.CHANNEL_VIOLATIONS)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(violationId ?: System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        const val EXTRA_VIOLATION_ID = "violation_id"
    }
}
