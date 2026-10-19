# Mamelodi Initiative — Task 2 Overlay

Add these files to your existing Android project; this is not a replacement project.

1. Back up your project.
2. Extract this ZIP into the project root, preserving paths.
3. Merge the Gradle changes in `gradle/FIREBASE_GRADLE_CHANGES.md`.
4. Wrap your existing root composable in `AuthGate` as shown in `integration/MAINACTIVITY_INTEGRATION.md`.
5. Sync Gradle, build, and test on a physical phone.

Google Sign-In is intentionally excluded. Keep your existing `app/google-services.json`; do not post it publicly.

This overlay adds Firebase email/password authentication and prepares Azure DevOps files. It does not replace existing screens or implement the required REST API, relational database, user settings, or all app features. Azure Pipeline/Boards files are templates and require a real Azure DevOps project and successful run before they count as evidence.
