## New Features Implementation Guide

### 1. Behavioral Simulation Hook

**Location:** `app/src/main/java/com/grindrplus/hooks/BehavioralSimulation.kt`

#### What It Does
Simulates natural user behavior to avoid server detection by implementing:

- **Request Delay Simulation**: Adds random 500ms-3s delays between API calls to mimic natural browsing
- **Natural Network Timing**: Analyzes request patterns and adjusts timing based on request type
- **Keep-Alive Heartbeat**: Simulates natural connection keep-alive patterns (45-90s intervals)
- **Rate Limiting Compliance**: Respects Grindr's rate limits (60 profile fetches/min, 30 location updates/min)
- **Location Smoothing**: Gradual movement simulation instead of instant teleportation
- **Typing Simulation**: Simulates natural message typing speed (200ms per character + jitter)
- **User Agent Variation**: Introduces minor variations to avoid pattern matching

#### API Methods
```kotlin
// Add delay between requests
calculateDelay(requestType: String): Long

// Smooth location movement
smoothLocationUpdate(target/current lat/long): Pair<Double, Double>

// Get natural heartbeat interval
getHeartbeatInterval(): Long

// Check rate limit compliance
checkRateLimiting(requestType: String): Boolean

// Get natural typing delay
getTypingSimulationDelay(messageLength: Int): Long
```

#### Usage
The hook is automatically enabled and works in the background. No configuration needed.

---

### 2. Profile Analytics Dashboard

**Files:**
- `app/src/main/java/com/grindrplus/commands/Analytics.kt` - Command module
- `app/src/main/java/com/grindrplus/persistence/model/ProfileAnalyticsEntity.kt` - Database models
- `app/src/main/java/com/grindrplus/persistence/dao/ProfileAnalyticsDao.kt` - Database access
- `app/src/main/java/com/grindrplus/hooks/ProfileAnalyticsTracker.kt` - Event tracking

#### What It Does
Tracks and displays comprehensive user analytics including:

- **Profile Views**: Track all profiles you view with timestamp and source
- **Daily Summary**: Total views, unique profiles, messages sent/received today
- **Hourly Breakdown**: Visual hourly distribution of profile views
- **Top Profiles**: Most-viewed profiles over the last 7 days
- **Message Analytics**: Count messages in conversations
- **Data Export**: Export analytics to CSV format

#### Database Schema

**ProfileAnalyticsEntity** - Stores each profile view
```kotlin
- profileId: String (Primary Data)
- viewedAt: Long (Timestamp)
- viewSource: String (browse/cascade/search/favorite)
```

**MessageAnalyticsEntity** - Stores message statistics
```kotlin
- conversationId: String
- messageCount: Int
- lastMessageTime: Long
- firstMessageTime: Long
- totalCharacters: Int
```

**AnalyticsSummaryEntity** - Daily summary statistics
```kotlin
- date: Long (Day start time)
- totalViewsToday: Int
- totalMessagesReceived: Int
- totalMessagesSent: Int
- totalCharactersSent: Int
- uniqueProfilesToday: Int
- averageResponseTime: Long
```

#### Commands

**View Today's Summary**
```
/analytics
/analytics summary
```
Shows overview for the current day.

**View Profile Views**
```
/analytics views [days]
/analytics views 7        # Last 7 days (default)
/analytics views 30       # Last 30 days
```

**View Most Visited Profiles**
```
/analytics top
```
Shows top 10 profiles viewed in the last 7 days with view counts.

**Hourly Breakdown**
```
/analytics hourly
```
Shows visual hourly distribution (with ASCII bars) for today's views.

**Export Data**
```
/analytics export
```
Exports all analytics from the last 30 days to CSV format.

#### Example Output

```
📊 PROFILE ANALYTICS - TODAY

Total Views: 42
Unique Profiles: 38

Messages Received: 12
Messages Sent: 8
Characters Sent: 1,234

Avg Response Time: 2m 15s

Last Update: just now
```

```
👀 VIEWS - Last 7 Days

Total Views: 287
Unique Profiles: 245
Average/Day: 41

Recent Views:
• profile_id_123 - Jan 30, 14:32 (browse)
• profile_id_456 - Jan 30, 14:28 (cascade)
...
and 277 more
```

```
🏆 TOP 10 PROFILES (Last 7 Days)

1. profile_id_123 - viewed 8 times
2. profile_id_456 - viewed 6 times
3. profile_id_789 - viewed 5 times
...
```

```
📈 HOURLY BREAKDOWN - TODAY

00:00 | (0)
01:00 | (0)
...
14:00 | ██████████████ (15)
15:00 | ███████████ (12)
16:00 | █████████████████ (18)
...
```

#### Features

✅ **Automatic Tracking** - Profile views and messages are tracked automatically
✅ **Privacy** - All data stored locally, never sent to servers
✅ **Performance** - Lightweight database queries
✅ **Customizable** - Easy to extend tracking logic
✅ **Export** - Share or backup analytics data

#### Technical Details

**ProfileAnalyticsTracker Hook:**
- Hooks into Profile class constructors to detect views
- Hooks into ConversationMessage to track messages
- Records data asynchronously to avoid UI blocking
- Gracefully handles missing class references

**Analytics Command Module:**
- Integrates with existing CommandHandler system
- Uses Kotlin coroutines for async database queries
- Shows data in dialog format (consistent with other commands)
- Cleans up old data automatically

---

## Configuration

Both features are **enabled by default** but can be disabled in settings:

1. **BehavioralSimulation** - Disable if you experience slowdowns
2. **ProfileAnalytics** - Disable if you don't need tracking

### Adjusting Parameters

**BehavioralSimulation delays:**
```kotlin
requestDelayRange = 500L..3000L  // Modify request delay range
locationUpdateDelay = 30000L..180000L  // Modify location update frequency
```

**Analytics retention:**
- Old profile views older than 90 days auto-delete
- Message data persists indefinitely
- Manual cleanup can be added if needed

---

## Integration with Existing Code

### Database Integration
- Registered in `GPDatabase.kt` (version bumped to 6)
- Uses Room ORM like existing entities
- Uses same `DateConverter` for timestamp handling

### Hook Integration
- Registered in `HookManager.kt`
- Follows existing Hook class pattern
- Integrates with Config system for enable/disable

### Command Integration
- Registered in `CommandHandler.kt`
- Follows `CommandModule` interface
- Shows results in standard AlertDialog format

---

## Future Enhancements

**Possible additions:**
1. **Graph Visualization** - Render analytics as charts
2. **Predictive Analytics** - Suggest active times based on history
3. **Comparison Stats** - Compare current activity to historical averages
4. **Alerts** - Notify on unusual patterns (potential ban risk)
5. **Advanced Filtering** - Filter views by profile type, distance, etc.
6. **Integration with Location Hook** - Correlate views with location changes

---

## Performance Considerations

- Database queries limited to 10,000 records at a time
- Async execution prevents UI blocking
- Lightweight tracking with minimal overhead
- Automatic data cleanup prevents database bloat

---

## Troubleshooting

**No analytics showing?**
- Ensure `ProfileAnalyticsTracker` is enabled
- Check that database initialization succeeded
- Try restarting the app

**Performance issues?**
- Disable `BehavioralSimulation` hook if delays are too slow
- Reduce `requestDelayRange` values
- Clear old analytics data

**Command not recognized?**
- Ensure `Analytics` is added to CommandHandler
- Try exact command syntax: `/analytics summary`

---

## Statistics

**Files Added:** 5
**Files Modified:** 3
**Total Lines Added:** ~1,200
**Database Version:** Updated from 5 to 6
