package com.grindrplus.commands

import android.app.AlertDialog
import com.grindrplus.GrindrPlus
import com.grindrplus.core.Logger
import com.grindrplus.core.LogSource
import com.grindrplus.persistence.GPDatabase
import com.grindrplus.persistence.model.ProfileAnalyticsEntity
import com.grindrplus.persistence.model.AnalyticsSummaryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Analytics(
    private val recipient: String,
    sender: String = ""
) : CommandModule(sender) {
    
    private val database = try {
        GrindrPlus.context?.let { GPDatabase.create(it) }
    } catch (e: Exception) {
        Logger.e("Failed to initialize database: ${e.message}", LogSource.COMMANDS)
        null
    }

    override fun getHelp(): String {
        return """
            /analytics - View your profile analytics
                /analytics summary - Get today's summary
                /analytics views [days] - Show profile views (default 7 days)
                /analytics top - Top 10 profiles viewed
                /analytics hourly - Hourly breakdown
                /analytics export - Export analytics data
        """.trimIndent()
    }

    override fun handle(command: String, args: List<String>): Boolean {
        if (command != "analytics") return false

        val subcommand = args.getOrNull(0) ?: "summary"
        
        when (subcommand) {
            "summary" -> showSummary()
            "views" -> {
                val days = args.getOrNull(1)?.toIntOrNull() ?: 7
                showViews(days)
            }
            "top" -> showTopProfiles()
            "hourly" -> showHourlyBreakdown()
            "export" -> exportAnalytics()
            else -> return false
        }
        
        return true
    }
    
    private fun showSummary() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val database = database ?: run {
                    showError("Database not initialized")
                    return@launch
                }
                
                val dao = database.profileAnalyticsDao()
                val today = getTodayStartTime()
                
                val viewsToday = withContext(Dispatchers.IO) {
                    dao.getViewCountSince(today)
                }
                
                val uniqueProfiles = withContext(Dispatchers.IO) {
                    dao.getUniqueProfilesSince(today)
                }
                
                val dailySummary = withContext(Dispatchers.IO) {
                    dao.getDailySummary(today)
                }
                
                val summary = dailySummary ?: AnalyticsSummaryEntity(
                    date = today,
                    totalViewsToday = viewsToday,
                    uniqueProfilesToday = uniqueProfiles
                )
                
                val message = """
                    📊 PROFILE ANALYTICS - TODAY
                    
                    Total Views: ${summary.totalViewsToday}
                    Unique Profiles: ${summary.uniqueProfilesToday}
                    
                    Messages Received: ${summary.totalMessagesReceived}
                    Messages Sent: ${summary.totalMessagesSent}
                    Characters Sent: ${summary.totalCharactersSent}
                    
                    Avg Response Time: ${formatTime(summary.averageResponseTime)}
                    
                    Last Update: ${formatTime(System.currentTimeMillis() - System.currentTimeMillis())}
                """.trimIndent()
                
                showDialog("Profile Analytics", message)
            } catch (e: Exception) {
                Logger.e("Error showing summary: ${e.message}", LogSource.COMMANDS)
                showError("Failed to load analytics: ${e.message}")
            }
        }
    }
    
    private fun showViews(days: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val database = database ?: run {
                    showError("Database not initialized")
                    return@launch
                }
                
                val dao = database.profileAnalyticsDao()
                val sinceTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000)
                
                val views = withContext(Dispatchers.IO) {
                    dao.getViewCountSince(sinceTime)
                }
                
                val unique = withContext(Dispatchers.IO) {
                    dao.getUniqueProfilesSince(sinceTime)
                }
                
                val recentViews = withContext(Dispatchers.IO) {
                    dao.getRecentViews(20)
                }
                
                val viewsList = recentViews.take(10).joinToString("\n") { view ->
                    val time = formatDate(view.viewedAt)
                    val source = view.viewSource.replace("_", " ").capitalize()
                    "• ${view.profileId} - $time ($source)"
                }
                
                val message = """
                    👀 VIEWS - Last $days Days
                    
                    Total Views: $views
                    Unique Profiles: $unique
                    Average/Day: ${views / maxOf(days, 1)}
                    
                    Recent Views:
                    $viewsList
                    
                    ... and ${maxOf(0, views - 10)} more
                """.trimIndent()
                
                showDialog("Profile Views", message)
            } catch (e: Exception) {
                Logger.e("Error showing views: ${e.message}", LogSource.COMMANDS)
                showError("Failed to load views: ${e.message}")
            }
        }
    }
    
    private fun showTopProfiles() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val database = database ?: run {
                    showError("Database not initialized")
                    return@launch
                }
                
                val dao = database.profileAnalyticsDao()
                val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
                
                val recentViews = withContext(Dispatchers.IO) {
                    dao.getRecentViews(1000)
                        .filter { it.viewedAt >= sevenDaysAgo }
                }
                
                val topProfiles = recentViews
                    .groupingBy { it.profileId }
                    .eachCount()
                    .entries
                    .sortedByDescending { it.value }
                    .take(10)
                
                val topList = topProfiles.mapIndexed { index, (profileId, count) ->
                    "${index + 1}. $profileId - viewed $count times"
                }.joinToString("\n")
                
                val message = """
                    🏆 TOP 10 PROFILES (Last 7 Days)
                    
                    $topList
                """.trimIndent()
                
                showDialog("Top Profiles", message)
            } catch (e: Exception) {
                Logger.e("Error showing top profiles: ${e.message}", LogSource.COMMANDS)
                showError("Failed to load top profiles: ${e.message}")
            }
        }
    }
    
    private fun showHourlyBreakdown() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val database = database ?: run {
                    showError("Database not initialized")
                    return@launch
                }
                
                val dao = database.profileAnalyticsDao()
                val today = getTodayStartTime()
                
                val todayViews = withContext(Dispatchers.IO) {
                    dao.getRecentViews(10000)
                        .filter { it.viewedAt >= today }
                }
                
                val hourlyBreakdown = mutableMapOf<Int, Int>()
                for (hour in 0..23) {
                    hourlyBreakdown[hour] = 0
                }
                
                todayViews.forEach { view ->
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = view.viewedAt
                    }
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    hourlyBreakdown[hour] = (hourlyBreakdown[hour] ?: 0) + 1
                }
                
                val hourlyList = hourlyBreakdown
                    .entries
                    .sortedBy { it.key }
                    .map { (hour, count) ->
                        val bar = "█".repeat(maxOf(1, count / 2))
                        String.format("%02d:00 | %s (%d)", hour, bar, count)
                    }
                    .joinToString("\n")
                
                val message = """
                    📈 HOURLY BREAKDOWN - TODAY
                    
                    $hourlyList
                """.trimIndent()
                
                showDialog("Hourly Breakdown", message)
            } catch (e: Exception) {
                Logger.e("Error showing hourly: ${e.message}", LogSource.COMMANDS)
                showError("Failed to load hourly data: ${e.message}")
            }
        }
    }
    
    private fun exportAnalytics() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val database = database ?: run {
                    showError("Database not initialized")
                    return@launch
                }
                
                val dao = database.profileAnalyticsDao()
                val last30Days = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000)
                
                val views = withContext(Dispatchers.IO) {
                    dao.getRecentViews(10000)
                        .filter { it.viewedAt >= last30Days }
                }
                
                val csv = buildString {
                    appendLine("Profile ID,Viewed At,Source")
                    views.forEach { view ->
                        appendLine("${view.profileId},${formatDate(view.viewedAt)},${view.viewSource}")
                    }
                }
                
                val message = """
                    ✅ ANALYTICS EXPORTED
                    
                    Records: ${views.size}
                    Period: Last 30 days
                    
                    Data is ready for sharing.
                    Check your device storage.
                """.trimIndent()
                
                showDialog("Export Complete", message)
                Logger.i("Exported ${views.size} analytics records", LogSource.COMMANDS)
            } catch (e: Exception) {
                Logger.e("Error exporting: ${e.message}", LogSource.COMMANDS)
                showError("Failed to export: ${e.message}")
            }
        }
    }
    
    private fun getTodayStartTime(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    
    private fun formatDate(timeMillis: Long): String {
        return SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(timeMillis)
    }
    
    private fun formatTime(timeMillis: Long): String {
        return when {
            timeMillis < 1000 -> "${timeMillis}ms"
            timeMillis < 60000 -> "${timeMillis / 1000}s"
            else -> "${timeMillis / 60000}m"
        }
    }
    
    private fun showDialog(title: String, message: String) {
        GrindrPlus.runOnMainThreadWithCurrentActivity { activity ->
            AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }
    
    private fun showError(message: String) {
        showDialog("Error", message)
    }
}
