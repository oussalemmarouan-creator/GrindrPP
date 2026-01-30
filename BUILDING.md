# 🔨 Building GrindrPlus APK

This guide explains how to build the GrindrPlus Android APK with all the new features (Behavioral Simulation & Profile Analytics).

## 📋 Prerequisites

You need the following installed on your machine:

- **Java Development Kit (JDK)** - Version 17 or higher
  - Download: https://www.oracle.com/java/technologies/downloads/
  
- **Android SDK** - API 24+ (will be installed automatically)
  - Can be installed via Android Studio or command line

- **Git** - For cloning the repository
  - Download: https://git-scm.com/download

## 🚀 Quick Start

### Step 1: Clone the Repository

```bash
git clone https://github.com/R0rt1z2/GrindrPlus.git
cd GrindrPlus
```

### Step 2: Install Android SDK (First Time Only)

```bash
# Make the script executable
chmod +x install_sdk.sh

# Run the installation script
./install_sdk.sh
```

This will download and install:
- Android SDK Platform Tools
- Android SDK Build Tools (34.0.0)
- Android Platform 34
- Required Java dependencies

**Installation takes 10-15 minutes** depending on your internet speed.

### Step 3: Build the APK

```bash
# Make the build script executable
chmod +x build.sh

# Run the build script
./build.sh
```

The build will:
1. Compile Kotlin code
2. Compile Java resources
3. Bundle resources
4. Create the APK

**Build time:** 5-10 minutes first time, 2-3 minutes afterwards

### Step 4: Find Your APK

After a successful build, your APK will be at:

```
app/build/outputs/apk/debug/app-debug.apk
```

File size: ~15-20 MB

## 🛠️ Manual Build (Advanced)

If you prefer to build manually without scripts:

### Set Environment Variables

**Linux/Mac:**
```bash
export JAVA_HOME=/path/to/jdk
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH
```

**Windows (PowerShell):**
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:ANDROID_HOME = "$env:USERPROFILE\Android\Sdk"
$env:PATH += ";$env:ANDROID_HOME\cmdline-tools\latest\bin;$env:ANDROID_HOME\platform-tools"
```

### Create local.properties

In the GrindrPlus root directory, create `local.properties`:

```properties
sdk.dir=/path/to/Android/Sdk
ndk.dir=/path/to/Android/Sdk/ndk/26.1.10909125
```

### Build with Gradle

**Debug APK:**
```bash
./gradlew assembleDebug
```

**Release APK:**
```bash
./gradlew assembleRelease
```

## 📱 Installing the APK

### Via Android Device (USB)

```bash
# Connect your device via USB
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Manual Installation

1. Copy the APK file to your Android device
2. Open file manager on device
3. Navigate to APK file
4. Tap to install
5. Grant necessary permissions

### Via LSPosed/LSPatch

1. Transfer APK to device
2. Open LSPosed or LSPatch manager
3. Install module
4. Follow on-screen prompts
5. Restart device or Grindr app

## ✅ Verification

### Check Build Success

```bash
# Check if APK exists and get size
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

### Check APK Contents

```bash
# View files in APK
unzip -l app/build/outputs/apk/debug/app-debug.apk | grep "com/grindrplus"
```

## 🐛 Troubleshooting

### "SDK location not found"

**Solution:** Create `local.properties` file:

```bash
echo "sdk.dir=$HOME/Android/Sdk" > local.properties
```

### "Java version not supported"

**Solution:** Check your Java version:

```bash
java -version
```

Must be Java 17+. If not, update Java.

### "ANDROID_SDK_ROOT not set"

**Solution:** Set the environment variable:

```bash
export ANDROID_HOME=$HOME/Android/Sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME
```

### Build is very slow

**Solution:** This is normal for first build. Subsequent builds are faster. 

To speed up:
- Use `--parallel` flag: `./gradlew assembleDebug --parallel`
- Use daemon: `./gradlew assembleDebug --daemon`
- Increase Gradle heap: `org.gradle.jvmargs=-Xmx4g`

### "Command not found: gradlew"

**Solution:** Make it executable:

```bash
chmod +x gradlew
./gradlew assembleDebug
```

### Build fails with "No matching variant"

**Solution:** Make sure you have the correct Android SDK versions:

```bash
# Install correct versions
sdkmanager "platforms;android-34" "build-tools;34.0.0"
```

## 📊 Build Output

When successful, you'll see:

```
BUILD SUCCESSFUL in Xs

========================================
  ✓ BUILD SUCCESSFUL!
========================================

APK Location:
-rw-r--r-- 1 user group 18M Jan 30 19:40 app-debug.apk

Next Steps:
1. Transfer APK to Android device
2. Install via LSPosed/LSPatch
3. Enable 'BehavioralSimulation' hook
4. Enable 'Profile Analytics' hook
5. Restart Grindr
6. Test: /analytics summary
```

## 🔒 Security Note

The APK is **debug signed** by default. For production/release:

1. Create a signing key:
```bash
keytool -genkey -v -keystore my-release-key.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias my-key-alias
```

2. Sign with release build:
```bash
./gradlew assembleRelease \
  -Pandroid.injected.signing.store.file=/path/to/key.jks \
  -Pandroid.injected.signing.store.password=password \
  -Pandroid.injected.signing.key.alias=alias \
  -Pandroid.injected.signing.key.password=password
```

## 📖 Additional Resources

- [Android Developers - Build from Command Line](https://developer.android.com/studio/build/building-cmdline)
- [Gradle Android Plugin](https://developer.android.com/studio/releases/gradle-plugin)
- [GrindrPlus GitHub](https://github.com/R0rt1z2/GrindrPlus)

## ✨ New Features in This Build

This build includes:

### Behavioral Simulation
- Request delay simulation (500-3000ms)
- Location smoothing (gradual movement)
- Keep-alive heartbeat (45-90s)
- Rate limit compliance
- Typing simulation
- User agent variation

### Profile Analytics
- Profile view tracking
- Message counting
- Daily summaries  
- Hourly breakdown
- Top profiles ranking
- CSV export

**Use:** Type `/analytics summary` in any Grindr chat

## 🆘 Getting Help

If you encounter issues:

1. Check this guide's troubleshooting section
2. Review the error message carefully
3. Search GitHub issues: https://github.com/R0rt1z2/GrindrPlus/issues
4. Check Android documentation

## 📝 Notes

- Building from source requires ~2 GB of disk space
- First build takes longest (downloads dependencies)
- Subsequent builds are much faster
- APK is compatible with Android 7.0+ (API 24+)

---

**Happy building! 🚀**

If you have questions, check [QUICK_REFERENCE.md](QUICK_REFERENCE.md) for feature usage.
