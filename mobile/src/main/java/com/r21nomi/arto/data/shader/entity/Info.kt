package com.r21nomi.arto.data.shader.entity

import com.squareup.moshi.Json

/**
 * Created by r21nomi on 2017/08/23.
 */
data class Info(
        @Json(name = "id") val id: String,
        @Json(name = "date") val date: String,
        @Json(name = "viewed") val viewed: Int,
        @Json(name = "name") val name: String,
        @Json(name = "username") val userName: String,
        @Json(name = "description") val description: String,
        @Json(name = "likes") val likes: Int,
        @Json(name = "published") val published: Int,
        @Json(name = "flags") val flags: Int,
        @Json(name = "hasliked") val hasLiked: Int,
        @Json(name = "tags") val tags: List<String>
)