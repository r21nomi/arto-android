package com.r21nomi.arto.data.shader

import com.r21nomi.arto.data.shaderResponse.entity.ShaderResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by r21nomi on 2017/08/23.
 */
interface ShaderResponseApi {

    @GET("/api/v1/shaders")
    fun getShaderResponse(
            @Query("key") key: String,
            @Query("num") num: Int
    ): Single<ShaderResponse>
}