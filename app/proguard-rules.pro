# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\lux\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# and each project's build.gradle calling proguardFiles(...)

# Retrofit & Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class com.google.gson.** { *; }

# Models used in API
-keep class com.luxury.cheats.services.freefireapi.** { *; }

# Navigation Routes (Crucial for isAuthScreen logic)
-keep class com.luxury.cheats.navigations.** { *; }

# Android Components
-keep class **.ui.** { *; }

# ViewModels
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
