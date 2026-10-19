# Firebase Gradle changes — merge, do not replace

In root `build.gradle.kts`, add to the existing `plugins` block if absent:
```kotlin
id("com.google.gms.google-services") version "4.4.2" apply false
```

In `app/build.gradle.kts`, add to the existing `plugins` block:
```kotlin
id("com.google.gms.google-services")
```

Add to the existing `dependencies` block:
```kotlin
implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
implementation("com.google.firebase:firebase-auth")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")
```

Do not add a second Firebase BoM if one already exists. Keep all existing Compose, Navigation, lifecycle, Android and JVM 17 settings.

Check that `app/google-services.json` matches package `com.mamelodiinitiative`, and that Email/Password is enabled in Firebase Console. Sync, then build. If Gradle reports conflicts, share both Gradle files and the exact error instead of adding more versions at random.
