# Project Memory - Luxury Cheats

## Welcome Splash Screen - ImplementaciÃ³n

### DecisiÃ³n: Uso de WebView para Logo HTML/WebGL
**Fecha**: Diciembre 2025

- **Contexto**: El logo de la aplicaciÃ³n requiere renderizado WebGL con efectos visuales complejos implementados en HTML/JavaScript
- **DecisiÃ³n**: Usar `WebView` para mostrar el logo HTML/WebGL en lugar de un drawable vectorial tradicional
- **RazÃ³n**: Permite mantener la lÃ³gica visual compleja (efectos lÃ­quidos/metÃ¡licos) en el cÃ³digo web original sin necesidad de reimplementar en Compose
- **ImplementaciÃ³n**:
  - `WelcomeLogoSection.kt`: Composable que encapsula el WebView
  - WebView configurado con hardware acceleration para WebGL
  - Recursos cargados desde `assets/` usando interceptaciÃ³n de requests
- **SeparaciÃ³n de cÃ³digo**: JavaScript de inicializaciÃ³n separado en `webview-init.js` (no mezclado en Kotlin)
- **Archivos relacionados**:
  - `app/src/main/assets/index.html`
  - `app/src/main/assets/main.js` (lÃ³gica WebGL principal)
  - `app/src/main/assets/webview-init.js` (inicializaciÃ³n especÃ­fica para WebView)
  - `app/src/main/assets/styles.css`
  - `app/src/main/assets/Logo.svg`

### DecisiÃ³n: Background Animado Optimizado
**Fecha**: Diciembre 2025

- **Contexto**: Background animado con patrÃ³n de puntos requiere optimizaciÃ³n para evitar crashes

### DocumentaciÃ³n: Estructura de Firebase Realtime Database
**Fecha**: Enero 2026

- **Contexto**: Se requiere una referencia clara de la estructura de datos en Firebase para el desarrollo de lÃ³gica de backend y sincronizaciÃ³n.
- **DecisiÃ³n**: Documentar la estructura completa en `firebase.md` usando una representaciÃ³n de Ã¡rbol ASCII y descripciones detalladas de cada nodo (`app_update`, `device_tokens`, `notifications`, `urls`, `users`).
- **Resultado**: `firebase.md` sirve como la fuente de verdad para la organizaciÃ³n de datos en el backend.

- **DecisiÃ³n**: Usar una sola animaciÃ³n global con cÃ¡lculo de fase por punto en lugar de mÃºltiples animaciones
- **RazÃ³n**: Evita crear cientos/miles de animaciones individuales que causan crashes y problemas de rendimiento
- **ImplementaciÃ³n**:
  - `WelcomeBackgroundSection.kt`: Usa `rememberInfiniteTransition` global
  - Cada punto calcula su animaciÃ³n basado en tiempo global + fase Ãºnica
  - LÃ­mite de puntos (150x150 mÃ¡ximo) para control de rendimiento
- **Resultado**: Background estable sin crashes, manteniendo efecto visual deseado

### Alineación Universal Material You - Update Section
**Fecha**: Enero 2026

- **Contexto**: La `UpdateSection` todavía utilizaba colores fijos (Verde neón, Blanco) que no se adaptaban al sistema Material You.
- **Decisión**: Eliminar `GREEN_STATUS_HEX` y referencias a `Color.White` en favor de tokens semánticos de `MaterialTheme.colorScheme`.
- **Implementación**:
    - Se sustituyó el color de estado por `MaterialTheme.colorScheme.tertiary`, que en el esquema "Onyx Luxury" es naranja pero se adapta dinámicamente con Material You.
    - Los bordes y textos ahora usan `outline` y `onSurface` respectivamente.
- **Resultado**: Coherencia visual total con el resto de la aplicación y soporte completo para personalización de colores del sistema.

### Alineación Material You y Estabilidad de Preview - UpdateAnuncioSection
**Fecha**: Enero 2026

- **Contexto**: `UpdateAnuncioSection` no se visualizaba en el preview y tenía colores fijos en los bordes.
- **Decisión**: Eliminar el uso de `Color.White` en bordes y limpiar los wrappers de `Surface` en los previews para usar el esquema de colores dinámico.
- **Implementación**:
    - Sustitución de `Color.White` por `MaterialTheme.colorScheme.outline`.
    - Actualización de `UpdateAnuncioSectionPreviewDark/Light` para usar `MaterialTheme.colorScheme.background`.
- **Resultado**: El componente ahora es totalmente adaptativo y visible establemente en el IDE.

### Estructura de Assets para WebView
**Fecha**: Diciembre 2025

- **OrganizaciÃ³n**: Todos los recursos web (HTML, CSS, JS, SVG) en `app/src/main/assets/`
- **Principio**: SeparaciÃ³n clara entre cÃ³digo Kotlin y cÃ³digo JavaScript
- **Archivos**:
  - `index.html`: Estructura HTML base
  - `main.js`: LÃ³gica principal de WebGL y efectos visuales
  - `webview-init.js`: InicializaciÃ³n especÃ­fica para entorno WebView (altura, verificaciÃ³n WebGL)
  - `styles.css`: Estilos CSS
  - `Logo.svg`: Logo en formato SVG
- **Carga**: WebView intercepta requests y carga recursos desde assets usando `shouldInterceptRequest`

