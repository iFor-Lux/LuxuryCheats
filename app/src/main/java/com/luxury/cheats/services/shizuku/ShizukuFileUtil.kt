package com.luxury.cheats.services.shizuku

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utilidad para realizar operaciones de archivos (ls, mkdir, cp, mv, rm, echo, cat)
 * utilizando privilegios de Shizuku/Root.
 */
@Singleton
class ShizukuFileUtil
    @Inject
    constructor(
        private val shizukuService: ShizukuService,
    ) {
        /**
         * Lista los archivos y directorios en la ruta especificada.
         * @param path Ruta del directorio a listar.
         * @return Lista de nombres de archivos/directorios.
         */
        suspend fun listFiles(path: String): List<String> {
            return when (val result = shizukuService.executeCommand("ls \"$path\"")) {
                is ShizukuService.StringResult.Success -> {
                    result.output.split("\n").filter { it.isNotEmpty() }
                }
                is ShizukuService.StringResult.Error -> {
                    emptyList()
                }
            }
        }

        /**
         * Crea un directorio de forma recursiva (mkdir -p).
         * @param path Ruta del directorio a crear.
         * @return true si la operación fue exitosa.
         */
        suspend fun createDirectory(path: String): Boolean {
            return when (shizukuService.executeCommand("mkdir -p \"$path\"")) {
                is ShizukuService.StringResult.Success -> true
                is ShizukuService.StringResult.Error -> false
            }
        }

        /**
         * Copia un archivo forzando el reemplazo (cp -f).
         * @param source Ruta de origen.
         * @param destination Ruta de destino.
         * @return true si la operación fue exitosa.
         */
        suspend fun copyFile(
            source: String,
            destination: String,
        ): Boolean {
            // cp -f source destination
            return when (shizukuService.executeCommand("cp -f \"$source\" \"$destination\"")) {
                is ShizukuService.StringResult.Success -> true
                is ShizukuService.StringResult.Error -> false
            }
        }

        /**
         * Mueve o renombra un archivo forzando el reemplazo (mv -f).
         * @param source Ruta de origen.
         * @param destination Ruta de destino.
         * @return true si la operación fue exitosa.
         */
        suspend fun moveFile(
            source: String,
            destination: String,
        ): Boolean {
            return when (shizukuService.executeCommand("mv -f \"$source\" \"$destination\"")) {
                is ShizukuService.StringResult.Success -> true
                is ShizukuService.StringResult.Error -> false
            }
        }

        /**
         * Elimina un archivo o directorio (rm).
         * @param path Ruta a eliminar.
         * @param recursive Si es true, elimina directorios y su contenido (-r).
         * @return true si la operación fue exitosa.
         */
        suspend fun deleteFile(
            path: String,
            recursive: Boolean = false,
        ): Boolean {
            val flags = if (recursive) "-rf" else "-f"
            return when (shizukuService.executeCommand("rm $flags \"$path\"")) {
                is ShizukuService.StringResult.Success -> true
                is ShizukuService.StringResult.Error -> false
            }
        }

        /**
         * Escribe texto en un archivo, sobrescribiendo su contenido.
         * @param path Ruta del archivo.
         * @param text Contenido a escribir.
         * @return true si la operación fue exitosa.
         */
        suspend fun writeText(
            path: String,
            text: String,
        ): Boolean {
            // echo "text" > path
            // Escaping simple quotes for basic safety
            val escapedText = text.replace("\"", "\\\"")
            return when (shizukuService.executeCommand("echo \"$escapedText\" > \"$path\"")) {
                is ShizukuService.StringResult.Success -> true
                is ShizukuService.StringResult.Error -> false
            }
        }

        /**
         * Lee el contenido de texto de un archivo (cat).
         * @param path Ruta del archivo.
         * @return Contenido del archivo o cadena vacía si falla.
         */
        suspend fun readText(path: String): String {
            return when (val result = shizukuService.executeCommand("cat \"$path\"")) {
                is ShizukuService.StringResult.Success -> result.output
                is ShizukuService.StringResult.Error -> ""
            }
        }
    }
