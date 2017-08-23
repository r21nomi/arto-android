package com.r21nomi.arto.data.shaderResponse.entity

import com.squareup.moshi.Json

/**
 * Created by r21nomi on 2017/08/23.
 */
data class ShaderResponse(
        @Json(name = "Shaders") val shadersCount: Int,
        @Json(name = "Results") val shaderIds: List<String>
)