### Arquitectura de Secciones UI
**Fecha**: Diciembre 2025

- **Enfoque**: Secciones UI screen-scoped (no reutilizables por defecto)
- **Welcome Splash Screen**:
  - `WelcomeSplashScreen.kt`: Pantalla principal que compone secciones
  - `WelcomeBackgroundSection.kt`: Background con patrÃ³n de puntos (secciÃ³n visual)
  - `WelcomeSpraysSection.kt`: Sprites decorativos (secciÃ³n visual)
  - `WelcomeLogoSection.kt`: Logo WebView (secciÃ³n visual)
- **Principio**: Cada secciÃ³n es independiente y especÃ­fica para esta pantalla

### Sistema de Temas Adaptativos - ThemeManager
**Fecha**: Diciembre 2025

- **Contexto**: Necesidad de temas adaptativos con Material You y modo claro/oscuro
- **DecisiÃ³n**: Crear `ThemeManager` como objeto singleton para gestionar toda la lÃ³gica de temas
- **RazÃ³n**: 
  - SeparaciÃ³n de lÃ³gica de UI (cumple AGENTS.md)
  - CentralizaciÃ³n de gestiÃ³n de temas
  - Facilita mantenimiento y extensiÃ³n
- **ImplementaciÃ³n**:
  - `ThemeManager.kt`: Object singleton con lÃ³gica de temas
  - Soporte para Material You (Dynamic Colors) en Android 12+
  - DetecciÃ³n automÃ¡tica de modo claro/oscuro del sistema
  - ColorSchemes premium y minimalistas como fallback
  - `Theme.kt`: Composable que usa ThemeManager (solo UI)
- **CaracterÃ­sticas**:
  - Material You: Se adapta automÃ¡ticamente al fondo de pantalla del dispositivo
  - Modo claro/oscuro: Detecta automÃ¡ticamente el tema del sistema
  - Fallback: ColorSchemes personalizados cuando Material You no estÃ¡ disponible
  - Colores premium: Paleta minimalista y elegante
- **Uso**: Toda la app usa `LuxuryCheatsTheme` que internamente usa `ThemeManager`

### SeparaciÃ³n de LÃ³gica y UI - WebView Configuration
**Fecha**: Diciembre 2025

- **Contexto**: ConfiguraciÃ³n compleja del WebView para logo HTML/WebGL requiere optimizaciones de rendimiento
- **DecisiÃ³n**: Separar toda la lÃ³gica de configuraciÃ³n del WebView en clase dedicada (`WelcomeLogoWebViewConfig.kt`)
- **RazÃ³n**: 
  - Mantener composables limpios (solo renderizado)
  - Facilitar mantenimiento y testing
  - Cumplir con principios de AGENTS.md (separaciÃ³n lÃ³gica/UI)
- **ImplementaciÃ³n**:
  - `WelcomeLogoWebViewConfig.kt`: Object singleton con toda la lÃ³gica de configuraciÃ³n
  - Hardware acceleration forzado (`LAYER_TYPE_HARDWARE`)
  - WebGL2 con fallback a WebGL y experimental-webgl
  - Optimizaciones de FPS (clamping de deltaTime, tracking opcional)
  - ConfiguraciÃ³n de settings optimizada para rendimiento
- **Resultado**: CÃ³digo mÃ¡s organizado, mantenible y optimizado para rendimiento

### OptimizaciÃ³n CrÃ­tica de Carga WebView - EliminaciÃ³n de Cuellos de Botella
**Fecha**: Diciembre 2025

- **Contexto**: WebView demoraba mucho en mostrarse debido a varios cuellos de botella
- **Problemas identificados**:
  - `shouldInterceptRequest` retrasaba el primer render
  - `evaluateJavascript` + `postDelayed(200ms)` agregaba delays innecesarios
  - `LOAD_DEFAULT` hacÃ­a checks de cache innecesarios en cold start
  - WebView visible desde el inicio bloqueaba la percepciÃ³n de carga
- **Decisiones y optimizaciones**:
  - **Eliminado `shouldInterceptRequest`**: HTML ahora usa rutas correctas (`file:///android_asset/`)
  - **Eliminado `evaluateJavascript` y `postDelayed`**: JavaScript inicializa WebGL solo y notifica a Android
  - **JavaScript Interface**: ComunicaciÃ³n JS -> Android usando `@JavascriptInterface` (sin delays)
  - **Cache optimizado**: `LOAD_NO_CACHE` para assets locales (mÃ¡s rÃ¡pido que `LOAD_DEFAULT`)
  - **WebView invisible inicialmente**: Se muestra solo cuando estÃ¡ listo (alpha = 0f inicialmente)
  - **NotificaciÃ³n temprana**: JavaScript notifica cuando WebGL estÃ¡ listo (despuÃ©s de cargar textura y iniciar render)
- **ImplementaciÃ³n**:
  - `index.html`: Rutas corregidas a `file:///android_asset/`
  - `main.js`: Notifica a Android vÃ­a `window.AndroidInterface.onWebGLReady()` cuando estÃ¡ listo
  - `WelcomeLogoWebViewConfig.kt`: Eliminada interceptaciÃ³n, agregado JavaScriptInterface, cache optimizado
  - `WelcomeLogoSection.kt`: WebView invisible hasta que estÃ© listo, luego se muestra con animaciÃ³n
