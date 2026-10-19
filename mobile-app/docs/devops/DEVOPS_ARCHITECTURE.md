# Mamelodi Initiative — DevOps architecture

## Delivery flow
Developer workstation → feature branch and meaningful commits → Azure Repos → pull request/review → Azure Pipelines → Gradle build and unit tests → build result and debug APK artifact → physical-device validation.

Azure Boards tracks work from New → Active → Resolved/Closed. Link pull requests to work items where practical.

## Service responsibilities
- **Azure Repos:** source code and history. Keep `main` stable; use `feature/<short-name>` branches.
- **Azure Boards:** backlog, ownership, progress and bugs.
- **Azure Pipelines:** automatically run Android build and JVM unit tests on push/PR to `main` and `develop`.
- **README:** prerequisites, setup, architecture, build/test instructions and team workflow.

## Security and evidence
Never commit passwords, tokens, signing keys, service-account JSON or personal access tokens. Keep evidence genuine: do not claim a pipeline passed until a real run succeeds. Pipeline build success does not replace physical-device testing.

## Working while school DevOps access is blocked
1. Continue in the existing local Git repository.
2. Make small, meaningful local commits if Git is configured.
3. Keep backlog and architecture documentation in the repository.
4. When access returns, create/import the Azure project, push to Azure Repos, create a pipeline from `azure-pipelines.yml`, authorize it, and verify a real successful run.

## Planned system boundaries
- Android: Kotlin, Jetpack Compose, ViewModels.
- Authentication: Firebase Authentication, email/password in this milestone.
- User preferences/profile: after auth verification.
- REST API + relational database: separate later milestone to satisfy the brief's API and database-table requirements.
- Avoid duplicating the same source of truth across Firestore and SQL without a clear reason.

## Milestone gates
1. Existing app builds and installs.
2. Firebase email registration/login verified.
3. Core screens and validation completed.
4. API and relational schema deployed; seed at least 10 records in each applicable table.
5. Automated tests cover important business logic.
6. Azure Pipeline succeeds after access is restored.
7. Complete workflow is tested on a physical device and genuine demo evidence captured.
