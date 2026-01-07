package com.luxury.cheats.services
 
import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
 
/**
 * Servicio para operaciones con archivos locales y temporales.
 * Maneja la persistencia de imágenes de perfil y banners copiándolos al almacenamiento interno.
 */
class FileService(private val context: Context) {
 
    /**
     * Copia una imagen desde una URI externa al almacenamiento interno de la app.
     * @param uriString La URI de la imagen seleccionada.
     * @param fileName Nombre del archivo destino (ej: "profile.jpg").
     * @return La URI del archivo guardado localmente, o null si falló.
     */
    fun saveImageToInternal(uriString: String, fileName: String): String? {
        return try {
            val uri = Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            
            // Directorio interno para imágenes de usuario
            val folder = File(context.filesDir, "user_media")
            if (!folder.exists()) folder.mkdirs()
            
            // Borrar archivos viejos con el mismo prefijo para no acumular basura
            // (Opcional, pero recomendado si usamos nombres como profile_v1.jpg)
            val prefix = fileName.substringBefore(".")
            folder.listFiles()?.filter { it.name.startsWith(prefix) }?.forEach { it.delete() }
            
            // Usamos un sufijo de tiempo para evitar problemas de caché en Coil
            val finalName = "${prefix}_${System.currentTimeMillis()}.jpg"
            val file = File(folder, finalName)
            
            val outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            
            Uri.fromFile(file).toString()
        } catch (e: java.io.IOException) {
            android.util.Log.e("FileService", "Error saving image", e)
            null
        }
    }
}
