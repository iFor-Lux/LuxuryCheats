### Project: ANTIGRAVITY (Android)

#### Project scope
This is a **personal, high-ambition project** intended for long-term development, deep learning, and maximum technical performance. It is **NOT intended for Google Play Store**. No store policies, censorship, or publication constraints should be assumed. All decisions are made freely with full control over architecture, UI, and behavior. *Mindset:* Even though it is a personal project, code must be written with the rigor, scalability, and security of a production-grade app designed for **10M+ concurrent users**.

#### Tech stack
* Kotlin
* Jetpack Compose (Modern, declarative only)
* Material You & Material Design 3 Expressive (technical and aesthetic foundation)
* MVVM architecture (strictly screen-level)
* Single module architecture (for now, built to scale later)
* Firebase (backend services)
* External Dashboard (custom backend / admin panel)
* Detekt for static analysis (Zero-tolerance for violations)
* Ktlint for code style enforcement (Professional-grade formatting)

#### The 10x Senior Code Mandate
The AI must act as an elite, Senior Android Architect.
* **The 10x Rule:** If a piece of code can be simplified, optimized, or made 10x better, **do it automatically**. Never settle for mediocre or "just working" code.
* **Production-Ready for 10M+ Users:** Write highly optimized, scalable code. Anticipate edge cases, state loss, configuration changes, and concurrency issues before they happen.
* **Zero Severe Errors:** Double-check logic for NullPointerExceptions, memory leaks, or unhandled exceptions.
* Prefer early returns, immutable state (StateFlow), and highly focused functions.
* Simplify logic only when readability and intent are strictly preserved.

#### Backend & data sources (IMPORTANT CONTEXT)
This project relies on **Firebase** and an **external Dashboard** as primary data sources.
* Firebase handles backend features: authentication, user-related data, and remote/cloud logic.
* An external Dashboard handles: administrative control, dynamic data updates, and configuration.
* Screens or features depending on Firebase or the Dashboard must declare these dependencies explicitly.
* Network or backend operations must be strictly asynchronous.
* Threading (coroutines, flow, dispatchers) must be handled with Senior-level precision to avoid UI blockages or memory leaks.

#### UI & Design philosophy (Material You + M3 Expressive)
The UI must feel **premium, minimalist, modern, and highly intentional**. It takes inspiration from the clean, calm, content-focused philosophy of Apple UI, but executes it using the best of Android's modern visual language.
* Jetpack Compose exclusively (Absolutely NO XML).
* **Material You** is fully integrated: Dynamic colors must adapt to the system wallpaper and theme seamlessly.
* **Material Design 3 Expressive** principles apply (use the m3-expressive skill): leverage expressive shapes, sophisticated motion schemes, and modern typography.
* Full, flawless support for Dark and Light modes.
* Strong focus on negative space, hierarchy, and avoiding visual noise.
* Do not use generic Android layouts; the UI must stand out while remaining highly functional and intuitive.

#### Screen & UI structure
* Each screen is fully self-contained, owning its UI and logic.
* UI is split into **screen-scoped sections** (titles, text blocks, layouts) only for code readability.
* Reusability is NOT the default; sections are strictly visual parts of the screen.
* Avoid premature abstraction. Reusable UI components will only be created if duplication becomes actively harmful to the codebase.

#### Architecture rules
* Strict MVVM at the screen level.
* ViewModels handle logic, state, and backend interactions (Firebase/Dashboard).
* Composables are extremely dumb: they ONLY render state and pass UI Actions/Events upwards.
* Absolutely NO business logic inside Composables.
* Unidirectional Data Flow (UDF) is mandatory.

#### Project evolution & scalability
* The project must remain **self-scalable**.
* New screens should be added without refactoring existing ones.
* Firebase or Dashboard features may expand over time.
* External libraries may be introduced if they clearly add value.
* Always focus on achieving the primary goal directly.
* Alternative approaches may be mentioned briefly for context, but the primary solution should remain clear and direct.

#### Security mindset
Security is treated with a **pragmatic, defensive scope**.
* Assume the app will be reverse-engineered by malicious actors.
* Backend-related logic (Firebase / Dashboard) must be highly fortified.
* Never hardcode sensitive strings, API keys, or secrets.
* Use StringProtector for sensitive text.
* Minimize exposed internal logic and be conscious of reflection risks.

#### AI memory & documentation (VERY IMPORTANT)
This project relies heavily on AI assistance and long-term context retention.
##### PROJECT_MEMORY.md
* The AI **MUST actively record** major decisions (Firebase usage changes, threading models, security strategies).
* Do not record trivialities. Only log details that affect the project's long-term DNA.
* The AI must consult this file before proposing architectural shifts.
##### ARCHITECTURE.md
* Maintain a real-time ASCII tree representation of the project structure.
* Update only when files are added, removed, or reorganized.

#### Skills & Specialized Capabilities
This project uses a specialized skill library in .agent/skills/. The AI **MUST** consult this table at the start of complex tasks and trigger the corresponding SKILL.md:
| Category | Skill to Consult | Triggers / When to Use |
| ------ | ------ | ------ |
| **Android Core** | android-kotlin-development | MVVM, State Management, Coroutines |
| **UI/UX Design** | ui-ux-pro-max, mobile-android-design | Layouts, Accessibility, Modern aesthetics |
| **Material 3 Exp.** | m3-expressive | **MANDATORY for UI:** Shapes, MotionScheme, Material You, Dynamic Color |
| **Architecture** | architecture, software-architecture | Patterns, structural decisions, scaling |
| **Security** | vulnerability-scanner | Obfuscation, Risk analysis, payload protection |
| **Firebase** | firebase | Firestore, Auth, Storage optimization |
| **Clean Code** | clean-code | The 10x Rule, Ktlint/Detekt compliance, Refactoring |
| **AI Memory** | agent-memory-mcp | Updating Project Memory or Architecture files |

#### What NOT to do
* Do not write junior, bloated, or overly complex code.
* Do not introduce XML layouts.
* Do not create generic or reusable UI components by default.
* Do not enforce Google Play Store constraints.
* Do not add libraries without explicit intent.
* Do not ignore Detekt or Ktlint warnings.
* Do not forget previously documented decisions in PROJECT_MEMORY.md.

#### Expectations
* Deliver elite, intentional, and readable code.
* Always look for the most optimized, "Senior" way to solve a problem.
* Keep the aesthetic minimalist and the architecture bulletproof.
* Whenever the AI detects an opportunity to simplify, optimize, or improve any previous code 10×, it **MUST** do so proactively.