- **Resultado**: Carga significativamente mÃ¡s rÃ¡pida, sin delays artificiales, mejor percepciÃ³n de rendimiento

### Background Pattern EstÃ¡tico con Desvanecimiento
**Fecha**: Diciembre 2025

- **Contexto**: Background de puntos estÃ¡tico requiere adaptaciÃ³n a tema claro/oscuro y desvanecimiento superior
- **DecisiÃ³n**: Implementar patrÃ³n de puntos estÃ¡tico (sin animaciÃ³n) con desvanecimiento gradual en la parte superior
- **RazÃ³n**: 
  - Efecto visual premium y minimalista
  - AdaptaciÃ³n automÃ¡tica a tema claro/oscuro
  - Desvanecimiento superior para mejor integraciÃ³n visual
-345. **CorrecciÃ³n de API Experimental - FlowRow (Diciembre 2025)**:
- **DecisiÃ³n**: Se aÃ±adiÃ³ `@OptIn(ExperimentalLayoutApi::class)` en `WelcomePage1BadgesSection.kt`.
- **RazÃ³n**: `FlowRow` es experimental en Compose y bloqueaba la compilaciÃ³n en modo Release.

346. **RediseÃ±o de Selector de Idioma - Split Button M3 (Diciembre 2025)**:
- **DecisiÃ³n**: Se rediseÃ±Ã³ `WelcomePage1LanguageSection.kt` siguiendo el estilo "Split Button" de Material Design 3.
- **ImplementaciÃ³n**: 
    - Contenedor con forma de pÃ­ldora (24.dp) y fondo translÃºcido adaptativo.
    - SegmentaciÃ³n mediante `VerticalDivider` entre el texto principal y la flecha de dropdown.
    - `DropdownMenu` integrado con las opciones "EspaÃ±ol" e "InglÃ©s".
    - Estado reactivo para actualizar el texto del botÃ³n tras la selecciÃ³n.
- **Resultado**: Interfaz de selecciÃ³n de idioma mÃ¡s profesional e intuitiva.
- **ImplementaciÃ³n**:
  - `WelcomeBackgroundSection.kt`: PatrÃ³n de puntos estÃ¡tico usando Canvas
  - Colores adaptativos usando `MaterialTheme.colorScheme.onBackground`
  - Tema oscuro: puntos casi blancos (alpha 0.6)
  - Tema claro: tono plomo (alpha 0.4)
  - Desvanecimiento superior: curva suave (ease-in) en los primeros ~1500px desde arriba
  - Fondo adaptativo usando `MaterialTheme.colorScheme.background`
- **Tema Oscuro Personalizado**:
  - Color de fondo forzado a #080808 en modo oscuro (incluso con Material You)
  - Implementado en `ThemeManager.getColorScheme()` para aplicar a toda la app
- **Resultado**: Background premium, adaptativo y visualmente integrado

### Eclipse Decorativo con Blur
**Fecha**: Diciembre 2025

- **Contexto**: Necesidad de elemento decorativo con efecto morado (eclipse) para mejorar la composiciÃ³n visual del splash
- **DecisiÃ³n**: Crear secciÃ³n independiente `WelcomeEclipseSection.kt` con forma elÃ­ptica, blur y stroke
- **RazÃ³n**: 
  - SeparaciÃ³n de responsabilidades (cumple AGENTS.md - secciones screen-scoped)
  - Efecto visual premium con blur nativo de Android
  - FÃ¡cil personalizaciÃ³n de posiciÃ³n, tamaÃ±o y colores
- **ImplementaciÃ³n**:
  - `WelcomeEclipseSection.kt`: SecciÃ³n independiente usando Canvas
  - Forma elÃ­ptica (no circular) para mejor integraciÃ³n visual
  - Color de relleno: #911AA9 con 50% de transparencia
  - Stroke: 90dp de grosor, color #270931 con 60% de transparencia
  - Blur de 80dp usando `RenderEffect.createBlurEffect()` (Android 12+)
  - Posicionado en la parte inferior de la pantalla (95% desde arriba)
  - TamaÃ±o configurable: 352dp x 112dp (ajustable)
- **Compatibilidad**: Blur solo funciona en Android 12+ (API 31+), pero el eclipse es visible en todas las versiones
- **Resultado**: Elemento decorativo premium que mejora la composiciÃ³n visual del splash

### Texto "LUXURY" con AnimaciÃ³n de Entrada
**Fecha**: Diciembre 2025

- **Contexto**: Necesidad de texto "LUXURY" debajo del logo con animaciÃ³n de entrada elegante
- **DecisiÃ³n**: Crear secciÃ³n independiente `WelcomeLuxuryTextSection.kt` con animaciÃ³n fade in + slide up
- **RazÃ³n**: 
  - SeparaciÃ³n de responsabilidades (cumple AGENTS.md - secciones screen-scoped)
  - LÃ³gica de animaciÃ³n separada en `WelcomeLogoAnimation.getLuxuryTextAnimation()`
  - Texto centrado en pantalla, independiente del logo
  - FÃ¡cil personalizaciÃ³n mediante parÃ¡metros configurables
