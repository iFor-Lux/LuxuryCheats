# Optimization
-optimizationpasses 5
-dontpreverify
# Deshabilitamos repackageclasses ya que puede romper la reflexión de Retrofit/Gson
# -repackageclasses ''
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

# Retrofit / OkHttp
-keepattributes Signature, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault, EnclosingMethod, InnerClasses
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# FreeFire API - Crucial para que funcione en Release
-keep interface com.luxury.cheats.services.freefireapi.FreeFireApiService { *; }
-keep class com.luxury.cheats.services.freefireapi.** { *; }
-keepclassmembers class com.luxury.cheats.services.freefireapi.** { *; }

# Gson
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

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
-keep class androidx.graphics.shapes.** { *; }
-keepclassmembers class androidx.graphics.shapes.** { *; }
-dontwarn androidx.graphics.shapes.**