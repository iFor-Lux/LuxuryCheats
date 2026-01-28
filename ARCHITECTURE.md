.
└── LUXURY CHEATS
    ├── .gradle
    ├── .idea
    ├── .kotlin
    ├── PROJECT_MEMORY.md
    ├── ARCHITECTURE.md
    ├── app
    │   ├── build
    │   └── src
    │       ├── androidTest
    │       ├── main
    │       │   ├── java
    │       │   │   └── com.luxury.cheats
    │       │   │       ├── App.kt
    │       │   │       ├── MainActivity.kt
    │       │   │       ├── core
    │       │   │       │   ├── theme
    │       │   │       │   │   ├── Color.kt
    │       │   │       │   │   ├── Theme.kt
    │       │   │       │   │   ├── Type.kt
    │       │   │       │   │   └── ThemeManager.kt
    │       │   │       │   ├── ui
    │       │   │       │   │   ├── DotPatternBackground.kt
    │       │   │       │   │   ├── LogoWebViewConfig.kt
    │       │   │       │   │   ├── LogoWebViewManager.kt
    │       │   │       │   │   ├── PersistentLogo.kt
    │       │   │       │   │   └── WelcomeEclipseSection.kt
    │       │   │       │   ├── security
    │       │   │       │   │   ├── StringProtector.kt
    │       │   │       │   │   ├── SecurityChecks.kt
    │       │   │       │   │   └── WebViewHardening.kt
    │       │   │       │   ├── util
    │       │   │       │   │   ├── FileUtils.kt
    │       │   │       │   │   ├── PermissionUtils.kt
    │       │   │       │   │   └── Extensions.kt
    │       │   │       │   └── system
    │       │   │       │       ├── receivers
    │       │   │       │       │   ├── CleanerReceiver.kt
    │       │   │       │       │   └── DeviceAdminReceiver.kt
    │       │   │       │       ├── floatingpanel
    │       │   │       │       │   ├── FloatingPanelService.kt
    │       │   │       │       │   ├── FloatingPanelController.kt
    │       │   │       │       │   ├── FloatingPanelView.kt
    │       │   │       │       │   └── FloatingPanelLayout.kt
    │       │   │       │       ├── services
    │       │   │       │       │   └── LuxuryCleanerService.kt
    │       │   │       │       └── Shizuku
    │       │   │       │           ├── java
    │       │   │       │           │   ├── ShizukuManager.java
    │       │   │       │           │   ├── ShizukuMaster.java
    │       │   │       │           │   └── ShizukuShellCommand.java
    │       │   │       │           └── kotlin
    │       │   │       │               ├── ShizukuManager.kt
    │       │   │       │               ├── ShizukuMaster.kt
    │       │   │       │               └── ShizukuShellCommand.kt
    │       │   │       ├── services
    │       │   │       │   ├── AuthService.kt
    │       │   │       │   ├── FirebaseService.kt
    │       │   │       │   ├── FileService.kt
    │       │   │       │   ├── UrlService.kt
    │       │   │       │   ├── AnalyticsService.kt
    │       │   │       │   ├── FreeFireApiService.kt
    │       │   │       │   ├── FreeFirePlayerModels.kt
    │       │   │       │   └── UserPreferencesService.kt
    │       │   │       ├── navigations
    │       │   │       │   ├── AppNavHost.kt
    │       │   │       │   └── NavRoutes.kt
    │       │   │       ├── features
    │       │   │       │   ├── welcome
    │       │   │       │   │   ├── ui
    │       │   │       │   │   │   ├── WelcomeRoute.kt
    │       │   │       │   │   │   └── WelcomeScreen.kt
    │       │   │       │   │   ├── logic
    │       │   │       │   │   │   ├── WelcomeViewModel.kt
    │       │   │       │   │   │   ├── WelcomeState.kt
    │       │   │       │   │   │   └── WelcomeAction.kt
    │       │   │       │   │   ├── splash
    │       │   │       │   │   │   ├── ui
    │       │   │       │   │   │   │   ├── WelcomeSplashScreen.kt
    │       │   │       │   │   │   │   ├── WelcomeLogoSection.kt
    │       │   │       │   │   │   │   ├── WelcomeSpraysSection.kt
    │       │   │       │   │   │   │   ├── WelcomeSplashRoute.kt
    │       │   │       │   │   │   │   └── WelcomeLuxuryTextSection.kt
    │       │   │       │   │   │   └── logic
    │       │   │       │   │   │       ├── WelcomeSplashViewModel.kt
    │       │   │       │   │   │       ├── WelcomeSplashState.kt
    │       │   │       │   │   │       ├── WelcomeSplashAction.kt
    │       │   │       │   │   │       └── logo
    │       │   │       │   │   │           └── WelcomeLogoAnimation.kt
    │       │   │       │   │   │       └── sprays
    │       │   │       │   │   │           └── WelcomeSpraysAnimations.kt
    │       │   │       │   │   ├── page1
    │       │   │       │   │   │   └── bienvenida
    │       │   │       │   │   │       ├── ui
    │       │   │       │   │   │       │   ├── WelcomePage1Route.kt
    │       │   │       │   │   │       │   ├── WelcomePage1Screen.kt
    │       │   │       │   │   │       │   ├── WelcomePage1LogoSection.kt
    │       │   │       │   │   │       │   ├── WelcomePage1LuxuryText.kt
    │       │   │       │   │   │       │   ├── WelcomePage1LanguageSection.kt
    │       │   │       │   │   │       │   ├── WelcomePage1TextSection.kt
    │       │   │       │   │   │       │   ├── WelcomePage1ButtonSection.kt
    │       │   │       │   │   │       │   └── WelcomePage1BadgesSection.kt
    │       │   │       │   │   │       └── logic
    │       │   │       │   │   │           ├── WelcomePage1ViewModel.kt
    │       │   │       │   │   │           ├── WelcomePage1State.kt
    │       │   │       │   │   │           └── WelcomePage1Action.kt
    │       │   │       │   │   ├── page2
    │       │   │       │   │   │   └── permisos
    │       │   │       │   │   │       ├── ui
    │       │   │       │   │   │       │   └── WelcomePage2Screen.kt
    │       │   │       │   │   │       └── logic
    │       │   │       │   │   │           ├── WelcomePage2ViewModel.kt
    │       │   │       │   │   │           ├── WelcomePage2State.kt
    │       │   │       │   │   │           └── WelcomePage2Action.kt
    │       │   │       │   │   ├── page3
    │       │   │       │   │   │   └── shizuku
    │       │   │       │   │   │       ├── ui
    │       │   │       │   │   │       │   └── WelcomePage3Screen.kt
    │       │   │       │   │   │       └── logic
    │       │   │       │   │   │           ├── WelcomePage3ViewModel.kt
    │       │   │       │   │   │           ├── WelcomePage3State.kt
    │       │   │       │   │   │           └── WelcomePage3Action.kt
    │       │   │       │   │   └── page4
    │       │   │       │   │       ├── ui
    │       │   │       │   │       │   └── WelcomePage4Screen.kt
    │       │   │       │   │       └── logic
    │       │   │       │   │           ├── WelcomePage4ViewModel.kt
    │       │   │       │   │           ├── WelcomePage4State.kt
    │       │   │       │   │           └── WelcomePage4Action.kt
    │       │   │       │   ├── login
    │       │   │       │   │   ├── pantalla
    │       │   │       │   │   │   ├── ui
    │       │   │       │   │   │   │   ├── LoginPantallaScreen.kt
    │       │   │       │   │   │   │   ├── LoginPantallaUserPasswordSection.kt
    │       │   │       │   │   │   │   └── LoginPantallaPhantomCursor.kt
    │       │   │       │   │   │   └── logic
    │       │   │       │   │   │       ├── LoginPantallaViewModel.kt
    │       │   │       │   │   │       ├── LoginPantallaState.kt
    │       │   │       │   │   │       └── LoginPantallaAction.kt
    │       │   │       │   │   ├── teclado
    │       │   │       │   │   │       ├── ui
    │       │   │       │   │   │       │   ├── LoginTecladoSection.kt
    │       │   │       │   │   │       │   ├── AlphabeticTeclado.kt
    │       │   │       │   │   │       │   ├── NumericTeclado.kt
    │       │   │       │   │   │       │   └── TecladoKey.kt
    │       │   │       │   │   │       └── logic
    │       │   │       │   │   │           └── LoginTecladoState.kt
    │       │   │       │   ├── notifications
    │       │   │       │   │   ├── ui
    │       │   │       │   │   │   ├── NotificationsRoute.kt
    │       │   │       │   │   │   └── NotificationsScreen.kt
    │       │   │       │   │   ├── logic
    │       │   │       │   │   │   ├── NotificationsViewModel.kt
    │       │   │       │   │   │   ├── NotificationsState.kt
    │       │   │       │   │   │   └── NotificationsAction.kt
    │       │   │       │   │   └── service
    │       │   │       │   │       └── NotificationsService.kt
    │       │   │       │   ├── update
    │       │   │       │   │   ├── ui
    │       │   │       │   │   │   ├── UpdateRoute.kt
    │       │   │       │   │   │   ├── UpdateScreen.kt
    │       │   │       │   │   │   ├── UpdateSection.kt
    │       │   │       │   │   │   └── UpdateAnuncioSection.kt
    │       │   │       │   │   ├── logic
    │       │   │       │   │   │   ├── UpdateViewModel.kt
    │       │   │       │   │   │   ├── UpdateState.kt
    │       │   │       │   │   │   └── UpdateAction.kt
    │       │   │       │   │   └── service
    │       │   │       │   │       └── UpdateService.kt
    │       │   │       │   ├── home
    │       │   │       │   │   ├── ui
    │       │   │       │   │   │   ├── HomeRoute.kt
    │       │   │       │   │   │   └── HomeScreen.kt
    │       │   │       │   │   └── logic
    │       │   │       │   │       ├── HomeViewModel.kt
    │       │   │       │   │       └── HomeState.kt
    │       │   │       │   ├── perfil
    │       │   │       │   │   ├── ui
    │       │   │       │   │   │   ├── PerfilScreen.kt
    │       │   │       │   │   │   ├── PerfilInfoSection.kt
    │       │   │       │   │   │   ├── PerfilDetallesSection.kt
    │       │   │       │   │   │   ├── PerfilCreditosSection.kt
    │       │   │       │   │   │   └── PerfilComunidadSection.kt
    │       │   │       │   │   └── logic
    │       │   │       │   │       ├── PerfilViewModel.kt
    │       │   │       │   │       └── PerfilState.kt
    │       │   │       │   ├── download
    │       │   │       │   │   └── ui
    │       │   │       │   │       └── DownloadArchivoSection.kt
    │       │   │       │   ├── widgets
    │       │   │       │   │   ├── InfoMessageDialog.kt
    │       │   │       │   │   ├── InfoMessageSection.kt
    │       │   │       │   │   └── InfoActivationSection.kt
    │       │   │       │   └── settings
    │       │   │       │       ├── ui
    │       │   │       │       │   ├── SettingsRoute.kt
    │       │   │       │       │   └── SettingsScreen.kt
    │       │   │       │       └── logic
    │       │   │       │           ├── SettingsViewModel.kt
    │       │   │       │           └── SettingsState.kt
    │       │   ├── assets
    │       │   │   ├── index.html
    │       │   │   ├── main.js
    │       │   │   ├── webview-init.js
    │       │   │   ├── styles.css
    │       │   │   └── Logo.svg
    │       │   ├── res
    │       │   │   ├── drawable
    │       │   │   │   ├── sprit1.avif
    │       │   │   │   ├── sprit2.avif
    │       │   │   │   └── sprit3.webp
    │       │   │   ├── mipmap-anydpi
    │       │   │   ├── mipmap-hdpi
    │       │   │   ├── mipmap-xhdpi
    │       │   │   ├── mipmap-xxhdpi
    │       │   │   ├── mipmap-xxxhdpi
    │       │   │   ├── values
    │       │   │   └── xml
    │       │   └── AndroidManifest.xml
    │       ├── test
    │       ├── .gitignore
    │       ├── build.gradle.kts
    │       └── proguard-rules.pro
    ├── gradle
    ├── .gitignore
    ├── build.gradle.kts
    ├── gradle.properties
    ├── gradlew
    ├── gradlew.bat
    ├── local.properties
    └── settings.gradle.kts