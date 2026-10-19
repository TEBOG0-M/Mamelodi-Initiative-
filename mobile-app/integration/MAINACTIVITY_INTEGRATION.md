# Integrate AuthGate

In `MainActivity.kt`, keep the existing theme and `setContent` block. Wrap your current root composable in `AuthGate`; do not create a second `setContent` or delete your navigation.

Example (replace `ExistingAppRoot()` with your actual root composable):
```kotlin
import com.mamelodiinitiative.auth.AuthGate

setContent {
    YourExistingTheme {
        AuthGate {
            ExistingAppRoot()
        }
    }
}
```

If your existing root is `AppNavigation()`, use that instead of `ExistingAppRoot()`. Preserve your actual theme name.

## Acceptance checks
- Fresh app data opens the auth screen.
- Invalid email and password under six characters show validation.
- Registration creates a user visible in Firebase Console → Authentication → Users.
- Correct credentials open the existing app.
- Wrong credentials show a message without crashing.
- Force-stop/relaunch retains a Firebase session.
- Test on the physical phone.
