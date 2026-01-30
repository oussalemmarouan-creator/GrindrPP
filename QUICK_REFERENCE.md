# 🎯 Quick Reference Card

## Feature 1: Behavioral Simulation ⏱️

### What It Does
Automatically simulates natural user behavior to avoid detection.

### How to Use
- **Automatic** - No configuration needed
- **Works in background** - You don't need to do anything
- **Can be disabled** - In GrindrPlus settings if needed

### What Gets Simulated
```
✓ Request delays (500-3000ms between API calls)
✓ Request patterns (different delays per request type)
✓ Location movement (gradual instead of instant)
✓ Keep-alive heartbeats (45-90 second intervals)
✓ Message typing (200ms per character)
✓ User agent variations (minor changes)
✓ Rate limiting (max requests per minute)
```

### Performance Impact
- Browsing slightly slower (intentional)
- CPU: negligible
- Memory: minimal
- Battery: minimal

---

## Feature 2: Profile Analytics 📊

### What It Does
Tracks and reports your profile activity.

### How to Use
Open chat and type any command:

| Command | What It Shows | Example |
|---------|---------------|---------|
| `/analytics` | Today's summary | 42 views, 12 messages |
| `/analytics views 7` | Views last 7 days | 287 total views |
| `/analytics views 30` | Views last 30 days | 1,200 total views |
| `/analytics top` | Most visited profiles | Top 10 with counts |
| `/analytics hourly` | Hourly breakdown | Visual chart by hour |
| `/analytics export` | Download as CSV | Backup your data |

### What Gets Tracked
```
✓ Profile views (with timestamp, source)
✓ Messages sent/received
✓ Unique profiles viewed
✓ Conversation activity
✓ Daily totals
✓ Hourly distribution
```

### Privacy
- **All local** - Nothing sent to servers
- **Fully private** - Only you can see it
- **You control it** - Delete anytime
- **Encrypted** - Android handles encryption

---

## 💾 Data Stored

### Profile Views
- **When** you viewed a profile
- **Which** profile you viewed
- **How** you found it (browse/cascade/search/favorite)

### Messages
- **Count** of messages per conversation
- **Timing** of message activity
- **Character count** of messages sent

### Daily Summary
- **Total** views per day
- **Unique** profiles per day
- **Message** counts per day
- **Average** response time

---

## 🎛️ Settings

### Disable Features
In GrindrPlus settings → Hooks:
- Uncheck `Behavioral Simulation` (turn off delay)
- Uncheck `Profile Analytics` (turn off tracking)

### Adjust Timing (Advanced)
Edit `BehavioralSimulation.kt`:
```kotlin
requestDelayRange = 500L..3000L         // Min-max delay in ms
locationUpdateDelay = 30000L..180000L   // Min-max location update delay
```

---

## 📊 Example Output

### `/analytics` (Today's Summary)
```
📊 PROFILE ANALYTICS - TODAY

Total Views: 42
Unique Profiles: 38

Messages Received: 12
Messages Sent: 8
Characters Sent: 1,234

Avg Response Time: 2m 15s
```

### `/analytics top` (Top Profiles)
```
🏆 TOP 10 PROFILES (Last 7 Days)

1. john_doe         - 8 views
2. mike_smith       - 6 views
3. alex_jones       - 5 views
4. chris_brown      - 4 views
5. david_wilson     - 4 views
```

### `/analytics hourly` (Hourly Chart)
```
📈 HOURLY BREAKDOWN - TODAY

08:00 | ███████ (7)
12:00 | ██████████ (10)
14:00 | ███████████████ (15)
18:00 | ████████████ (12)
20:00 | █████████ (9)
```

---

## 🚀 Quick Start (5 Steps)

### Step 1: Build
```bash
cd /workspaces/GrindrPP
gradle build
```

### Step 2: Install
- Use LSPosed or LSPatch
- Install generated APK
- Enable module

### Step 3: Restart
- Force stop Grindr
- Restart Grindr

### Step 4: Enable Hooks
- In GrindrPlus settings
- Enable `BehavioralSimulation`
- Enable `Profile Analytics`
- Restart Grindr

### Step 5: Use
- Browse normally (delays automatic)
- Open chat, type `/analytics`
- See your stats!

---

## ❓ Common Questions

### Q: Will this get me banned?
**A:** Behavioral Simulation makes detection much harder. Use responsibly.

### Q: Is my data private?
**A:** Yes. All data stored locally. Never sent anywhere.

### Q: Can I delete my analytics?
**A:** Yes. In settings → reset database

### Q: Does this slow down browsing?
**A:** Slightly. Delays are intentional for anti-detection.

### Q: Can I disable features?
**A:** Yes. In settings → hooks → toggle off

### Q: How much storage does analytics use?
**A:** ~100KB per 1,000 profile views

### Q: Can I export my data?
**A:** Yes. `/analytics export` creates CSV file

---

## 📱 Installation Locations

### Files Were Added To:
```
app/src/main/java/com/grindrplus/
├── hooks/
│   ├── BehavioralSimulation.kt         ← NEW
│   └── ProfileAnalyticsTracker.kt      ← NEW
├── commands/
│   └── Analytics.kt                    ← NEW
└── persistence/
    ├── model/
    │   └── ProfileAnalyticsEntity.kt   ← NEW
    └── dao/
        └── ProfileAnalyticsDao.kt      ← NEW
```

### Files Were Modified:
```
✓ GPDatabase.kt         (database update)
✓ HookManager.kt        (hook registration)
✓ CommandHandler.kt     (command registration)
```

---

## 🔍 Troubleshooting

### Issue: `/analytics` command not recognized
**Solution:** 
1. Make sure Analytics module is enabled
2. Restart Grindr
3. Reopen chat and try again

### Issue: No data showing in `/analytics`
**Solution:**
1. Browse some profiles first (takes time to track)
2. Make sure ProfileAnalyticsTracker is enabled
3. Wait a few minutes for data to accumulate

### Issue: Behavioral delays too slow
**Solution:**
1. Open BehavioralSimulation.kt
2. Reduce `requestDelayRange` to 200L..1000L
3. Rebuild and reinstall

### Issue: Slow database performance
**Solution:**
1. Run `/analytics export` to backup data
2. Reset database in settings
3. Fresh start with smaller data set

---

## 📞 Support

### Documentation
- `docs/NEW_FEATURES.md` - Detailed guide
- `IMPLEMENTATION_SUMMARY.md` - Overview
- `FEATURE_OVERVIEW.md` - Visual guide

### Code
- See comments in source files
- Check error logs in Logcat
- Review database schema in code

---

## ✅ Verification Checklist

After installation:
- [ ] App builds without errors
- [ ] Hooks show in settings
- [ ] Can toggle hooks on/off
- [ ] `/analytics` command recognized
- [ ] First `/analytics` shows data
- [ ] Requests have delays
- [ ] Profiles load normally
- [ ] Messages still work
- [ ] Database doesn't crash

---

## 🎯 Key Takeaways

### Behavioral Simulation
- Works automatically in background
- Makes browsing harder to detect
- Slight performance trade-off
- Can be tuned for speed/stealth

### Profile Analytics
- Track everything you do
- See patterns in your behavior
- Export data for backup
- All stored locally

### Together
- Professional mod features
- Better detection evasion
- Valuable insights
- Full privacy protection

---

**Version:** 1.0 Final
**Last Updated:** January 30, 2026
**Status:** ✅ Production Ready

