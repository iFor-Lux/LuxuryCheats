# Project: Luxury Cheats (Android)

## Project scope
This is a **personal project** intended for long-term development and learning.
It is **NOT intended for Google Play Store**.
No store policies or publication constraints should be assumed.
All decisions are made freely with full control over architecture, UI, and behavior.

## Tech stack
- Kotlin
- Jetpack Compose
- Material Design 3 Expressive (technical foundation, not a visual limitation)
- MVVM architecture (screen-level only)
- Single module architecture (for now)
- Firebase (backend services)
- External Dashboard (custom backend / admin panel)
- Detekt for static analysis
- Ktlint for code style enforcement

## Backend & data sources (IMPORTANT CONTEXT)
This project relies on **Firebase** and an **external Dashboard** as primary data sources.

- Firebase is used for backend-related features such as:
  - authentication
  - user-related data
  - remote or cloud-based logic (when applicable)
- An external Dashboard is used for:
  - administrative control
  - dynamic data updates
  - configuration or management outside the app
- Screens or features that depend on Firebase or the Dashboard must be explicit
  about these dependencies
- Network or backend operations should be treated as asynchronous
- Threading (coroutines, dispatchers) must be respected and clearly handled

This context must be assumed by default unless explicitly stated otherwise.

## UI & Design philosophy
The UI should feel **premium, minimalist, and intentional**, inspired by the
**design philosophy of Apple UI** (clean, calm, content-focused),
**not by direct iOS patterns or visual imitation**.

- Jetpack Compose only (no XML)
- Material Design 3 is used as a base, not as a constraint
- Full support for **both Dark and Light mode**
- UI must adapt correctly to system theme changes
- Adaptive / dynamic colors are encouraged
- Strong focus on spacing, hierarchy, and typography
- Prefer fewer elements with clear meaning
- Avoid visual noise and generic Android layouts
- The UI should stand out while remaining clear and functional
- Open to custom layouts, transitions, and experimental UI ideas

## Screen & UI structure
- Each screen is fully self-contained
- Each screen owns its UI and logic
- UI is split into **screen-scoped sections** only for clarity
- UI sections are NOT reusable across screens by default
- Avoid shared or generic UI components
- Sections represent visual parts of the screen:
  - titles
  - text blocks
  - images
  - buttons
  - layout blocks
- Sections exist for readability and control, not abstraction

> Reusable UI components may be considered **only if duplication becomes harmful**
> or if explicitly requested.

## Architecture rules
- MVVM applied strictly at screen level
- Each screen contains:
  - ViewModel
  - UI State
  - UI Actions / Events
- ViewModels handle logic and state only
- Backend interactions (Firebase / Dashboard) belong in ViewModels or
  dedicated data layers, not in UI
- Composables render state only
- No business logic inside composables
- Prefer unidirectional data flow
- State handled using StateFlow

## Code quality & best practices
Code should prioritize **clarity, simplicity, and intent**.

- Prefer simple and direct solutions
- Avoid deeply nested `if / else` chains when logic can be simplified
- Prefer `when`, early returns, and small focused functions
- Simplify logic **only when readability and intent are preserved**
- Avoid clever or overly abstract code
- Favor readability over micro-optimizations
- Detekt and Ktlint rules must be respected at all times
- Follow Kotlin and Jetpack Compose best practices consistently

## Project evolution & scalability
- The project must remain **self-scalable**
- New screens should be added without refactoring existing ones
- Firebase or Dashboard features may expand over time
- External libraries may be introduced if they clearly add value
- Avoid premature abstractions or forced generalization
- Always focus on achieving the primary goal directly
- Alternative approaches may be mentioned briefly for context,
  but the primary solution should remain clear and direct

## Security mindset
Security is a priority, with a **pragmatic scope**.

- Assume the app will be reverse engineered
- Pay special attention to backend-related logic (Firebase / Dashboard)
- Avoid hardcoded sensitive strings or keys
- Use StringProtector for protected or sensitive text
- Minimize exposed internal logic
- Be mindful of attack surfaces and reflection risks
- This is **not banking-level security**, but defensive and conscious design

## AI memory & documentation (VERY IMPORTANT)
This project relies heavily on AI assistance and long-term context retention.

### PROJECT_MEMORY.md
- The AI **MUST actively record** important decisions and milestones
- Record when:
  - Firebase usage changes
  - Dashboard integrations evolve
  - Data flow or threading decisions are made
- Only record information that affects:
  - architecture
  - UI flow
  - security strategy
  - backend integration
  - long-term direction
- Avoid recording trivial steps or repetitive details
- The AI must consult this file before making architectural decisions
- Previously documented decisions must not be ignored or overridden

### ARCHITECTURE.md
- The AI **MUST maintain** an up-to-date view of the project structure
- Use ASCII tree symbols to represent folders and files
- The structure must reflect the real project layout
- Update this file **only when**:
  - folders are added or removed
  - files are added or removed
  - project structure is reorganized

## What NOT to do
- Do not introduce XML layouts
- Do not create generic or reusable UI components by default
- Do not enforce Google Play Store constraints
- Do not add libraries without explicit intent
- Do not refactor unrelated code
- Do not ignore or forget previously documented decisions

## Expectations
- Code should be explicit, readable, and intentional
- Maintain full control over UI, behavior, and architecture
- Prefer pragmatic solutions over theoretical ones
- Think defensively and long-term
- Act as a senior Android developer assisting a large, evolving project
