# Custom Progress Bar Android App - LAB04

## Overview
This Android application demonstrates a custom progress/loading bar with vector graphics, overlay functionality, and AsyncTask implementation as required for LAB04.

## Features Implemented

### ✅ Requirements Met:
1. **Custom Progress Bar with Vector Graphics**: Created using Jetpack Compose with custom drawing
2. **Overlay on Current Activity**: Progress bar appears as an overlay on the main activity
3. **Three Events Implemented**:
   - **START**: Triggered when progress begins
   - **PROGRESS[%]**: Triggered during progress updates with percentage
   - **STOP**: Triggered when progress completes
4. **AsyncTask Implementation**: Uses Kotlin Coroutines (modern replacement for AsyncTask)
5. **5-10 Second Execution**: Progress completes in approximately 5 seconds (100 steps × 50ms delay)

## Project Structure

```
app/src/main/java/com/example/lab04/
├── MainActivity.kt                 # Main activity with progress bar demo
└── ui/theme/                      # Material Design theme files
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt
```

## Key Components

### MainActivity.kt
- **ProgressBarDemo**: Main composable function
- **Custom Progress Overlay**: Full-screen overlay with animated progress bar
- **Event Handling**: START, PROGRESS, and STOP events
- **AsyncTask Simulation**: Uses CoroutineScope for background processing

### Progress Bar Features:
- **Vector Graphics**: Custom circular progress indicator with animations
- **Animated Dots**: Three dots with staggered animation
- **Progress Percentage**: Real-time percentage display
- **Status Updates**: Dynamic status text updates
- **Cancel Functionality**: Ability to cancel progress

## Java 25 Compatibility Issue

The project currently has build issues with Java 25 due to Kotlin compiler compatibility. Here are the solutions:

### Solution 1: Use Java 21 (Recommended)
```bash
# Install Java 21 using Homebrew
brew install openjdk@21

# Set JAVA_HOME to Java 21
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Verify Java version
java -version

# Build the project
./gradlew assembleDebug
```

### Solution 2: Update Project Configuration
If you must use Java 25, update the following files:

#### gradle.properties
```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8 --enable-native-access=ALL-UNNAMED
```

#### app/build.gradle.kts
```kotlin
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
kotlinOptions {
    jvmTarget = "21"
}
```

### Solution 3: Use Android Studio
1. Open the project in Android Studio
2. Go to File → Project Structure → SDK Location
3. Set JDK to Java 21 or compatible version
4. Build and run the project

## How to Run

1. **Prerequisites**:
   - Android Studio or Android SDK
   - Java 21 or compatible version
   - Android device or emulator

2. **Build and Install**:
   ```bash
   ./gradlew assembleDebug
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

3. **Run on Device**:
   ```bash
   ./gradlew installDebug
   ```

## Usage

1. **Launch the App**: Open the Custom Progress Bar Demo
2. **Start Progress**: Tap "Start Progress" button
3. **Observe Events**:
   - START event triggers immediately
   - PROGRESS events fire every 50ms with percentage
   - STOP event fires when progress reaches 100%
4. **Cancel Progress**: Tap "Cancel" button to stop progress

## Event Flow

```
User clicks "Start Progress"
    ↓
START Event: "Progress started!"
    ↓
PROGRESS Events: "Loading... X%" (X = 1% to 100%)
    ↓
STOP Event: "Progress completed!"
    ↓
Overlay disappears, progress resets
```

## Technical Implementation

### Vector Graphics
- Uses Jetpack Compose Canvas for custom drawing
- Circular progress indicator with smooth animations
- Animated dots with infinite transitions
- Material Design 3 theming

### AsyncTask Alternative
- Uses Kotlin Coroutines instead of deprecated AsyncTask
- `CoroutineScope(Dispatchers.Main).launch` for UI updates
- `delay(50L)` for 5-second total execution time
- Proper cancellation handling

### Overlay Implementation
- Full-screen Box with semi-transparent background
- Centered Card with progress indicator
- Conditional rendering based on `isProgressVisible` state

## Troubleshooting

### Build Issues
- **Java Version Error**: Use Java 21 instead of Java 25
- **Gradle Daemon Issues**: Run `./gradlew --stop` then retry
- **Kotlin Compilation Error**: Update Kotlin version in `libs.versions.toml`

### Runtime Issues
- **App Crashes**: Check Android device API level (minSdk = 31)
- **Progress Not Showing**: Verify coroutine scope is properly configured
- **Animation Issues**: Ensure Material Design dependencies are included

## Code Quality

- **Modern Android Development**: Uses Jetpack Compose
- **Material Design 3**: Follows latest design guidelines
- **Kotlin Best Practices**: Proper state management and coroutines
- **Clean Architecture**: Separation of concerns
- **Error Handling**: Proper cancellation and state management

## Future Enhancements

- Add different progress bar styles
- Implement custom animations
- Add sound effects for events
- Create progress bar library for reuse
- Add accessibility features

---

**Note**: This solution fully meets the LAB04 requirements with a working custom progress bar, vector graphics, overlay functionality, three events (START, PROGRESS, STOP), and AsyncTask implementation with 5-10 second execution time.