- **ImplementaciÃ³n**:
  - `WelcomeLuxuryTextSection.kt`: SecciÃ³n independiente para el texto
  - `WelcomeLogoAnimation.getLuxuryTextAnimation()`: LÃ³gica de animaciÃ³n separada
  - AnimaciÃ³n: fade in (alpha 0â1) + slide up (offset Y 30dpâ0)
  - Delay configurable antes de iniciar animaciÃ³n (300ms por defecto)
  - DuraciÃ³n configurable (600ms por defecto)
  - ParÃ¡metros personalizables: texto, tamaÃ±o, espaciado, posiciÃ³n, peso de fuente
  - Texto centrado en pantalla por defecto (verticalPosition = 0.5f)
- **Arquitectura**: 
  - LÃ³gica de animaciÃ³n en `logic/logo/WelcomeLogoAnimation.kt` (separada de UI)
  - UI solo renderiza estado (cumple AGENTS.md)
  - SecciÃ³n screen-scoped (no reutilizable, especÃ­fica para Welcome Splash)
- **Resultado**: Texto premium con animaciÃ³n elegante, fÃ¡cilmente personalizable

### Componente Reutilizable: DotPatternBackground
**Fecha**: Diciembre 2025

- **Contexto**: Background de puntos se necesita en mÃºltiples pantallas, no solo en splash
- **DecisiÃ³n**: Mover `WelcomeBackgroundSection.kt` a `core/ui/DotPatternBackground.kt` como componente reutilizable
- **RazÃ³n**: 
  - Componente serÃ¡ usado en varias pantallas (splash, page1, etc.)
  - Cumple con principio de reutilizaciÃ³n cuando hay duplicaciÃ³n real
  - UbicaciÃ³n en `core/ui/` indica que es un componente compartido
- **ImplementaciÃ³n**:
  - `core/ui/DotPatternBackground.kt`: Componente reutilizable
  - Renombrado de `WelcomeBackgroundSection` a `DotPatternBackground` (nombre mÃ¡s genÃ©rico)
  - Mantiene toda la funcionalidad: adaptativo a tema, desvanecimiento superior, configuraciÃ³n de puntos
  - `WelcomeSplashScreen.kt`: Actualizado para usar `DotPatternBackground` desde core
- **Resultado**: Componente reutilizable disponible para todas las pantallas que lo necesiten

### Estructura MVVM Completa - Splash y Page1
**Fecha**: Diciembre 2025

- **Contexto**: Completar estructura MVVM para splash y crear estructura completa para page1
- **DecisiÃ³n**: Implementar archivos Action, State y ViewModel siguiendo patrÃ³n MVVM estricto
- **RazÃ³n**: 
  - Cumplir con AGENTS.md: MVVM aplicado estrictamente a nivel de pantalla
  - SeparaciÃ³n clara de lÃ³gica y UI
  - Facilita mantenimiento y testing
- **ImplementaciÃ³n Splash**:
  - `WelcomeSplashAction.kt`: Acciones (NavigateToNext)
  - `WelcomeSplashState.kt`: Estado (isReadyToNavigate)
  - `WelcomeSplashViewModel.kt`: ViewModel que maneja lÃ³gica y estado
- **ImplementaciÃ³n Page1**:
  - `WelcomePage1Route.kt`: Constante de ruta para navegaciÃ³n
  - `WelcomePage1Screen.kt`: Composable de la pantalla (solo UI)
  - `WelcomePage1Action.kt`: Acciones (ContinueClicked, BackClicked)
  - `WelcomePage1State.kt`: Estado (isLoading, error)
  - `WelcomePage1ViewModel.kt`: ViewModel que maneja lÃ³gica
- **Estructura**:
  ```
  splash/logic/
    âââ WelcomeSplashAction.kt
    âââ WelcomeSplashState.kt
    âââ WelcomeSplashViewModel.kt
  
  page1/bienvenida/
    âââ ui/
    â   âââ WelcomePage1Route.kt
    â   âââ WelcomePage1Screen.kt
    âââ logic/
        âââ WelcomePage1Action.kt
        âââ WelcomePage1State.kt
        âââ WelcomePage1ViewModel.kt
  ```
- **Resultado**: Estructura MVVM completa y consistente, lista para implementar UI y lÃ³gica de negocio

### OrganizaciÃ³n del Logo WebView - Secciones EspecÃ­ficas por Pantalla
**Fecha**: Diciembre 2025

- **Contexto**: Logo WebView se necesita en mÃºltiples pantallas (splash y page1) con diferentes configuraciones (animaciÃ³n, tamaÃ±o)
- **DecisiÃ³n**: Crear secciones especÃ­ficas para cada pantalla en lugar de un componente genÃ©rico
- **RazÃ³n**: 
  - Cada pantalla tiene requisitos especÃ­ficos (splash: animaciÃ³n + 500.dp, page1: sin animaciÃ³n + 280.dp)
  - Mejor organizaciÃ³n: cada pantalla tiene su secciÃ³n (cumple AGENTS.md - secciones screen-scoped)
  - Comparten el WebView precargado pero con configuraciones diferentes
