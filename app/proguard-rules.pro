# Mantiene información mínima para poder leer stacktraces en producción
# sin exponer nombres reales de archivos fuente
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile


# Elimina logs de desarrollo para mejorar seguridad y reducir tamaño
# Log.v, Log.d y Log.i no se incluyen en el APK final
# Log.w y Log.e permanecen activos
-assumenosideeffects class android.util.Log {
    public static * v(...);
    public static * d(...);
    public static * i(...);
}


# Mantiene metadata de Kotlin, necesaria para reflection, Compose y serialización
-keep class kotlin.Metadata { *; }

# Evita warnings internos del runtime de Kotlin
-dontwarn kotlin.**


# Jetpack Compose es compatible con R8
# Solo se silencian warnings innecesarios
-dontwarn androidx.compose.**
-dontwarn androidx.compose.runtime.**


# Reglas necesarias para Kotlinx Serialization
-keepclassmembers class kotlinx.serialization.** {
    *** Companion;
}

-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}


# Reglas comunes para Retrofit y OkHttp
# No mantiene clases completas, solo evita warnings
-dontwarn okhttp3.**
-dontwarn retrofit2.**

# Mantiene las interfaces que definen endpoints HTTP
-keep interface retrofit2.http.** { *; }


# Reglas para Firebase
# Firebase ya incluye sus propias reglas de optimización
# No se debe usar -keep para permitir que R8 elimine código no utilizado
-dontwarn com.google.firebase.**


# Optimización segura del bytecode
-optimizationpasses 5
-allowaccessmodification


# Evita warnings de anotaciones no usadas en runtime
-dontwarn javax.annotation.**
