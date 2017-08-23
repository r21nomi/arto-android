package com.r21nomi.arto.data.shaderResponse.entity

/**
 * Created by r21nomi on 2017/08/24.
 */
data class PreviewShader(
        val id: String
) {
    fun getUrl(): String {
        return "https://www.shadertoy.com/media/shaders/$id.jpg"
    }
}