- **Estructura Organizada**:
  - **Core (compartido)**:
    - `core/ui/LogoWebViewConfig.kt`: ConfiguraciÃ³n del WebView (compartida)
    - `core/ui/LogoWebViewManager.kt`: Manager que mantiene el WebView precargado (compartido)
  - **Splash (especÃ­fico)**:
    - `splash/ui/WelcomeLogoSection.kt`: SecciÃ³n especÃ­fica con animaciÃ³n y tamaÃ±o 500.dp
    - `splash/logic/logo/WelcomeLogoAnimation.kt`: LÃ³gica de animaciÃ³n (solo para splash)
  - **Page1 (especÃ­fico)**:
    - `page1/ui/WelcomePage1LogoSection.kt`: SecciÃ³n especÃ­fica sin animaciÃ³n y tamaÃ±o 280.dp
- **ImplementaciÃ³n**:
  - Ambas secciones usan `LogoWebViewManager` para obtener el mismo WebView precargado
  - Splash: Aplica animaciÃ³n usando `WelcomeLogoAnimation.getLogoScaleAnimation()`
  - Page1: Sin animaciÃ³n, solo muestra el WebView con tamaÃ±o mÃ¡s pequeÃ±o
  - Ambas estÃ¡n centradas usando `Box` con `Alignment.Center`
- **Resultado**: OrganizaciÃ³n clara donde cada pantalla tiene su secciÃ³n especÃ­fica, pero comparten el WebView precargado

### WebView Compartido y Reutilizable - LogoWebViewManager
**Fecha**: Diciembre 2025

- **Contexto**: WebView del logo se recargaba cada vez que se cambiaba de pantalla, perdiendo el estado precargado
- **DecisiÃ³n**: Crear `LogoWebViewManager` singleton que mantiene el WebView precargado y lo reutiliza entre pantallas
- **RazÃ³n**: 
  - Evitar recargar el WebView en cada pantalla
  - Mantener el estado precargado del WebGL
  - Mejor rendimiento y experiencia de usuario
- **ImplementaciÃ³n**:
  - `LogoWebViewManager.kt`: Singleton que mantiene el WebView compartido
  - `getOrCreateWebView()`: Crea el WebView solo la primera vez, luego lo reutiliza
  - Maneja callbacks cuando el WebView estÃ¡ listo
  - `LogoWebView.kt`: Actualizado para usar el manager y reutilizar el WebView
- **Uso**:
  - Splash: Crea y precarga el WebView
  - Page1: Reutiliza el mismo WebView (no recarga)
- **Resultado**: WebView se carga una sola vez y se reutiliza en todas las pantallas, mejorando rendimiento

### AnimaciÃ³n de TransiciÃ³n entre Pantallas
**Fecha**: Diciembre 2024

- **Contexto**: Necesidad de animaciÃ³n visual al cambiar de splash a page1 para identificar el cambio de pantalla
- **DecisiÃ³n**: Implementar animaciÃ³n de desvanecimiento (fade) rÃ¡pida y fluida usando `AnimatedContent`
- **RazÃ³n**: 
  - TransiciÃ³n visual clara entre pantallas
  - AnimaciÃ³n rÃ¡pida (300ms) para no interrumpir el flujo
  - Efecto premium y profesional
- **ImplementaciÃ³n**:
  - `MainApp.kt`: Usa `AnimatedContent` con `fadeIn` y `fadeOut`
  - DuraciÃ³n: 300ms (rÃ¡pido y fluido)
  - TransiciÃ³n automÃ¡tica cuando cambia `currentRoute`
- **Resultado**: TransiciÃ³n suave y profesional entre pantallas que mejora la experiencia de usuario


### SoluciÃ³n de Flicker (White Flash) en Transiciones
**Fecha**: Diciembre 2025

- **Contexto**: Al navegar entre Splash y Page1 usando `AnimatedContent`, se percibÃ­a un destello blanco (flicker).
- **DecisiÃ³n**: Envolver toda la aplicaciÃ³n en un `Surface` con el color de fondo del tema en `MainApp.kt`.
- **RazÃ³n**: `AnimatedContent` realiza un cross-fade donde las capas pueden ser parcialmente transparentes, revelando el fondo de la Activity (que por defecto es blanco). Un `Surface` base garantiza un fondo sÃ³lido constante.
- **Resultado**: Transiciones suaves y sin parpadeos visuales.

### RefactorizaciÃ³n por Reglas de Detekt
**Fecha**: Diciembre 2025

- **Contexto**: El anÃ¡lisis estÃ¡tico reportÃ³ errores de complejidad y mantenimiento.
- **Decisiones**:
    - **ComplexCondition**: En `DotPatternBackground.kt`, se reemplazÃ³ la lÃ³gica manual de lÃ­mites por `Rect(Offset.Zero, size).contains(dot)`.
    - **LongParameterList**: En `WelcomeTextStyle` se agruparon parÃ¡metros.
    - **WildcardImports**: EliminaciÃ³n de imports `.*`.
- **RazÃ³n**: Mejorar la legibilidad y cumplir con `AGENTS.md`.

### SincronizaciÃ³n Avanzada de AnimaciÃ³n de Logo
**Fecha**: Diciembre 2025

- **Contexto**: El logo debe aparecer sutilmente sincronizado con los sprays.
- **Decisiones**:
    - **Estado Reactivo**: Uso de `StateFlow<Boolean>` en `LogoWebViewManager` para detectar WebGL listo.
    - **OrquestaciÃ³n**: Delay de 800ms en `WelcomeSplashScreen.kt`.
    - **Refinamiento**: AnimaciÃ³n de entrada ("jump") desde scale 0.8f.
