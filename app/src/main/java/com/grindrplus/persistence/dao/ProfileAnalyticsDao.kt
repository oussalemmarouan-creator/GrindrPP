package com.grindrplus.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.grindrplus.persistence.model.ProfileAnalyticsEntity
import com.grindrplus.persistence.model.MessageAnalyticsEntity
import com.grindrplus.persistence.model.AnalyticsSummaryEntity

@Dao
interface ProfileAnalyticsDao {
    
    @Insert
    suspend fun insertProfileView(analytics: ProfileAnalyticsEntity)
    
    @Query("SELECT COUNT(*) FROM ProfileAnalyticsEntity WHERE viewedAt >= :since")
    suspend fun getViewCountSince(since: Long): Int
    
    @Query("SELECT COUNT(DISTINCT profileId) FROM ProfileAnalyticsEntity WHERE viewedAt >= :since")
    suspend fun getUniqueProfilesSince(since: Long): Int
    
    @Query("SELECT * FROM ProfileAnalyticsEntity ORDER BY viewedAt DESC LIMIT :limit")
    suspend fun getRecentViews(limit: Int = 50): List<ProfileAnalyticsEntity>
    
    @Query("SELECT * FROM ProfileAnalyticsEntity WHERE profileId = :profileId ORDER BY viewedAt DESC LIMIT :limit")
    suspend fun getProfileViews(profileId: String, limit: Int = 10): List<ProfileAnalyticsEntity>
    
    @Query("DELETE FROM ProfileAnalyticsEntity WHERE viewedAt < :beforeTime")
    suspend fun deleteOldViews(beforeTime: Long)
    
    // Message Analytics
    @Insert
    suspend fun insertMessageAnalytics(analytics: MessageAnalyticsEntity)
    
    @Query("UPDATE MessageAnalyticsEntity SET messageCount = messageCount + 1, lastMessageTime = :timestamp WHERE conversationId = :conversationId")
    suspend fun incrementMessageCount(conversationId: String, timestamp: Long)
    
    @Query("SELECT * FROM MessageAnalyticsEntity ORDER BY lastMessageTime DESC LIMIT :limit")
    suspend fun getRecentConversations(limit: Int = 20): List<MessageAnalyticsEntity>
    
    // Summary Analytics
    @Insert
    suspend fun insertDailySummary(summary: AnalyticsSummaryEntity)
    
    @Query("SELECT * FROM AnalyticsSummaryEntity ORDER BY date DESC LIMIT :days")
    suspend fun getLastDaysSummary(days: Int = 7): List<AnalyticsSummaryEntity>
    
    @Query("SELECT * FROM AnalyticsSummaryEntity WHERE date = :date LIMIT 1")
    suspend fun getDailySummary(date: Long): AnalyticsSummaryEntity?
}
