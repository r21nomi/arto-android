package com.r21nomi.arto.data.shader

import com.r21nomi.arto.data.shader.entity.Shader
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by r21nomi on 2017/08/23.
 */
interface ShaderApi {

    @GET("/api/v1/shaders/{shaderId}")
    fun getShader(
            @Path("shaderId") shaderId: String,
            @Query("key") key: String
    ): Single<Shader>
}