package com.r21nomi.arto.data.shader.entity

import com.squareup.moshi.Json

/**
 * Created by r21nomi on 2017/08/23.
 */
data class ShaderProgram(
        @Json(name = "code") val code: String,
        @Json(name = "name") val name: String,
        @Json(name = "description") val description: String,
        @Json(name = "type") val type: String
)