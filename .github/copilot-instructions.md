# Copilot Instructions for Vialert

## Project context
- This is an Android app built with Kotlin and Jetpack Compose.
- Module layout is currently single-module: `:app`.
- Package root: `com.example.myapplication`.
- Current app uses feature-based folders and layered packages:
  - `core/` for shared navigation, theme, and utilities.
  - `features/` for screen-level UI and ViewModels.
  - `domain/` for business models.
  - `data/` for data sources and repositories.
  - `di/` for Hilt modules (currently minimal/empty).

## Tech stack and versions
- Kotlin: `2.2.x`
- Android Gradle Plugin: `8.13.x`
- Compose BOM: `2026.02.00`
- Navigation Compose: `2.9.7`
- Hilt: `2.57.2` (+ KSP)
- Kotlin Serialization JSON: `1.10.0`
- Coil 3: `3.4.0`
- DataStore Preferences: `1.2.0`
- Min SDK: `28`, Target/Compile SDK: `36`

## Build and dependency rules
- Always use the version catalog in `gradle/libs.versions.toml`.
- Do not hardcode dependency versions in `app/build.gradle.kts`.
- Prefer existing libraries already in this project before introducing new ones.
- If adding a new library, add it to `libs.versions.toml` and reference it via `libs.*`.

## Architecture and code organization
- Keep files in their feature or layer package. Do not place new screens in `core/`.
- Keep business logic out of composables when possible.
- Use ViewModel for validation and state transitions.
- Keep domain models in `domain/model` and avoid Android framework dependencies there.
- Add reusable UI/theme helpers under `core/theme` or `core/utils`.

## Navigation conventions
- The app uses typed routes with Kotlin serialization in `core/navigation/MainRoutes.kt`.
- When adding a new destination:
  - Add a `@Serializable data object` route that extends `MainRoutes`.
  - Register the destination in `core/navigation/AppNavigation.kt`.
  - Keep route names clear and feature-aligned.
- Keep `AppNavigation` focused on route wiring, not screen business logic.

## Compose and UI guidelines
- Prefer state hoisting and unidirectional data flow.
- Keep composables small and single-purpose.
- Use `MaterialTheme` tokens from `core/theme` rather than inline styling where possible.
- Use `Modifier` chaining clearly and consistently.
- Keep preview functions lightweight and deterministic.

## ViewModel and state guidelines
- Expose immutable UI state when possible.
- Keep validation logic centralized in ViewModel or dedicated validators.
- Avoid side effects inside composables except through controlled effect APIs.
- Prefer explicit UI events over scattered callback logic.

## Strings, resources, and localization
- Avoid introducing new hardcoded user-facing strings in composables.
- Prefer string resources in `app/src/main/res/values/strings.xml`.
- Keep content descriptions meaningful for accessibility.

## Testing expectations
- Add or update unit tests for ViewModel/domain logic in `app/src/test`.
- Add UI tests in `app/src/androidTest` for critical user flows.
- Keep tests focused on behavior, not implementation details.

## Code style expectations
- Prefer clear names over abbreviations.
- Avoid placeholder TODO implementations in final code output.
- Keep functions short and extract helpers when logic grows.
- Match existing Kotlin and Compose formatting style used in this repository.

## When generating code changes
- Provide complete, compile-ready snippets.
- Include required imports.
- Do not rename packages, app id, or module structure unless explicitly requested.
- Preserve existing behavior unless the task explicitly requires behavior changes.
