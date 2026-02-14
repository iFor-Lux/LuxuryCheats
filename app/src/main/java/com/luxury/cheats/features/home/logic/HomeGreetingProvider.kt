package com.luxury.cheats.features.home.logic

/**
 * Proveedor de lógica para saludos y formateo de datos del perfil del jugador.
 */
object HomeGreetingProvider {
    private object Constants {
        const val NEW_YEAR_MONTH = 1
        const val NEW_YEAR_DAY = 1
        const val YEAR_END_MONTH = 12
        const val YEAR_END_DAY = 31
        const val VALENTINE_MONTH = 2
        const val VALENTINE_DAY = 14
        const val HALLOWEEN_MONTH = 10
        const val HALLOWEEN_START = 25
        const val HALLOWEEN_END = 31
        const val XMAS_MONTH = 12
        const val XMAS_START = 20
        const val XMAS_END = 30

        const val MORNING_START = 6
        const val MORNING_END = 11
        const val AFTERNOON_START = 12
        const val AFTERNOON_END = 18
    }

    /**
     * Retorna un par (saludo, subtítulo) basado en la hora y fecha actual.
     */
    fun getGreetingForTimeAndDate(
        hour: Int,
        month: Int,
        day: Int,
    ): Pair<String, String> {
        return when {
            month == Constants.NEW_YEAR_MONTH && day == Constants.NEW_YEAR_DAY ||
                month == Constants.YEAR_END_MONTH && day == Constants.YEAR_END_DAY ->
                "FELIZ AÑO NUEVO" to "¡Qué este año 2027 esté lleno de victorias incomparables!"
            month == Constants.VALENTINE_MONTH && day == Constants.VALENTINE_DAY ->
                "FELIZ SAN VALENTÍN" to "¡Hoy es un gran día para compartir victorias con alguien especial!"
            month == Constants.HALLOWEEN_MONTH &&
                day in Constants.HALLOWEEN_START..Constants.HALLOWEEN_END ->
                "¡FELIZ HALLOWEEN!" to "¡Una noche de trucos, tratos y muchas victorias de miedo!"
            month == Constants.XMAS_MONTH && day in Constants.XMAS_START..Constants.XMAS_END ->
                "FELIZ NAVIDAD" to "¡Te deseamos una muy feliz navidad y feliz juego con Luxury!"
            else -> getGreetingByHour(hour)
        }
    }

    private fun getGreetingByHour(hour: Int): Pair<String, String> {
        return when (hour) {
            in Constants.MORNING_START..Constants.MORNING_END ->
                "BUENOS DIAS" to "Gran día para empezar a jugar y divertirse todo el día"
            in Constants.AFTERNOON_START..Constants.AFTERNOON_END ->
                "BUENAS TARDES" to "Tarde perfecta para unas partidas legendarias"
            else -> "BUENAS NOCHES" to "La noche es joven para seguir ganando en cada partida"
        }
    }

    /**
     * Formatea los datos de un jugador en un string para la consola.
     */
    fun formatPlayerData(data: com.luxury.cheats.services.freefireapi.PlayerResponse): String {
        val basic = data.basicInfo ?: return "ERROR EN PROTOCOLO DE DATOS: BASIC INFO NULL."
        val sb = StringBuilder()

        sb.append("🔥 SEGURIDAD LUXURY ACTIVADO 🔥\n")
        sb.append("---------------------------------\n")

        appendSectionProfile(sb, basic)
        sb.append("---------------------------------\n\n")

        appendSectionRank(sb, basic)
        sb.append("---------------------------------\n\n")

        data.clanInfo?.let { appendSectionClan(sb, it) }
        data.socialInfo?.let { appendSectionSocial(sb, it) }

        return sb.toString()
    }

    private fun appendSectionProfile(
        sb: StringBuilder,
        basic: com.luxury.cheats.services.freefireapi.BasicInfo,
    ) {
        sb.append("👤 PERFIL\n")
        sb.append("• UID      : ${basic.accountId ?: "N/A"}\n")
        sb.append("• NICKNAME : ${basic.nickname ?: "N/A"}\n")
        sb.append("• REGIÓN   : ${basic.region ?: "N/A"}\n")

        val levelStr = basic.level?.toString() ?: "N/A"
        val expStr = formatExp(basic.exp)

        sb.append("• NIVEL    : $levelStr (EXP: $expStr)\n")
        sb.append("• LIKES    : ${basic.liked ?: 0}\n\n")
    }

    private fun formatExp(exp: Long?): String {
        if (exp == null) return "0"
        return try {
            String.format(java.util.Locale.US, "%,d", exp)
        } catch (e: java.util.IllegalFormatException) {
            android.util.Log.e("HomeGreetingProvider", "Error formatting EXP: ${e.message}")
            exp.toString()
        }
    }

    private fun appendSectionRank(
        sb: StringBuilder,
        basic: com.luxury.cheats.services.freefireapi.BasicInfo,
    ) {
        sb.append("🏆 RANGO\n")
        sb.append("• BR RANK  : ${basic.rank ?: "N/A"} (${basic.rankingPoints ?: 0} pts)\n")
        sb.append("• CS RANK  : ${basic.csRank ?: "N/A"} (${basic.csRankingPoints ?: 0} pts)\n")
        sb.append("• MAX RANK : BR: ${basic.brMaxRank ?: "N/A"} | CS: ${basic.csMaxRank ?: "N/A"}\n\n")
    }

    private fun appendSectionClan(
        sb: StringBuilder,
        clan: com.luxury.cheats.services.freefireapi.ClanInfo,
    ) {
        if (clan.clanName != null) {
            sb.append("🛡️ CLAN\n")
            sb.append("• NOMBRE   : ${clan.clanName}\n")
            sb.append("• NIVEL    : ${clan.clanLevel ?: 0}\n")
            sb.append("• MIEMBROS : ${clan.memberNum ?: 0}/${clan.capacity ?: 0}\n\n")
            sb.append("---------------------------------\n\n")
        }
    }

    private fun appendSectionSocial(
        sb: StringBuilder,
        social: com.luxury.cheats.services.freefireapi.SocialInfo,
    ) {
        sb.append("🌐 SOCIAL\n")
        sb.append("• FIRMA    : ${social.signature ?: "Sin firma"}\n")
    }
}
