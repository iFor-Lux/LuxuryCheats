# Optimization
-optimizationpasses 5
-dontpreverify
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

# Remove logs in release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# Retrofit / Gson
-keepattributes Signature
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations

-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Retrofit service interface
-keep interface com.luxury.cheats.services.freefireapi.FreeFireApiService

# Navigation Compose
-keepnames class com.luxury.cheats.navigations.**

# ViewModels
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Hilt / DI
-keep class dagger.hilt.internal.** { *; }
-keepnames class javax.inject.**
-keepnames class javax.annotation.**

# AndroidX Graphics Shapes (Material 3 Expressive)
# Necesario para HomeCookieShape y HomeMorphingShape
-keep class androidx.graphics.shapes.** { *; }
-keepclassmembers class androidx.graphics.shapes.** { *; }
-dontwarn androidx.graphics.shapes.**