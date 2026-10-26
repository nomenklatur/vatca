# AI Coding Agent Instructions: Build Vatca

You are a senior Android engineer, Kotlin specialist, and product-minded UI/UX engineer. Your task is to design, implement, test, and deliver **Vatca**, a native Android PDF reader.

You are working directly inside a code repository. Your responsibility is to produce a working application, not just a PRD, architecture proposal, code snippets, or static UI mockups.

Work autonomously, inspect the existing repository, make sensible engineering decisions, implement the application incrementally, run available checks, and fix issues you encounter.

Do not stop after creating a plan. Once the plan is established, proceed with implementation.

---

## 1. Product Overview

**App name:** Vatca

**Platform:** Android

**Programming language:** Kotlin

**UI framework:** Jetpack Compose

**Product category:** PDF reader

Vatca is a minimal, clean, completely free, offline-first Android PDF reader inspired by the reading experience of Moon+ Reader.

Vatca is NOT a complete Moon+ Reader clone.

The application focuses exclusively on three capabilities:

1. Reading PDF documents.
2. Customizing the reading experience.
3. Managing a simple visual library of PDF documents, with thumbnails generated from the PDFs themselves.

The application must be usable immediately without creating an account, signing in, connecting to a service, or paying for anything.

### Product principles

- Minimalism over feature quantity.
- Reading experience over interface complexity.
- Local-first and privacy-first.
- Native Android implementation.
- Reliable PDF rendering.
- Fast access to recently read documents.
- Consistent, predictable behavior.
- No unnecessary dependencies or infrastructure.

---

## 2. Strict Feature Scope

Implement only the capabilities explicitly described in this document.

### Feature A: PDF Library

The home screen displays the user's PDF library.

Required capabilities:

- Import PDFs using the native Android document picker.
- Support Android's Storage Access Framework.
- Display imported PDFs in a visually appealing library.
- Generate a thumbnail from the first page of each PDF.
- Display the document title.
- Display page count when available.
- Display reading progress when available.
- Display recently read information where useful.
- Open a PDF from the library.
- Search documents by title or filename.
- Sort documents using simple options, such as title and recently opened.
- Remove a document from the Vatca library.
- Provide a clear empty state with an import action.
- Provide a useful loading state while importing or generating thumbnails.
- Handle inaccessible, moved, or deleted source files gracefully.

Use a clean grid or list layout. Choose the layout that best supports the minimal visual design.

Removing a document from the library must not delete the original file from the user's device without explicit confirmation and a clear explanation. Prefer a library-only removal operation.

Do not recursively scan the user's entire storage.

#### PDF import behavior

Use the Android Storage Access Framework, preferably `ActivityResultContracts.OpenDocument`, with the PDF MIME type.

Persist URI access permissions when supported and necessary.

The library should retain access to previously imported documents across application restarts.

Handle permission failures and unavailable URIs gracefully.

Avoid copying entire PDF files into application storage unless required for a specific technical reason.

### Feature B: PDF Reader

Implement a real PDF reader. Do not substitute a screenshot, placeholder page, web page, or static mockup for PDF rendering.

Required capabilities:

- Render PDF pages accurately.
- Display the current page and total page count.
- Navigate between pages.
- Support continuous vertical scrolling.
- Support page-by-page navigation where supported.
- Support zooming.
- Support fit-to-width and fit-to-page behavior.
- Support configurable page spacing.
- Support configurable reading margins where applicable.
- Support portrait and landscape orientations.
- Support optional full-screen reading.
- Allow reader controls to be shown or hidden.
- Save the last reading position.
- Restore the last reading position when reopening a document.
- Handle large PDFs without loading every rendered page into memory.
- Display a loading indicator when opening a document.
- Display a useful error state when a document cannot be rendered.

Use an appropriate Android-compatible PDF rendering solution.

Evaluate Android's native `PdfRenderer` and, if necessary, a maintained PDF rendering library compatible with Kotlin and Jetpack Compose.

Choose the simplest reliable solution that meets the requirements.

Document the decision in the repository's technical documentation.

Do not build a custom PDF rendering engine.

Do not implement EPUB rendering or reflow PDF content into editable text.

#### PDF interaction requirements

- Scrolling must feel natural on Android.
- Zoom gestures must behave predictably.
- The reader must not reload the entire PDF unnecessarily when settings change.
- Page rendering must be lazy or demand-driven.
- Rendered pages should be cached with appropriate memory limits.
- Resources such as file descriptors, bitmaps, and rendering sessions must be released appropriately.
- Switching between documents must not leak rendering resources.
- Background operations must not block the main thread.

### Feature C: Customizable Reading Experience