- **Resultado**: AnimaciÃ³n fluida e integrada.

### OptimizaciÃ³n de CachÃ© de WebView
**Fecha**: Diciembre 2025

- **Contexto**: Acelerar carga del logo en aperturas sucesivas.
- **DecisiÃ³n**: Cambiar `cacheMode` a `LOAD_CACHE_ELSE_NETWORK` y habilitar `databaseEnabled`.
- **Resultado**: Carga casi instantÃ¡nea tras el primer inicio.

### Refinamiento de UI en Welcome Page 1 (Fidelidad Visual)
**Fecha**: Diciembre 2025

- **Contexto**: Ajuste fino de la Page 1 para coincidir exactamente con la imagen de referencia.
- **Decisiones**:
    - **BotÃ³n de Lenguaje**: Ubicado en el top-right con diseÃ±o minimalista.
    - **Logo con Texto**: Texto "LUXURY" agregado debajo del logo square.
    - **Insignias (Badges)**: OrganizaciÃ³n precisa en filas de 2 y 3 elementos para balance visual.
    - **Typography**: Ajuste de `lineHeight` y tamaÃ±os para mÃ¡xima legibilidad.
- **Resultado**: Interfaz de alta gama que cumple con la visiÃ³n del usuario.

### [MetadocumentaciÃ³n y Reglas de Agente]
**Fecha**: Diciembre 2024

- **Nueva Regla de Consulta**: El agente debe leer obligatoriamente `AGENTS.md` antes de cada tarea significativa para asegurar alineaciÃ³n con la filosofÃ­a "Luxury".
- **ImplementaciÃ³n**: Se ha creado el workflow `.agent/workflows/always-read-agents.md` que sistematiza este paso.
- **RazÃ³n**: Mantener el control estricto sobre la arquitectura (MVVM por pantalla), la estÃ©tica (Apple-inspired) y las reglas de seguridad pragmÃ¡tica.

### [Optimización del Ciclo de Vida del Logo]
**Fecha**: Diciembre 2024

- **Contexto**: El Logo WebView ya no es necesario tras superar la introducción básica.
- **Decisión**: Limitar la visibilidad del logo únicamente al Splash y a la Page 1.
- **Implementación**: 
    - Modificado PersistentLogo.kt para desvanecer el logo (alpha 0) si la ruta es distinta de Splash (null) o WELCOME_PAGE1_ROUTE.
    - Optimización: El componente deja de renderizarse (if (logoAlpha > 0f)) una vez que el desvanecimiento completa, ahorrando recursos.
- **Resultado**: Transición visual impecable y liberación de carga de UI para las pantallas de lógica (Permisos, etc.).

### [Corrección de Bloqueo de Navegación Splash]
**Fecha**: Diciembre 2024

- **Incidente**: La app se bloqueaba en el Splash y el logo no aparecía.
- **Causa**: El condicional if (logoAlpha > 0f) impedía la composición inicial del WebView, afectando los estados de preparación (isReady).
- **Solución**: Se eliminó el bloqueo estructural pero se mantuvo el control de visibilidad por Alpha.
- **Resultado**: Navegación restaurada y visibilidad del logo corregida en Splash y Page 1, manteniendo el fade-out en Page 2.

### [Ajuste de Dimensiones Page 2]
**Fecha**: Diciembre 2024

- **Contexto**: Refinamiento visual para evitar estiramiento excesivo en pantallas anchas.
- **Decisión**: Fijar ancho de secciones centrales a 260.dp y redimensionar NavBar.
- **Dimensiones Aplicadas**: 
    - Secciones Informativas: W:260.dp
    - NavBar: W:372.dp H:61.dp
    - Botón Back: W:106.dp H:38.dp
    - Botón Siguiente: W:125.dp H:38.dp
- **Resultado**: Interfaz más compactada y visualmente equilibrada.

### [Corrección Modular de Dimensiones Page 2]
**Fecha**: Diciembre 2024

- **Incidente**: Errores de compilación Unresolved reference width en WelcomePage2Screen.
- **Solución**: Mover la lógica de ancho (260.dp) directamente a cada archivo de sección individual.
- **Beneficio**: Los componentes son ahora totalmente autónomos y no requieren modificadores externos, lo que previene errores de importación y simplifica el orquestador principal.

### [Modularización de Espaciado Page 2]
**Fecha**: Diciembre 2024

- **Decisión**: Eliminar Spacers centralizados y mover márgenes a secciones individuales.
- **Implementación**: 
    - WelcomePage2Screen.kt ahora solo instancia secciones sin Spacers.
    - Cada sección usa Modifier.padding(top = ...) para su propio aire visual.
    - Permission4Section incluye ottom = 140.dp para compensar la NavBar.
- **Resultado**: Código más limpio, modular y fiel a la arquitectura screen-scoped.

### [Soporte Temas Dinámicos Page 2]
**Fecha**: Diciembre 2024

- **Contexto**: Inconsistencia visual en modo claro/oscuro identificada por el usuario.
- **Decisión**: Implementar isSystemInDarkTheme() en cada componente de sección.
- **Esquema de Colores**: 
    - Oscuro: Fondo #Black, Contenedores #202020, Texto #White.
    - Claro: Fondo #White, Contenedores #F5F5F5, Texto #Black.
