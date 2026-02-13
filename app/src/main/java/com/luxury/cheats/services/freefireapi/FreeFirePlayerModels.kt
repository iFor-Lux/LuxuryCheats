package com.luxury.cheats.services.freefireapi

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * Respuesta principal de la API de búsqueda de jugadores.
 */
@Keep
data class PlayerResponse(
    @SerializedName("basicinfo") val basicInfo: BasicInfo? = null,
    @SerializedName("socialinfo") val socialInfo: SocialInfo? = null,
    @SerializedName("clanbasicinfo") val clanInfo: ClanInfo? = null,
    @SerializedName("petinfo") val petInfo: PetInfo? = null,
    @SerializedName("creditscoreinfo") val creditInfo: CreditInfo? = null
)

/** Información básica y de rango del jugador. */
@Keep
data class BasicInfo(
    @SerializedName("accountid") val accountId: String? = null,
    @SerializedName("nickname") val nickname: String? = null,
    @SerializedName("region") val region: String? = null,
    @SerializedName("level") val level: Int? = null,
    @SerializedName("exp") val exp: Long? = null,
    @SerializedName("liked") val liked: Int? = null,
    @SerializedName("rank") val rank: String? = null,
    @SerializedName("rankingpoints") val rankingPoints: Int? = null,
    @SerializedName("csrank") val csRank: String? = null,
    @SerializedName("csrankingpoints") val csRankingPoints: Int? = null,
    @SerializedName("maxrank") val brMaxRank: String? = null,
    @SerializedName("csmaxrank") val csMaxRank: String? = null
)

/** Información social y de perfil. */
@Keep
data class SocialInfo(
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("language") val language: String? = null,
    @SerializedName("signature") val signature: String? = null
)

/** Información del clan al que pertenece el jugador. */
@Keep
data class ClanInfo(
    @SerializedName("clanname") val clanName: String? = null,
    @SerializedName("clanlevel") val clanLevel: Int? = null,
    @SerializedName("membernum") val memberNum: Int? = null,
    @SerializedName("capacity") val capacity: Int? = null
)

/** Información de la mascota equipada. */
@Keep
data class PetInfo(
    @SerializedName("name") val name: String? = null,
    @SerializedName("level") val level: Int? = null,
    @SerializedName("exp") val exp: Int? = null
)

/** Información del puntaje de crédito y recompensas. */
@Keep
data class CreditInfo(
    @SerializedName("creditscore") val creditScore: Int? = null,
    @SerializedName("rewardstate") val rewardState: String? = null
)
