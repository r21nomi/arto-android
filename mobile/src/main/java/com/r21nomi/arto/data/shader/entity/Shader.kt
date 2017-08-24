package com.r21nomi.arto.data.shader.entity

import com.squareup.moshi.Json

/**
 * Created by r21nomi on 2017/08/23.
 */
data class Shader(
        @Json(name = "ver") val version: String,
        @Json(name = "info") val info: Info,
        @Json(name = "renderpass") val renderpass: List<ShaderProgram>
)