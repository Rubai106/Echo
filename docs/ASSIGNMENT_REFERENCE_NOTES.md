# Assignment 2 — notes on the supplied reference materials

This note maps all four URLs printed in the assignment PDF to the corresponding implementation. These references are learning material, not extra application features.

## 1. Android Developers — Broadcasts overview

https://developer.android.com/guide/components/broadcasts

Used for the dynamic broadcast-receiver pattern, explicit receiver registration/unregistration, and receiver export-security flags.

Relevant source:
- `app/src/main/java/com/example/echo/activities/CustomBroadcastActivity.kt`
- `app/src/main/java/com/example/echo/activities/BatteryActivity.kt`

The custom message receiver is registered with `ContextCompat.RECEIVER_NOT_EXPORTED` because it is intended to receive a broadcast sent only by this app. The battery receiver is registered separately with `ContextCompat.RECEIVER_EXPORTED` because it receives a system-originated broadcast. Each activity unregisters its receiver at the end of the relevant lifecycle scope.

## 2. Vogella — Android BroadcastReceiver tutorial

https://www.vogella.com/tutorials/AndroidBroadcastReceiver/article.html

Used as a complementary explanation of `BroadcastReceiver`, `IntentFilter`, dynamic registration, `onReceive()`, and receiver lifecycle.

Relevant source:
- `CustomBroadcastActivity.kt` creates an anonymous `BroadcastReceiver`, filters for the custom action, and displays the message in `onReceive()`.
- `BatteryActivity.kt` uses a separate receiver for battery notifications and unregisters it when the activity stops.

## 3. Android API — Intent.ACTION_BATTERY_CHANGED

https://developer.android.com/reference/android/content/Intent#ACTION_BATTERY_CHANGED

The API reference identifies `ACTION_BATTERY_CHANGED` as a sticky system broadcast carrying battery state and level information, and specifies that it must be received through explicit/dynamic registration rather than a manifest-declared receiver.

Relevant source:
- `BatteryActivity.kt` registers for `Intent.ACTION_BATTERY_CHANGED` while the activity is started.
- It reads `BatteryManager.EXTRA_LEVEL` and `BatteryManager.EXTRA_SCALE`, then uses `BatteryPercentage.calculate(level, scale)` to display the percentage.

## 4. Awesome Jetpack Compose learning resources

https://github.com/androiddevnotes/awesome-jetpack-compose-learning-resources

This is a curated learning list for Jetpack Compose, including layout, theming, navigation, accessibility, and testing material. It is included because the assignment lists it as a Jetpack learning resource.

The assignment's concrete implementation request is to practice Fragments and a navigation drawer. Therefore, this project keeps its required XML/Fragment/DrawerLayout structure and uses AndroidX libraries (AppCompat, Fragment, Core, DrawerLayout) and Material Components. It does not convert the required screens to Compose, which would change the specified practice scope.

## Custom-branch third activity explained

The third activity is `CustomBroadcastActivity`:

1. `CustomMessageActivity` sends the text to it as an Intent extra.
2. `CustomBroadcastActivity` registers the app's receiver for `BroadcastContract.ACTION_CUSTOM_MESSAGE`.
3. It sends a package-scoped broadcast containing the same text.
4. The receiver's `onReceive()` reads the text extra and updates `received_message_text`.

The second spinner option uses `BatteryActivity`; it does not enter this custom-broadcast third activity, matching the assignment's separate battery branch.
