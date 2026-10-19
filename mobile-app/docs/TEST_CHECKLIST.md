# Task 2 verification checklist

## Firebase email/password
- [ ] Email/Password provider enabled.
- [ ] Registration succeeds on physical device.
- [ ] Account appears in Firebase Authentication → Users.
- [ ] Existing account can sign in.
- [ ] Invalid email and short password rejected.
- [ ] Wrong credentials show readable error.
- [ ] Network failure does not crash app.
- [ ] Session persists after force-stop/relaunch.

## Build and CI
- [ ] `./gradlew assembleDebug` succeeds.
- [ ] `./gradlew testDebugUnitTest` succeeds.
- [ ] Azure Pipeline runs after access is restored.
- [ ] Test results and debug APK artifact are published.

## Remaining Task 2 requirements
- [ ] Registration/login.
- [ ] Passwords handled securely by Firebase; no raw passwords stored.
- [ ] User settings.
- [ ] Required API implemented and integrated.
- [ ] Relational tables created with at least 10 records per applicable table.
- [ ] Invalid input handled without crashes.
- [ ] Help/user documentation.
- [ ] Full system tested on a physical device.
- [ ] Final voiceover demo shows app and genuine hosted auth/API/database/DevOps evidence.
