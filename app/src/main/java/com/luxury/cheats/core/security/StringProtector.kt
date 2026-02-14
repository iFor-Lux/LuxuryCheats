package com.luxury.cheats.core.security

import com.luxury.cheats.BuildConfig

/**
 * Utilidad para desencriptar cadenas de texto protegidas mediante XOR.
 */
object StringProtector {
    // Clave determinística:
    // - Funciona en todos los dispositivos
    // - No depende de valores runtime variables
    private fun deriveKey(seed: Int): Int {
        return seed xor BuildConfig.VERSION_CODE
    }

    private const val HEX_RADIX = 16

    /**
     * Desencripta strings codificados como \\uXXXX utilizando una semilla y el código de versión.
     *
     * @param data La cadena de texto codificada.
     * @param seed La semilla para derivar la clave XOR.
     * @return La cadena de texto desencriptada.
     */
    fun decrypt(
        data: String,
        seed: Int,
    ): String {
        val key = deriveKey(seed)

        return data
            .split("\\u")
            .filter { it.isNotEmpty() }
            .map { it.toInt(HEX_RADIX) xor key }
            .map { it.toChar() }
            .joinToString("")
    }
}
