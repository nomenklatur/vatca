# Vatca

**A minimal, offline-first PDF reader for Android.**

Vatca is a native Android reading app built for long-form study, research, and reading. It does three things deliberately well: it reads PDFs accurately, lets you tune the reading experience, and keeps a clean visual library of your documents. Everything stays on your device.

No account. No ads. No subscriptions. No analytics. No `INTERNET` permission.

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white&style=flat-square)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin&logoColor=white&style=flat-square)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white&style=flat-square)](https://developer.android.com/jetpack/compose)
[![minSdk](https://img.shields.io/badge/minSdk-24-0A7BBB?style=flat-square)](https://developer.android.com/tools/releases/platforms)
[![Offline](https://img.shields.io/badge/Offline--first-yes-2E7D32?style=flat-square)](#privacy-and-offline-operation)

---

## Table of contents

- [Overview](#overview)
- [Features](#features)
- [Screens](#screens)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [Product specification](#product-specification)
- [Getting started](#getting-started)
- [Configuration](#configuration)
- [Testing](#testing)
- [Known limitations](#known-limitations)
- [Contributing](#contributing)
- [Documentation](#documentation)
- [Acknowledgements](#acknowledgements)
- [License](#license)

---

## Overview

Most PDF readers on Android are either heavy utility suites or thin wrappers around a system viewer. Vatca takes a narrower position: a quiet, paper-first reading environment with a small, honest feature set.

The product follows a few firm principles:

- **Reading experience over feature quantity.** Chrome recedes while reading; the document is the interface.
- **Local-first and privacy-first.** Library data, reading progress, and preferences live in local storage. Nothing is uploaded.
- **Native Android.** Kotlin, Jetpack Compose, Material 3, and the platform's own PDF renderer. No WebView, no cross-platform runtime.
- **Predictable behavior.** Real page counts, real thumbnails generated from the documents themselves, and progress that reflects the actual reading position.
- **No unnecessary infrastructure.** A single Android module, a local database, and a preferences store.

Vatca is released as open source and is free to use, with no premium tier and no paywall.

---

## Features

### Library

- Import PDFs through Android's Storage Access Framework (`ActivityResultContracts.OpenDocument`), restricted to `application/pdf`.
- Persist read access to imported documents across app restarts where the provider supports it.
- Generate thumbnails from the **first page of each real PDF**; no fabricated covers.
- Show title, page count, reading progress, and last-opened metadata.
- Search the library by title or filename, case-insensitively.
- Sort by recently read, date added, title (A–Z), or reading progress.
- Switch between an adaptive grid and a compact list.
- Remove an entry from the library without deleting the original file. A confirmation dialog makes this explicit.
- Polished empty, importing, loading, and error states.
- Keep entries for moved or inaccessible files, with a clear recovery path when opening fails.

### Reader

- Accurate PDF rendering through Android's native `PdfRenderer`.
- Continuous vertical scrolling or page-by-page navigation.
- Page indicator, scrubber, previous/next controls, and tap zones in page mode.
- Pinch-to-zoom from 1.0x to 3.5x, double-tap zoom, and panning while zoomed.
- Fit-to-width and fit-to-page modes.
- Configurable page spacing and horizontal reading margins.
- Reader-scoped orientation preference: auto/sensor, portrait, or landscape.
- Immersive full-screen mode.
- Tap to show or hide reader controls.
- Restores the last page and scroll offset when reopening a document.
- Lazy page rendering with a bounded bitmap cache, so large PDFs do not load every page into memory.
- Dedicated loading and rendering-error states with retry.

### Reading customization

- Application theme: system, light, or dark.
- Reader backdrop: follow app, daylight, parchment, calm slate, or OLED black.
- Page switch animation: book-page flip, slide, or fade.
- Library defaults: grid/list layout and sorting order.
- In-app brightness override, scoped to the current window and restored on exit.
- All preferences persist locally through DataStore.

### Privacy and offline operation

- No user accounts, sign-in, profiles, or backend services.
- No cloud sync, advertisements, analytics SDKs, or advertising identifiers.
- No broad storage permissions; document access goes through SAF.
- No `INTERNET` permission in the manifest.
- No automatic uploads of documents or metadata.
- The full library and reader work without a network connection.

---

## Screens

| Screen | What it does |
|---|---|
| **Library** | Import, search, sort, switch grid/list, inspect progress, and remove entries. |
| **Reader** | Render and navigate the document; zoom, fit, change reader settings, toggle fullscreen and chrome. |
| **Settings** | Application theme, library defaults, reader defaults, and product information. |

The reader settings are also available as a bottom sheet from within the reader, so the reading flow is not interrupted.

---

## Tech stack

| Area | Technology |
|---|---|
| Language | Kotlin 2.2.10 |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM with unidirectional data flow |
| Navigation | Navigation Compose 2.8.9 |
| PDF rendering | Android `android.graphics.pdf.PdfRenderer` |
| Local database | Room 2.7.0 |
| Preferences | DataStore Preferences 1.1.7 |
| Async work | Kotlin Coroutines 1.10.2 |
| Image loading | Coil Compose 2.7.0 |
| Build system | Gradle 9.3.1 with Kotlin DSL and version catalogs |
| Android Gradle Plugin | 9.1.1 |
| SDK levels | `minSdk 24`, `targetSdk 36`, `compileSdk 36.1` |
| Java/Kotlin target | JVM 11 |

Dependency versions are centralized in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

---

## Architecture

Vatca is a single-module app (`:app`) organized by responsibility rather than by layer-heavy ceremony.

```text
app/src/main/java/com/example/
├── MainActivity.kt                  # Edge-to-edge Compose entry point
├── VatcaApplication.kt              # Builds Room, repository, and preferences
├── core/
│   └── pdf/
│       └── PdfRendererEngine.kt     # PDF session, page rendering, thumbnails
├── data/
│   ├── local/
│   │   ├── PdfDocumentDao.kt        # Room DAO
│   │   ├── PdfDocumentEntity.kt     # Persistence model
│   │   ├── PreferencesManager.kt    # DataStore preferences
│   │   └── VatcaDatabase.kt         # Room database
│   ├── model/
│   │   ├── PdfDocument.kt           # Domain model + progress calculation
│   │   └── ReaderSettings.kt        # Enums and UserPreferences
│   └── repository/
│       └── PdfRepository.kt         # Library + import + progress facade
└── ui/
    ├── library/                     # Library screen and ViewModel
    ├── reader/                      # Reader screen and ViewModel
    ├── settings/                    # Settings screen + reusable selectors
    ├── navigation/                  # Navigation Compose graph
    └── theme/                       # Editorial color, type, and Material theme
```

### Data flow

1. `VatcaApplication` creates the Room database, `PdfRepository`, and `PreferencesManager`.
2. Screen-level `ViewModel`s receive those dependencies through simple factories.
3. `PdfRepository` exposes Room data as `Flow<List<PdfDocument>>`.
4. Compose collects UI state with `collectAsStateWithLifecycle()` and renders immutable state objects.
5. The reader opens a `PdfDocumentSession` from the persisted URI; pages render on demand.
6. Reading progress is written back to Room after a short debounce and again when the reader is cleared.

### PDF rendering pipeline

The rendering layer is deliberately isolated from the UI:

- A document is opened once per reader session from its persisted content URI.
- `PdfRenderer` and its `ParcelFileDescriptor` are wrapped in `PdfDocumentSession`.
- All native renderer calls are serialized on a dedicated single-thread coroutine dispatcher.
- Page dimensions are read up front so the UI can reserve the correct aspect ratio.
- Bitmaps are rendered lazily, at the display width, clamped to 300–2048 px.
- A 48 MB `LruCache` keyed by page index bounds bitmap memory.
- Thumbnails are page 0 rendered to 360 px wide and compressed as JPEG at 85% into `filesDir/thumbnails/`.
- Sessions are closed in `ReaderViewModel.onCleared()`, which also flushes final reading progress.
- Rendering never happens on the main thread.

`android.graphics.pdf.PdfRenderer` was chosen over third-party PDF engines because it is part of the Android platform, adds no binary size, requires no native dependency management, and is sufficient for a fixed-layout PDF reader. The trade-off is that rendering quality and format support follow the platform implementation.

### Persistence

Room stores one row per library entry in `pdf_documents`:

| Column | Purpose |
|---|---|
| `id` | Stable UUID generated at import |
| `uri` | Persisted content URI |
| `title` | Display name without the `.pdf` suffix |
| `pageCount` | Page count read from the document |
| `lastReadPage` | Zero-based current page |
| `lastReadOffset` | Scroll offset within the page |
| `lastOpenedAt` | Last time the document was opened |
| `dateAdded` | Import timestamp |
| `thumbnailPath` | Path to the generated JPEG thumbnail |
| `fileSize` | Source file size when the provider exposes it |

DataStore holds application and reader preferences under `vatca_preferences`. Reading progress is derived from the actual page index and scroll offset, not from an arbitrary counter.

---

## Product specification

This section describes the scope Vatca is built against. The goal is a deliberately narrow product, not a general-purpose document suite.

### Functional requirements

#### 1. PDF library

| Requirement | Status |
|---|---|
| Import PDFs using the native Android document picker / SAF | Implemented |
| Persist URI access across restarts | Implemented, with graceful fallback when unsupported |
| Generate thumbnails from the first PDF page | Implemented |
| Display title, page count, progress, and recent activity | Implemented |
| Search by document title or filename | Implemented (title substring match) |
| Sort by recent, date added, title, or progress | Implemented |
| Grid and list presentation | Implemented |
| Remove a document from the library only | Implemented, behind confirmation |
| Empty, importing, loading, and error states | Implemented |
| Handle moved, deleted, or inaccessible sources | Implemented: entry is retained and opening shows an error with retry |
| Never recursively scan device storage | Respected |

#### 2. PDF reader

| Requirement | Status |
|---|---|
| Render real PDF pages accurately | Implemented with platform `PdfRenderer` |
| Show current page and total page count | Implemented |
| Navigate page by page | Implemented |
| Continuous vertical scrolling | Implemented |
| Page-by-page reading mode | Implemented |
| Zooming and panning | Implemented |
| Fit-to-width and fit-to-page | Implemented |
| Configurable page spacing | Implemented |
| Configurable reading margins | Implemented |
| Portrait and landscape support | Implemented |
| Optional full-screen reading | Implemented |
| Show/hide reader controls | Implemented |
| Save and restore reading position | Implemented |
| Handle large PDFs without retaining every page | Implemented with lazy rendering and a 48 MB cache |
| Loading and rendering-error states | Implemented |
| Avoid reloading the PDF when settings change | Implemented; the session stays in the ViewModel |
| Release renderer, bitmap, and file-descriptor resources | Implemented in `onCleared()` and session cleanup |

#### 3. Customizable reading experience

| Requirement | Status |
|---|---|
| System, light, and dark application themes | Implemented |
| Warm/sepia and other reader backdrops | Implemented |
| System theme following | Implemented |
| Continuous and page-by-page layouts | Implemented |
| Fit modes, spacing, margins, and zoom | Implemented |
| Reader chrome visibility and fullscreen | Implemented |
| Screen orientation preference | Implemented, scoped to the reader |
| Optional in-app brightness adjustment | Implemented, window-scoped and restored on exit |
| Library grid/list and sorting preferences | Implemented |
| Persist preferences locally | Implemented |

The reader backdrop changes the **viewer surface only**. PDF pages themselves remain fixed-layout and render on a white page background. Vatca does not reflow PDF text, change document font sizes, or recolor PDF content.

#### 4. Local persistence

| Requirement | Status |
|---|---|
| Library entries and source URIs | Implemented in Room |
| Document metadata and thumbnail references | Implemented |
| Last reading position and progress | Implemented |
| Global reading and library preferences | Implemented in DataStore |
| Per-document reading preferences | Not implemented; settings are global |
| Progress based on actual page/scroll position | Implemented |
| Avoid database writes on every scroll frame | Implemented with a 1.2 s debounce |
| Handle stale file references gracefully | Implemented |

### Non-functional requirements

- **Offline-first:** no network calls and no `INTERNET` permission.
- **Privacy:** no accounts, analytics, ads, tracking, or document uploads.
- **Scoped permissions:** SAF only; no broad storage access.
- **Responsiveness:** all rendering and database work runs off the main thread.
- **Resource safety:** renderer sessions and file descriptors are closed deterministically.
- **Consistency:** immutable UI state, explicit loading/error states, and stable lazy-list keys.
- **Accessibility:** Material 3 touch targets, contrast-aware editorial palette, edge-to-edge insets.

### Explicitly out of scope

Vatca does not implement, and should not grow into: EPUB/MOBI/AZW/AZW3/FB2/TXT/HTML/DOC/DOCX/CBZ/CBR support, accounts or profiles, cloud sync, third-party storage integrations, ads, subscriptions, in-app purchases, paywalls, online bookstores, social reading, recommendations, AI assistants or summaries, text-to-speech, dictionaries or translation, highlights or annotation management, handwriting, gamification, reading-statistics dashboards, DRM management, unrelated notifications, analytics SDKs, or onboarding flows.

Do not add features merely because another reader has them.

---

## Getting started

### Prerequisites

| Tool | Requirement |
|---|---|
| Android Studio | Latest stable release recommended |
| JDK | 17 or newer; the Gradle wrapper declares a daemon toolchain and can provision it through the foojay resolver |
| Android SDK Platform | Platform 36 with minor API level 1 (`compileSdk 36.1`) |
| Android SDK tools | Build Tools and Platform Tools |
| Device or emulator | Android 7.0 (API 24) or newer |
| Git | Any recent version |

The first build downloads Gradle 9.3.1, the daemon JDK, and dependencies, so an internet connection is required for setup even though the app itself is offline.

### 1. Clone the repository

```bash
git clone <repository-url>
cd vatca
```

### 2. Point Gradle at your Android SDK

Create `local.properties` in the project root (Android Studio usually does this automatically):

```properties
sdk.dir=/absolute/path/to/Android/Sdk
```

On Windows, escape the path:

```properties
sdk.dir=C\:\\Users\\<you>\\AppData\\Local\\Android\\Sdk
```

`local.properties` is gitignored and must not be committed.

### 3. Generate a debug keystore

`debug.keystore` is intentionally gitignored, but `app/build.gradle.kts` references `${rootDir}/debug.keystore` for debug builds. A fresh clone therefore needs one generated locally:

```bash
keytool -genkeypair -v \
  -keystore debug.keystore \
  -storepass android \
  -alias androiddebugkey \
  -keypass android \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -dname "CN=Android Debug,O=Android,C=US"
```

Run this from the repository root. If you already have a debug keystore you want to reuse, place it at `debug.keystore` in the project root. Do not commit it.

### 4. Build and install

```bash
# Build a debug APK
./gradlew assembleDebug

# Install it on a connected device or running emulator
./gradlew installDebug
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

The debug APK is produced at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

You can also open the project in Android Studio and use **Run > Run 'app'**.

### 5. Run the tests

```bash
# JVM unit tests and Robolectric tests
./gradlew test

# Instrumented tests; requires a connected device or emulator
./gradlew connectedDebugAndroidTest
```

See [Testing](#testing) for what is currently covered.

### 6. Build a release APK

Release signing reads three environment variables and expects the key alias `upload`:

```bash
export KEYSTORE_PATH="/absolute/path/to/upload-keystore.jks"
export STORE_PASSWORD="your-store-password"
export KEY_PASSWORD="your-key-password"

./gradlew assembleRelease
```

If `KEYSTORE_PATH` is not set, the build falls back to `${rootDir}/my-upload-key.jks`. The release APK is written to:

```text
app/build/outputs/apk/release/app-release.apk
```

Release builds currently run with R8/minification disabled. Keep your signing files and passwords out of version control.

---

## Configuration

Most configuration lives in two files:

- [`app/build.gradle.kts`](app/build.gradle.kts) — app ID, namespace, SDK levels, signing, build features.
- [`gradle/libs.versions.toml`](gradle/libs.versions.toml) — dependency and plugin versions.

### Application identity

The project currently uses Android Studio/AI Studio defaults:

```kotlin
namespace = "com.example"
applicationId = "com.aistudio.vatca.rkymqx"
```

Change both before publishing to a store or distributing your own build. Renaming the namespace also means moving the Kotlin package directories and `package` declarations under `app/src/main/java/`.

### API keys and service files

Vatca does **not** use Gemini, Firebase, or any network service at runtime. No API key is required.

- `.env.example` exists because the build applies the Secrets Gradle Plugin. The plugin falls back to `.env.example` when `.env` is absent. You do not need to create `.env` for Vatca's core features.
- The Google Services plugin is configured with a warning-only strategy when `google-services.json` is missing, so the project builds without it.

If you fork Vatca for a different product, adjust or remove those plugins rather than adding secrets the app does not use.

---

## Testing

Current automated coverage is intentionally small but real:

- **Robolectric/JVM tests** verify the app name resource, reading-progress calculation, Room insert/query/update/delete behavior, and library sorting logic.
- **Instrumented test** verifies the application ID on a device.
- A placeholder arithmetic unit test exists from the project template.

```bash
./gradlew test
```

Useful follow-up areas for contributors: repository import failures, SAF permission revocation, ViewModel progress debouncing, thumbnail path cleanup, and Compose UI journeys from import to restored reading position.

---

## Known limitations

- **Fixed-layout PDFs.** Vatca does not reflow text, change document font sizes, or recolor PDF page content. The reader backdrop affects the viewer, not the document.
- **Global reader settings.** Reading mode, backdrop, spacing, margin, fit, transition, orientation, and brightness are global defaults, not per-document overrides.
- **Page-flip animation.** The "Book Page Flip" option is a 3D rotation/fade approximation, not a physical page-curl simulation.
- **Title-only search.** Search filters library titles; it does not search inside PDF text.
- **No annotations.** There are no highlights, notes, drawings, or bookmarks yet.
- **Database migrations.** Room is at schema version 1 with `fallbackToDestructiveMigration()`. A future schema change will reset library metadata. Original PDF files are never touched.
- **SAF access can be revoked.** If a provider moves a file or withdraws permission, the library entry remains but the reader shows an error. Reimport the document to recover access.
- **Rendering limits.** Bitmap width is clamped to 300–2048 px and the cache is capped at 48 MB. Very complex pages may render more slowly on low-end devices.
- **Release minification is disabled.** Enable R8 and verify the build before shipping a production APK.
- **AI Studio defaults remain.** The namespace and application ID are placeholders and should be changed before distribution.
- **Limited UI test coverage.** There is no full end-to-end Compose test suite yet.

---

## Contributing

Contributions are welcome, especially around reliability, rendering performance, accessibility, and tests.

1. Fork the repository and create a topic branch.
2. Keep changes inside the approved product scope. Open an issue before adding a feature that is not listed in the specification.
3. Follow the existing architecture:
   - Keep PDF access behind `PdfRepository` and `PdfRendererEngine`.
   - Keep business logic out of composables; use ViewModels and immutable UI state.
   - Do not block the main thread.
   - Close renderer sessions and release resources deterministically.
   - Use stable keys for lazy lists.
4. Add or update tests for behavior changes.
5. Run `./gradlew test` and `./gradlew assembleDebug` before opening a pull request.
6. Use clear commit messages. Conventional Commits are appreciated (`feat:`, `fix:`, `docs:`, `test:`, `refactor:`).

For bug reports, include the Android version, device model, PDF size/page count, and a description of the source (local file, Downloads provider, cloud provider) when possible. Do not attach copyrighted or private documents.

---

## Documentation

- [Design system: Serene Editorial Reader](public/assets/aistudio/DESIGN_09dc4c69.md) — color, typography, spacing, elevation, and component direction.
- [Product and implementation brief](public/assets/aistudio/vatca-android-coding-agent-prompt_4bf9e74c.md) — original scope, architecture expectations, and definition of done.

These documents describe the intent behind the product and are useful when reviewing changes for scope fit.

---

## Acknowledgements

Vatca stands on the Android and open-source ecosystem:

- [AndroidX and Jetpack Compose](https://developer.android.com/jetpack)
- [Room](https://developer.android.com/training/data-storage/room), [DataStore](https://developer.android.com/topic/libraries/architecture/datastore), and [Navigation Compose](https://developer.android.com/jetpack/androidx/releases/navigation)
- [Coil](https://coil-kt.github.io/coil/) for local image loading
- [Inter](https://rsms.me/inter/) typeface, bundled under the SIL Open Font License
- The Android platform `PdfRenderer` team

---

## License

Vatca is intended to be released as open source. **No license file is currently included in this repository.** Add a `LICENSE` file (for example, MIT or Apache-2.0) before publishing or accepting external contributions, and update this section with the chosen license.
