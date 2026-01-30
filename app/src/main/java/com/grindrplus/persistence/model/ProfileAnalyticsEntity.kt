package com.grindrplus.persistence.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProfileAnalyticsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: String,
    val viewedAt: Long,
    val viewSource: String = "browse" // browse, cascade, search, favorite
)

@Entity
data class MessageAnalyticsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: String,
    val messageCount: Int = 0,
    val lastMessageTime: Long = 0,
    val firstMessageTime: Long = 0,
    val totalCharacters: Int = 0
)

@Entity
data class AnalyticsSummaryEntity(
    @PrimaryKey val date: Long, // Date in milliseconds (start of day)
    val totalViewsToday: Int = 0,
    val totalMessagesReceived: Int = 0,
    val totalMessagesSent: Int = 0,
    val totalCharactersSent: Int = 0,
    val uniqueProfilesToday: Int = 0,
    val averageResponseTime: Long = 0 // in milliseconds
)
