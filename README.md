# Echo — Android application

Echo is the Android application for the provided mobile application assignment. It uses Kotlin, AndroidX Fragments, and a Navigation Drawer.

## Open in Android Studio

1. Extract this archive.
2. In Android Studio, choose **Open** and select the extracted \`Echo\` folder (the folder containing \`settings.gradle.kts\`).
3. Allow Gradle sync to finish.
4. Select the \`app\` run configuration and launch it on an emulator or Android device.

## Build and test on Windows

From the \`Echo\` project root in PowerShell:

\`\`\`powershell
.\gradlew.bat --no-daemon clean testDebugUnitTest assembleDebug
\`\`\`

The debug APK is generated at:

\`\`\`text
app\build\outputs\apk\debug\app-debug.apk
\`\`\`

To install it on a connected device/emulator:

\`\`\`powershell
adb install -r .\app\build\outputs\apk\debug\app-debug.apk
\`\`\`

## Features

- Navigation drawer: Broadcast Receiver, Image Scale, Video, Audio.
- Custom broadcast path: select Custom, enter plain text, and proceed to the receiving activity. \`CustomBroadcastActivity\` registers a custom \`BroadcastReceiver\`, sends the message through the app-scoped broadcast, receives it in \`onReceive()\`, and displays the received message.
- Battery path: select the system battery receiver option to display battery percentage updates. This branch does not enter the custom-message receiving activity.
- Internet image loading with pinch-to-zoom.
- In-app video playback uses \`MediaPlayer\` with a \`TextureView\` output surface, Play/Pause/Replay controls, and a first-frame rendering check.
- In-app audio playback uses \`MediaPlayer\` with explicit music audio attributes, audio-focus handling, asynchronous preparation, and visible playback status.

## Package identity

- Application name: \`Echo\`
- Application ID: \`com.example.echo\`
- Project name: \`Echo\`

The original assignment's supplied learning/reference links and their implementation notes are in \`docs/ASSIGNMENT_REFERENCE_NOTES.md\`.