Implement customization appropriate for PDF documents.

Provide settings for:

**Appearance**
- Light theme.
- Dark application theme.
- Warm/sepia reading interface where technically appropriate.
- Reader background color.
- System theme following.

**Reading layout**
- Continuous vertical scrolling.
- Page-by-page navigation where supported.
- Fit-to-width.
- Fit-to-page.
- Page spacing.
- Reading margins.
- Zoom controls.

**Display**
- Show or hide reader controls.
- Full-screen reading mode.
- Screen orientation preference.
- Optional in-app brightness adjustment.

**Library**
- Grid or list presentation.
- Sorting preference.

Persist user preferences locally.

Keep settings easy to discover and understand.

Important technical constraints:

- Do not promise PDF text font-size changes that modify the actual document layout. PDFs have fixed page layouts; text resizing is generally not equivalent to font resizing in EPUB readers.
- Do not claim that changing the reader background recolors the PDF itself. Distinguish between the page background and the rendered document.
- Do not implement automatic dark-mode PDF recoloring unless explicitly approved as a future requirement.
- Device brightness control must be scoped appropriately and must not permanently change the user's system brightness.
- Do not override system orientation globally.
- Clearly indicate when a setting is unavailable for a particular PDF or reading mode.

### Feature D: Local Persistence

Vatca must work offline.

Use local persistence for:

- Library entries.
- Source document URIs.
- Document metadata.
- Thumbnail cache references, if persisted.
- Last reading position.
- Reading progress.
- Per-document reading preferences where appropriate.
- Global reader preferences.
- Library sorting and layout preferences.

Use Room for structured persistent data and DataStore for application preferences when appropriate.

Choose a sensible schema and migrations strategy.

Reading progress must be calculated from the document's actual page position or scroll position, rather than an arbitrary counter.

Persist progress at appropriate intervals and when leaving the reader. Avoid writing to the database on every scroll frame.

Handle database errors and stale file references gracefully.

---

## 3. Explicitly Out of Scope

Do not implement any of the following:

- EPUB, MOBI, AZW, AZW3, FB2, TXT, HTML, DOC, DOCX, CBZ, or CBR support.
- User registration or sign-in.
- User profiles.
- Backend services.
- Cloud synchronization.
- Google Drive, Dropbox, or WebDAV integration.
- Advertisements.
- Subscriptions or premium tiers.
- In-app purchases.
- Paywalls.
- Online bookstores.
- Social reading.
- Book recommendations.
- AI assistants or AI-generated summaries.
- Text-to-speech or audiobooks.
- Dictionary or translation integration.
- Highlights or annotation management.
- Handwriting.
- Reading challenges or gamification.
- Reading statistics dashboards.
- DRM management.
- Notifications unrelated to core functionality.
- Analytics or advertising SDKs.
- Unnecessary onboarding screens.

Do not add features merely because they exist in Moon+ Reader.

If a requirement is not explicitly listed in this prompt, consider it out of scope unless it is essential for implementing one of the approved capabilities.

Do not expand the product scope without asking for approval.

---

## 4. Technology Stack

Build a native Android application using the following stack.

| Area | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Design system | Material 3, customized for Vatca |
| Architecture | MVVM with clear separation of responsibilities |
| Dependency injection | Hilt, if justified by project complexity |
| Local database | Room |
| Preferences | DataStore |
| Asynchronous work | Kotlin Coroutines and Flow |
| PDF rendering | Android PdfRenderer or a suitable maintained Android PDF library |
| Document selection | Android Storage Access Framework |
| Navigation | Navigation Compose |
| Testing | JUnit, Kotlin coroutine testing, Compose UI testing where appropriate |
| Build system | Gradle with Kotlin DSL |
| Dependency management | Version catalogs using `libs.versions.toml` |

Use stable, mutually compatible dependency versions available in the development environment.

Do not blindly upgrade dependencies to experimental versions.

Check the existing project's Gradle configuration and installed Android SDK before making changes.

Prefer stable Android APIs and modern platform practices.

Use a sensible minimum SDK based on PDF rendering, Compose, and supported Android devices. Document the choice.

Use the latest stable compile SDK available in the environment, if compatible.

Avoid unnecessary libraries.

Do not use a WebView to implement the reader.

Do not build the application as a React Native, Flutter, Ionic, or web application.

---

## 5. Architecture and Code Quality

Organize the codebase by feature and responsibility.

A suggested structure is:

```text
app/
  src/main/java/<package>/
    core/
      database/
      datastore/
      pdf/
      ui/
      util/
    data/
      local/
      repository/
    domain/
      model/
      repository/
      usecase/
    feature/
      library/
        ui/
        viewmodel/
      reader/
        ui/
        viewmodel/
      settings/
        ui/
        viewmodel/
    navigation/
    VatcaApplication.kt
    MainActivity.kt
```

