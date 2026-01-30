package com.grindrplus.hooks

import com.grindrplus.core.Logger
import com.grindrplus.core.LogSource
import com.grindrplus.utils.Hook
import com.grindrplus.utils.HookStage
import com.grindrplus.utils.hook
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.random.Random
import java.util.concurrent.atomic.AtomicLong

/**
 * Behavioral Simulation Hook
 * 
 * Simulates natural user behavior to avoid detection by:
 * - Adding random delays between requests
 * - Simulating natural network timing patterns
 * - Implementing geolocation smoothing
 * - Matching Grindr's rate limiting patterns
 */
class BehavioralSimulation : Hook(
    "Behavioral Simulation",
    "Simulates natural user behavior to avoid server detection"
) {
    private val lastRequestTime = AtomicLong(0)
    private val requestDelayRange = 500L..3000L  // 500ms to 3 seconds
    private val locationUpdateDelay = 30000L..180000L  // 30s to 3min between location updates
    private val lastLocationUpdate = AtomicLong(System.currentTimeMillis())
    
    // Track API call patterns
    private var lastRequestType = ""
    private var consecutiveRequests = 0
    
    override fun init() {
        // Hook into OkHttp3 request interceptor to add delays
        try {
            findClass("okhttp3.RealInterceptorChain")
                .hook("proceed", HookStage.BEFORE) { param ->
                    val request = param.args.getOrNull(0)
                    if (request is okhttp3.Request) {
                        val currentRequestType = extractRequestType(request)
                        
                        // Add natural delay between requests
                        val delay = calculateDelay(currentRequestType)
                        if (delay > 0) {
                            Thread.sleep(delay)
                        }
                        
                        lastRequestTime.set(System.currentTimeMillis())
                        lastRequestType = currentRequestType
                    }
                }
        } catch (e: Exception) {
            Logger.w("Failed to hook RealInterceptorChain: ${e.message}", LogSource.HOOKS)
        }
    }
    
    private fun extractRequestType(request: okhttp3.Request): String {
        val url = request.url.toString()
        return when {
            url.contains("/profiles/") && request.method == "GET" -> "profile_fetch"
            url.contains("/profiles/") && request.method == "PUT" -> "profile_update"
            url.contains("/chat") -> "chat"
            url.contains("/media") -> "media"
            url.contains("/location") -> "location"
            url.contains("/search") -> "search"
            url.contains("/favorites") -> "favorites"
            url.contains("/blocks") -> "blocks"
            else -> "other"
        }
    }
    
    private fun calculateDelay(requestType: String): Long {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastRequest = currentTime - lastRequestTime.get()
        
        // If this is the same type of request, add more delay (less rapid)
        val baseDelay = when {
            lastRequestType == requestType -> requestDelayRange.first + Random.nextLong(2000)
            else -> requestDelayRange.first + Random.nextLong(1500)
        }
        
        // Only apply delay if last request was very recent
        return if (timeSinceLastRequest < baseDelay) {
            baseDelay - timeSinceLastRequest
        } else {
            0L
        }
    }
    
    /**
     * Smooth location updates to simulate gradual movement
     * instead of instant teleportation
     */
    fun smoothLocationUpdate(
        targetLatitude: Double,
        targetLongitude: Double,
        currentLatitude: Double,
        currentLongitude: Double
    ): Pair<Double, Double> {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastUpdate = currentTime - lastLocationUpdate.get()
        
        // Check if enough time has passed for a location update
        val minDelay = locationUpdateDelay.first
        if (timeSinceLastUpdate < minDelay) {
            return Pair(currentLatitude, currentLongitude)
        }
        
        // Calculate distance and smoothly move towards target
        val latDiff = targetLatitude - currentLatitude
        val longDiff = targetLongitude - currentLongitude
        
        // Move only 30-70% of the distance per update (gradual movement)
        val movePercentage = 0.3 + Random.nextDouble() * 0.4
        
        val smoothLat = currentLatitude + (latDiff * movePercentage)
        val smoothLong = currentLongitude + (longDiff * movePercentage)
        
        lastLocationUpdate.set(currentTime)
        
        return Pair(smoothLat, smoothLong)
    }
    
    /**
     * Simulate natural keep-alive heartbeat patterns
     */
    fun getHeartbeatInterval(): Long {
        // Natural heartbeat every 45-90 seconds with jitter
        return 45000L + Random.nextLong(45000L)
    }
    
    /**
     * Match Grindr's rate limiting (e.g., max 60 profile fetches per minute)
     */
    fun checkRateLimiting(requestType: String): Boolean {
        val maxRequestsPerMinute = when (requestType) {
            "profile_fetch" -> 60
            "chat" -> 100
            "location" -> 30
            "search" -> 40
            "media" -> 50
            else -> 100
        }
        
        // This would require request counting, placeholder for now
        consecutiveRequests++
        
        return consecutiveRequests <= maxRequestsPerMinute
    }
    
    /**
     * Add randomized user agent variations
     */
    fun getVariedUserAgent(baseUserAgent: String): String {
        val variations = listOf(
            baseUserAgent,
            baseUserAgent.replace("Android ", "Android ") + " (slight variant)",
            baseUserAgent
        )
        return variations.random()
    }
    
    /**
     * Simulate natural typing speed for message input
     */
    fun getTypingSimulationDelay(messageLength: Int): Long {
        // Average typing speed: 40 WPM = ~200ms per character
        val baseDelay = messageLength * 200L
        // Add jitter (±20%)
        val jitter = baseDelay / 5
        return baseDelay + Random.nextLong(-jitter, jitter)
    }
}
