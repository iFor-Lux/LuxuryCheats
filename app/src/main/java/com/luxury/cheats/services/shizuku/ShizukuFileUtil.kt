package com.luxury.cheats.services.shizuku

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShizukuFileUtil @Inject constructor(
    private val shizukuService: ShizukuService
) {

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

    suspend fun createDirectory(path: String): Boolean {
        return when (shizukuService.executeCommand("mkdir -p \"$path\"")) {
            is ShizukuService.StringResult.Success -> true
            is ShizukuService.StringResult.Error -> false
        }
    }

    suspend fun copyFile(source: String, destination: String): Boolean {
        // cp -f source destination
        return when (shizukuService.executeCommand("cp -f \"$source\" \"$destination\"")) {
            is ShizukuService.StringResult.Success -> true
            is ShizukuService.StringResult.Error -> false
        }
    }
    
    suspend fun moveFile(source: String, destination: String): Boolean {
         return when (shizukuService.executeCommand("mv -f \"$source\" \"$destination\"")) {
            is ShizukuService.StringResult.Success -> true
            is ShizukuService.StringResult.Error -> false
        }
    }

    suspend fun deleteFile(path: String, recursive: Boolean = false): Boolean {
        val flags = if (recursive) "-rf" else "-f"
        return when (shizukuService.executeCommand("rm $flags \"$path\"")) {
            is ShizukuService.StringResult.Success -> true
            is ShizukuService.StringResult.Error -> false
        }
    }
    
    suspend fun writeText(path: String, text: String): Boolean {
        // echo "text" > path
        // Escaping simple quotes for basic safety
        val escapedText = text.replace("\"", "\\\"")
        return when (shizukuService.executeCommand("echo \"$escapedText\" > \"$path\"")) {
             is ShizukuService.StringResult.Success -> true
             is ShizukuService.StringResult.Error -> false
        }
    }
    
    suspend fun readText(path: String): String {
        return when (val result = shizukuService.executeCommand("cat \"$path\"")) {
            is ShizukuService.StringResult.Success -> result.output
            is ShizukuService.StringResult.Error -> ""
        }
    }
}