Adapt the structure to the repository where appropriate.

Do not introduce unnecessary abstraction layers simply to match this example.

### Engineering requirements

- Use Kotlin idioms and null safety.
- Keep composable functions focused and reusable.
- Keep business logic out of composables.
- Expose UI state through immutable state objects.
- Use ViewModels for screen-level state and actions.
- Use repositories to isolate persistence and document access.
- Use coroutines for I/O and rendering operations.
- Handle exceptions deliberately.
- Avoid global mutable state.
- Avoid giant composables and giant ViewModels.
- Avoid duplicated business logic.
- Avoid blocking the main thread.
- Avoid force-unwrapping nullable values.
- Avoid deprecated APIs when suitable alternatives exist.
- Use stable keys for lazy lists.
- Provide meaningful names and concise comments.
- Keep dependencies and abstractions proportionate to the application's size.

Use unidirectional data flow where appropriate.

Represent loading, success, empty, and error states explicitly.

Do not create a backend because Vatca is a local-first application.

---

## 6. UI/UX Design Direction

Vatca should feel like a carefully designed, modern reading application rather than a technical PDF utility.

The visual language should be minimal, calm, clean, and content-focused.

### Library screen

Design requirements:

- A restrained top app bar with the Vatca name or a simple wordmark.
- A clear library title.
- A search action.
- A simple layout toggle or sorting action where useful.
- A prominent but unobtrusive import PDF action.
- PDF cards with actual PDF-derived thumbnails.
- Readable document titles.
- Subtle reading-progress indicators.
- Minimal metadata.
- Generous spacing.
- Subtle dividers and borders.
- A balanced grid or list.
- A polished empty state.
- Useful loading and error states.

Do not fill the screen with unnecessary toolbar actions.

Do not use fabricated book covers.

PDF thumbnails must be generated from the actual source documents.

### Reader screen

The PDF content is the primary visual element.

Design requirements:

- Minimal reader chrome.
- Controls that can be hidden while reading.
- A discreet page indicator.
- Clear page navigation controls where needed.
- Simple zoom controls.
- Easy access to reading settings.
- A clean settings panel or bottom sheet.
- Smooth transitions without excessive animation.
- Appropriate light and dark appearance.
- Predictable back navigation.

Do not leave permanent floating buttons covering the document.

### Settings screen

Keep settings organized into a small number of understandable sections.

Avoid presenting every setting as an equally prominent control.

Use appropriate native controls such as switches, sliders, segmented buttons, and selection dialogs.

Settings must work, not just look interactive.

### Visual design principles

- Follow Material 3 accessibility and interaction guidelines.
- Customize Material 3 rather than accepting its default appearance everywhere.
- Use a consistent spacing scale.
- Establish a clear typography hierarchy.
- Use restrained accent colors.
- Ensure sufficient contrast.
- Support dynamic text sizing where appropriate.
- Respect system insets and edge-to-edge layout requirements.
- Provide meaningful touch targets.
- Avoid decorative gradients, excessive shadows, and excessive animation.
- Support light and dark application themes.

Use reasonable design judgment to establish Vatca's colors, typography, iconography, and component styles.

Do not pause for approval of minor design decisions.

---

## 7. Required Screens

Implement the following screens and states.

### A. Library

Includes:

- Empty library.
- Populated library.
- Searching.
- Sorting.
- Importing.
- Thumbnail loading.
- Missing or inaccessible documents.
- Library item removal.

### B. PDF Reader

Includes:

- Initial loading.
- Successful document rendering.
- Reading and scrolling.
- Zooming.
- Page navigation.
- Reader controls visible.
- Reader controls hidden.
- Settings adjustment.
- Full-screen mode.
- Document restoration at the saved position.
- Rendering errors.

### C. Settings

Includes:

- Theme selection.
- Reading layout settings.
- Page spacing and margin controls.
- Brightness settings where supported.
- Screen orientation settings.
- Library layout and sorting preferences.

### D. Minimal Dialogs and Sheets

Provide only those needed for core workflows, including:

- Importing a PDF through the Android picker.
- Confirming library removal when appropriate.
- Adjusting reader settings.
- Explaining an unrecoverable file-access issue.

Do not add unnecessary onboarding or confirmation dialogs.

---

## 8. Data Model

Design a minimal persistent data model.

A suggested library entity:

