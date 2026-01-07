# Luxury Cheats - Android

Luxury Cheats is a premium, minimalist Android application built with Jetpack Compose following the MVVM architecture. It focuses on clean UI, intentional design, and seamless integration with Firebase and an external dashboard.

## 🚀 Features

- **Premium UI**: Inspired by Apple's design philosophy—clean, calm, and content-focused.
- **MVVM Architecture**: Strict separation of concerns at the screen level for scalability and maintainability.
- **Dark/Light Mode**: Full support for system theme changes and adaptive colors.
- **Firebase Integration**: Robust backend services for authentication and user data.
- **Dynamic Configuration**: Powered by an external dashboard for real-time updates.
- **Security First**: Designed with a defensive mindset, using `StringProtector` and hardnened WebView implementations.

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Foundation**: Material Design 3 (Expressive)
- **Architecture**: MVVM (View-ViewModel-Stateflow)
- **Backend**: Firebase
- **Static Analysis**: Detekt & Ktlint

## 📂 Project Structure

The project follows a self-contained screen-level structure:

- `core/`: Shared components like themes, security, and utilities.
- `features/`: Modular screens (Welcome, Login, Home, Perfil, etc.), each containing its own UI and logic.
- `services/`: Dedicated layers for Firebase, API, and local storage.

## 🛠 Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/LuxuryCheats.git
   ```
2. **Firebase Setup**:
   - Add your `google-services.json` to the `app/` directory.
3. **Build**:
   - Open the project in Android Studio.
   - Sync Gradle and run the `:app` module.

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