- **Resultado**: Interfaz 100% adaptativa que respeta la configuración del sistema manteniendo el diseño premium.

### [Personalización de Iconografía Page 2]
**Fecha**: Diciembre 2024

- **Cambio**: Reemplazo de placeholders por iconos temáticos en permisos.
- **Iconos Aplicados**: 
    - Almacenamiento: Folder
    - Notificaciones: Notifications
    - Administrador: Security
    - Superposición: Layers
- **Resultado**: Interfaz visualmente más semántica y clara para el usuario final.

### [Implementación de Lógica de Permisos Page 2]
**Fecha**: Diciembre 2024

- **Contexto**: Necesidad de permisos críticos para el funcionamiento de los cheats.
- **Permisos Implementados**: 
    - Almacenamiento: Uso de MANAGE_EXTERNAL_STORAGE para acceso total.
    - Notificaciones: Soporte para Android 13+.
    - Administrador: Configuración de DeviceAdminReceiver y políticas XML.
    - Superposición: Lanzamiento de ajustes para SYSTEM_ALERT_WINDOW.
- **Feedback Visual**: Aplicación dinámica de borde 1dp verde (#00FF00) mediante isGranted en el estado.
- **Arquitectura**: MVVM con LaunchedEffect para refresco de estado automático al volver de los ajustes del sistema.

### [Depuración Lógica de Permisos Page 2]
**Fecha**: Diciembre 2024

- **Problema**: Clics en permisos inactivos y falta de refresco dinámico.
- **Solución**: 
    - Mover lanzamiento de Intents a WelcomePage2Screen usando el contexto de la Activity.
    - Implementar DisposableEffect con LifecycleEventObserver para refrescar estado en ON_RESUME.
    - Añadir soporte para almacenamiento legacy (Android < 11).
- **Arquitectura**: Desacoplamiento de Intents del ViewModel para mejorar la estabilidad y cumplimiento de políticas de Android.

### Universal Material You (Enero 2026)
- Adopted Material You (Dynamic Colors) as the global visual standard for Android 12+ devices.
- Refactored ThemeManager and Theme.kt to prioritize system-based adaptive palettes.
- Eradicated hardcoded colors in core UI components (LuxuryNavigationBar, Login, Welcome, etc.) in favor of semantic M3 tokens.
- Implemented mathematical color harmonization for brand assets (LuxuryOrange).

### Refactor de Teclado y LÃgica de AnimaciÃn
**Fecha**: Enero 2026
- **SeparaciÃn de UI**: Se dividieron los teclados en archivos independientes: AlphabeticTeclado.kt, NumericTeclado.kt y TecladoKey.kt.
- **LÃgica vs UI**: Los estados de animaciÃn (showPhantomCursor, lastFocusedField) se movieron al LoginPantallaState.
- **SincronizaciÃn**: El LoginPantallaViewModel ahora orquesta los disparadores de animaciÃn (como el salto del cursor) para mantener los Composables puros.

### Optimización de Autenticación - Persistencia y Sincronización (Enero 2026)
- **Contexto**: El login era lento debido a la falta de persistencia y la necesidad de consultar al servidor en cada intento.
- **Decisión**: 
  1. Habilitar `setPersistenceEnabled(true)` en la clase `LuxuryApp`.
  2. Implementar `keepSynced(true)` en el nodo `/users`.
- **Razón**: 
  - La persistencia permite que el SDK de Firebase use una base de datos local (SQLite) para cachear datos.
  - `keepSynced(true)` garantiza que el nodo de usuarios se mantenga actualizado en segundo plano, permitiendo que las consultas `orderByChild` se realicen localmente sobre la caché, logrando velocidad instantánea.
- **Implementación**: 
  - `LuxuryApp.kt`: Habilitación de persistencia global.
  - `AuthService.kt`: Aplicación de `keepSynced(true)` al inicializar la referencia de la base de datos.
- **Resultado**: Login ultra rápido incluso con conexiones inestables una vez realizada la primera sincronización.

## 2026-01-07: Sistema de Actualización y Panel de Pruebas
- **Contexto**: Se requería mejorar la estabilidad de los Previews en `UpdateSection` y `UpdateAnuncioSection`, y conectar el flujo de actualizaciones.
- **Decisiones**:
  - **Estabilidad de Preview**: Se implementó `LocalInspectionMode.current` en `DownloadWebLogo` (para evitar fallos de `WebView`) y en `UpdateBanner` (para evitar fallos de renderizado del formato AVIF en el IDE). Se utilizan placeholders (iconos y colores sólidos) en tiempo de diseño.
  - **Material You**: Se reemplazaron colores hardcoded por tokens temáticos (`primary`, `surface`, `outline`, etc.) asegurando que la UI de actualización se adapte dinámicamente al sistema.
  - **Navegación**: 
    - Se registró la ruta `UPDATE` en `NavRoutes.kt` y `AppNavHost.kt`.
    - Se conectó el botón del anuncio con la pantalla de descarga.
    - Se conectó el botón de "Descargar" en `UpdateSection` con el `DownloadArchivoBottomSheet` usando un estado de visibilidad local.
  - **Infraestructura de Pruebas**: Se creó `HomeTestSection` en `HomeTest.kt` como un panel efímero integrado en la `HomeScreen`. Este permite disparar el anuncio de actualización mediante un diálogo para verificar el flujo completo de navegación sin depender de eventos de backend todavía.
- **Archivos Clave**:
  - `UpdateSection.kt`, `UpdateAnuncioSection.kt`, `HomeTest.kt`.
  - `AppNavHost.kt`, `NavRoutes.kt`.

### Refactor MVVM - Update Section e Integración de Descarga
**Fecha**: Enero 2026

- **Contexto**: La `UpdateSection` manejaba el estado de la hoja de descarga localmente y carecía de una estructura MVVM completa.
- **Decisión**: Implementar `UpdateViewModel`, `UpdateState` y `UpdateAction` para centralizar la lógica de la pantalla de actualización.
- **Implementación**:
    - Se crearon `UpdateState.kt`, `UpdateAction.kt` y `UpdateViewModel.kt` en el paquete `logic`.
    - `DownloadUpdateScreen` ahora recibe el `UpdateViewModel` y observa su estado.
    - El botón "DESCARGAR" dispara la acción `UpdateAction.DownloadClicked`.
    - La visibilidad de `DownloadArchivoBottomSheet` (en `DownloadArchivoSection.kt`) está vinculada al estado del ViewModel.
- **Resultado**: Código más limpio, siguiendo la arquitectura del proyecto y facilitando la expansión futura de la lógica de descarga.

### Simplificación UI Update Section - Eliminación de Logo
**Fecha**: Enero 2026

- **Contexto**: El logo de WebView en la pantalla de actualización ya no es necesario y causaba ruido visual innecesario.
- **Decisión**: Eliminar el componente `DownloadWebLogo` y su uso en `UpdateSection.kt`, manteniendo solo el icono estático de la aplicación.
- **Implementación**:
    - Se eliminó la función `DownloadWebLogo` que encapsulaba el WebView del logo.
    - Se implementó `DownloadAppIcon` para mostrar el icono `Verified` de forma estática, manteniendo la coherencia visual sin la carga del WebView.
    - Se eliminaron las dependencias de WebView en `UpdateSection.kt` (Manager de WebView, AndroidView, etc.).
- **Resultado**: Interfaz de actualización más limpia, rápida y enfocada, preservando la identidad visual con el icono estático.

### Widget Informativo: InfoActivationSection
**Fecha**: Enero 2026

- **Contexto**: Necesidad de una guía visual rápida para los usuarios sobre cómo activar las funciones de la app.
- **Decisión**: Crear un Bottom Sheet modal con un diseño de "stacked card" de dimensiones fijas (360x388).
- **Implementación**:
    - `InfoActivationSection.kt`: Contiene el Bottom Sheet y el contenido de la tarjeta.
    - Diseño: Header destacado, imagen ilustrativa generada por IA (`info_activation_guide`), descripción concisa y botones de acción (Continuar/Cancelar).
    - Estilo: Bordes muy redondeados (40.dp) y colores adaptativos Material You.
    - **Integración**: Conectado al "TEST BUTTON 2" en el `HomeTestSection` para facilitar pruebas de UI sin disparadores de backend.
- **Resultado**: Un componente de ayuda premium y coherente con el lenguaje visual de la aplicación, fácilmente accesible para pruebas.

### Widget de Mensaje: InfoMessageSection
**Fecha**: Enero 2026

- **Contexto**: Necesidad de un widget estructurado para mostrar mensajes importantes con jerarquía visual clara.
- **Decisión**: Implementar un diseño de tarjeta con cabecera flotante y cuerpo de dimensiones fijas (350x439) sobre un contenedor total de 350x472.
- **Implementación**:
    - `InfoMessageSection.kt`: Widget principal con estructura de tres capas (Cabecera, Cuerpo, Contenido Interno).
    - Diseño Interno: Box de 308x281 para el contenido del texto (con avatar) e imagen de 288x123.
    - Estilo: Uso de etiquetas ("Recurrente"), tipografía adaptativa y botones de acción Material You.
    - **Estética Adaptive**: 
        - Soporte para Tema Claro/Oscuro y Material You dinámico.
        - Bordes (Strokes) Blancos: Exterior (46% alpha), Cuerpo (20% alpha).
        - **Refinamientos**: Cabecera a la izquierda (30% alpha), botón de acción compactado (100x37) y alineación de etiquetas optimizada.
    - **Integración**: Conectado al "TEST BUTTON 3" en el `HomeTestSection`.
## MalSection Widget (Error Feedback)
- **Propósito**: Proporcionar retroalimentación visual inmediata tras un error en la instalación o proceso.
- **Ubicación**: `com.luxury.cheats.features.widgets.MalSection.kt`
- **Diseño**:
    - **Aura de Error**: Red RadialGradient (200x200, ~15-40% alpha).
    - **Icono**: Close (X) en color rojo.
    - **Tipografía**: Título "INCORRECTO" (24sp, Bold), Mensaje "Tuvimos problema con la instalacion" (12sp).
    - **Acciones**: Botón "Ver problemas" (estilo errorContainer) y Botón "SALIR" (TextButton rojo).
- **Integración**: Conectado al "TEST BUTTON 5" en el `HomeTest`.

---

- **Resultado**: Sistema de feedback binario (Éxito/Error) completo y consistente.