```kotlin
@Entity(tableName = "pdf_documents")
data class PdfDocumentEntity(
    @PrimaryKey
    val id: String,
    val uri: String,
    val title: String,
    val pageCount: Int?,
    val lastReadPage: Int,
    val lastReadOffset: Float,
    val lastOpenedAt: Long?,
    val dateAdded: Long,
    val thumbnailPath: String?
)
```

This is a starting point, not a mandatory schema.

Review the model and adapt it to the chosen PDF rendering implementation.

Consider:

- Stable document IDs.
- Persisted document URI permissions.
- The distinction between a URI and a filesystem path.
- Reading progress normalization.
- Per-document versus global settings.
- Thumbnail cache invalidation.
- Missing files.
- Database migrations.

Do not store the entire PDF or large bitmap objects in Room.

Do not assume every document has a usable filesystem path.

If the application needs to reopen a document using a content URI, it must use Android's supported URI access mechanisms.

---

## 9. PDF Rendering and Resource Management

Treat PDF rendering as a core engineering responsibility.

Implement a dedicated PDF rendering abstraction rather than tightly coupling PDF access to UI components.

The rendering layer should provide operations equivalent to:

- Open a document from an accessible URI.
- Read page count.
- Read document metadata where available.
- Render a requested page at an appropriate resolution.
- Generate a first-page thumbnail.
- Close document resources.
- Report recoverable rendering errors.

Design the abstraction around the actual API provided by the chosen PDF engine.

### Performance rules

- Do not render every page during initial loading.
- Render visible pages and nearby pages as needed.
- Use appropriately sized bitmaps.
- Avoid retaining full-resolution bitmaps for every page.
- Cache thumbnails and rendered pages with bounded memory usage.
- Reuse cached thumbnails where valid.
- Perform rendering off the main thread.
- Avoid unnecessary recomposition.
- Cancel obsolete rendering work when appropriate.
- Close native resources reliably.
- Handle rapid scrolling and repeated document opening.
- Handle low-memory conditions gracefully.

For large PDFs, prioritize stable scrolling and memory safety over expensive rendering effects.

Test with small, large, text-heavy, and image-heavy PDFs where available.

---

## 10. Reading Progress and Preferences

Persist reading progress per document.

At minimum, save:

- Current page.
- Scroll position or offset when supported.
- Last opened timestamp.
- Relevant per-document settings when appropriate.

Persist global preferences separately.

Examples include:

- Application theme.
- Preferred reader layout.
- Page spacing.
- Default zoom behavior.
- Library presentation.
- Library sorting.

Do not write to the database on every frame.

Debounce or otherwise limit progress persistence, and save at meaningful lifecycle points.

Reopening a PDF must restore the correct page and, where supported, its scroll position.

If the original URI becomes inaccessible, preserve the library entry until the user removes it or chooses to reimport the document. Present a clear recovery action.

---

## 11. Privacy and Offline Operation

Vatca must work without a network connection.

Requirements:

- No account creation.
- No sign-in.
- No backend.
- No cloud sync.
- No advertisements.
- No analytics SDK.
- No advertising identifiers.
- No unnecessary tracking.
- No automatic PDF uploads.
- No unnecessary collection of personal information.

Do not request broad storage permissions when the Storage Access Framework can satisfy the use case.

Do not add the Android `INTERNET` permission unless a documented technical requirement genuinely needs it. The core product must not depend on network access.

Review the final manifest and dependency tree for unnecessary permissions or SDKs.

All reading and library functionality must remain available without payment or internet access.

---

## 12. Testing Requirements

Write meaningful tests rather than merely checking that the project compiles.

### Unit tests

Cover important behavior such as:

- Library repository operations.
- Reading-progress persistence.
- Preference persistence.
- Document sorting and filtering.
- Missing document handling.
- URI-access failures.
- Page-count and progress calculations.
- Thumbnail cache behavior where testable.

### PDF-related tests

Verify, as far as the chosen PDF engine and test environment permit:

- Opening a valid PDF.
- Reading page count.
- Rendering a page.
- Rendering a thumbnail.
- Closing document resources.
- Handling invalid or inaccessible documents.
- Reopening a document at its saved position.

### UI tests

Cover the main user journey where practical:

1. Launch the app.
2. See the empty library.
3. Import a PDF.
4. See the PDF thumbnail.
5. Open the PDF.
6. Navigate or scroll.
7. Change a reader setting.
8. Return to the library.
9. Reopen the PDF.
10. Verify that reading progress is restored.

Do not claim a test passed unless it was actually executed and its result verified.

---

## 13. Implementation Workflow

Follow this workflow.

### Phase 1: Repository inspection

Before editing:

1. Inspect the repository structure.
2. Identify the existing application module and package name.
3. Inspect Gradle files and dependency versions.
4. Check Android SDK configuration.
5. Identify existing screens, architecture, and tests.
6. Determine whether the project is new or already contains application code.

If a working project exists, preserve useful code and conventions.

Do not overwrite unrelated files or replace an existing project unnecessarily.

### Phase 2: Implementation plan

Create a short implementation plan organized around:

1. Project setup and design system.
2. PDF access and rendering.
3. Local persistence.
4. Library screen.
5. Reader screen.
6. Reading customization.
7. Error handling.
8. Testing and verification.

Identify important technical risks, especially PDF rendering, URI persistence, and memory management.

Once the plan is established, continue implementing without waiting for approval unless a genuine blocker or consequential ambiguity requires user input.

### Phase 3: Foundation

Implement:

- Gradle configuration.
- App theme.
- Navigation.
- Core models.
- Room database.
- DataStore preferences.
- Repository interfaces and implementations.
- PDF rendering abstraction.

Keep the application buildable as implementation progresses.

### Phase 4: Library

Implement the complete import-to-library flow.

Ensure thumbnails come from actual PDFs and library entries survive application restarts.

### Phase 5: Reader

Implement actual PDF rendering, navigation, scrolling, zooming, and resource management.

Prioritize correctness and responsiveness.

### Phase 6: Customization

Implement and persist supported reading settings.

Ensure changing settings has a real effect on the reader.

### Phase 7: Quality

Implement loading, empty, and error states.

Run tests, compile the application, and fix issues.

Review the application for unnecessary features, dependencies, permissions, and network access.

### Phase 8: Final verification

Execute the relevant available commands, such as:

```bash
./gradlew test
./gradlew assembleDebug
```

Run Android instrumented tests or connected-device tests if an emulator or device is available.

If a command cannot run because the environment lacks the Android SDK, emulator, or another dependency, report that limitation honestly.

Do not claim the app runs on a device if it has only been compiled.

---

## 14. Definition of Done

The implementation is complete only when the following conditions are satisfied.

### Build

- [ ] The Android application compiles successfully.
- [ ] The package configuration is valid.
- [ ] No known blocking build errors remain.

### Library

- [ ] The app opens directly to the library.
- [ ] A user can import a PDF using the Android file picker.
- [ ] The library displays the actual first-page thumbnail.
- [ ] Document title and available metadata are displayed.
- [ ] Search and sorting work.
- [ ] Removing a library entry does not unexpectedly delete the original PDF.
- [ ] Library entries persist across restarts.

### Reader

- [ ] A real PDF is rendered.
- [ ] Page navigation works.
- [ ] Scrolling and zooming work.
- [ ] Page display settings work.
- [ ] Reader controls can be hidden.
- [ ] Reading settings are applied correctly.
- [ ] The reader saves and restores progress.
- [ ] Large PDFs are handled without unnecessarily loading all pages into memory.
- [ ] Rendering resources are released reliably.

### Customization

- [ ] Light and dark themes work.
- [ ] Other supported appearance options work.
- [ ] Page spacing and margins have real effects.
- [ ] Zoom and fitting modes work.
- [ ] Settings persist across restarts.
- [ ] Unsupported options are not presented as functional controls.

### Privacy

- [ ] No account is required.
- [ ] No paywall or subscription exists.
- [ ] No ads are present.
- [ ] The application works offline.
- [ ] No unnecessary network or storage permissions remain.

### Quality

- [ ] Meaningful tests have been run.
- [ ] Important error states are handled.
- [ ] No placeholder PDF reader remains.
- [ ] No dead buttons or fake interactions remain.
- [ ] The UI is polished and consistent.
- [ ] No out-of-scope features have been introduced.

---

## 15. Final Deliverables

When finished, provide:

1. The implemented Android source code in the repository.
2. A working debug APK if the environment supports building it.
3. A concise architecture summary.
4. The selected PDF rendering engine and rationale.
5. A summary of implemented functionality.
6. A list of tests and build commands actually executed, including results.
7. Any known limitations or unverified behavior.
8. Instructions for opening the project in Android Studio and running the application.

If you can build an APK, provide its actual output path. Never invent an artifact path.

Do not return only a proposed architecture, code examples, or a list of future steps. Implement the application.

## Final Instruction

Build **Vatca**, a native Kotlin and Jetpack Compose Android PDF reader that is minimal, polished, highly customizable, private, offline-first, and completely free.

Keep the product scope narrow.

Prioritize the quality of the PDF reading experience, the visual library, and reliable local persistence.

**Do not add features outside the approved scope. Do not stop at planning. Inspect, implement, test, build, and report the actual results.**
