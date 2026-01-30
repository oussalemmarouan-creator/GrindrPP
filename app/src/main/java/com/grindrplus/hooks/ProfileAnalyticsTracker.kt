package com.grindrplus.hooks

import com.grindrplus.GrindrPlus
import com.grindrplus.core.Logger
import com.grindrplus.core.LogSource
import com.grindrplus.persistence.model.ProfileAnalyticsEntity
import com.grindrplus.utils.Hook
import com.grindrplus.utils.HookStage
import com.grindrplus.utils.hook
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Profile Analytics Hook
 * 
 * Tracks profile views, messages, and other analytics for the dashboard
 */
class ProfileAnalyticsTracker : Hook(
    "Profile Analytics",
    "Tracks profile views and messages for analytics dashboard"
) {
    
    override fun init() {
        // Hook profile view events
        try {
            hookProfileViewEvents()
        } catch (e: Exception) {
            Logger.w("Failed to hook profile views: ${e.message}", LogSource.HOOKS)
        }
        
        // Hook message events
        try {
            hookMessageEvents()
        } catch (e: Exception) {
            Logger.w("Failed to hook message events: ${e.message}", LogSource.HOOKS)
        }
    }
    
    private fun hookProfileViewEvents() {
        // Hook into common profile display methods
        findClass("com.grindrapp.android.persistence.model.Profile")
            .hook("<init>", HookStage.AFTER) { param ->
                try {
                    // Extract profile ID from the Profile object
                    val profileId = invokeMethodSafe(param.thisObject, "getId") as? String
                        ?: getFieldSafe(param.thisObject, "id") as? String
                    
                    if (!profileId.isNullOrEmpty()) {
                        recordProfileView(profileId, "browse")
                    }
                } catch (e: Exception) {
                    Logger.d("Failed to track profile view: ${e.message}", LogSource.HOOKS)
                }
            }
    }
    
    private fun hookMessageEvents() {
        // Hook message sending
        try {
            findClass("com.grindrapp.android.chat.persistence.ConversationMessage")
                .hook("<init>", HookStage.AFTER) { param ->
                    try {
                        val conversationId = getFieldSafe(param.thisObject, "conversationId") as? String
                        if (!conversationId.isNullOrEmpty()) {
                            recordMessageEvent(conversationId)
                        }
                    } catch (e: Exception) {
                        Logger.d("Failed to track message: ${e.message}", LogSource.HOOKS)
                    }
                }
        } catch (e: Exception) {
            Logger.d("Failed to hook message events: ${e.message}", LogSource.HOOKS)
        }
    }
    
    private fun recordProfileView(profileId: String, source: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = GrindrPlus.database ?: return@launch
                val analytics = ProfileAnalyticsEntity(
                    profileId = profileId,
                    viewedAt = System.currentTimeMillis(),
                    viewSource = source
                )
                database.profileAnalyticsDao().insertProfileView(analytics)
            } catch (e: Exception) {
                Logger.d("Failed to record profile view: ${e.message}", LogSource.HOOKS)
            }
        }
    }
    
    private fun recordMessageEvent(conversationId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = GrindrPlus.database ?: return@launch
                database.profileAnalyticsDao().incrementMessageCount(
                    conversationId,
                    System.currentTimeMillis()
                )
            } catch (e: Exception) {
                Logger.d("Failed to record message: ${e.message}", LogSource.HOOKS)
            }
        }
    }
    
    private fun invokeMethodSafe(obj: Any?, methodName: String): Any? {
        return try {
            if (obj == null) return null
            val clazz = obj::class.java
            val method = clazz.getDeclaredMethod(methodName)
            method.isAccessible = true
            method.invoke(obj)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getFieldSafe(obj: Any?, fieldName: String): Any? {
        return try {
            if (obj == null) return null
            val clazz = obj::class.java
            val field = clazz.getDeclaredField(fieldName)
            field.isAccessible = true
            field.get(obj)
        } catch (e: Exception) {
            null
        }
    }
}
