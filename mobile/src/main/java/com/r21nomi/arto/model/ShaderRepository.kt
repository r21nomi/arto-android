package com.r21nomi.arto.model

import com.r21nomi.arto.BuildConfig
import com.r21nomi.arto.data.shader.ShaderApi
import com.r21nomi.arto.data.shader.entity.Shader
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by r21nomi on 2017/08/25.
 */
@Singleton
class ShaderRepository @Inject constructor(private val shaderApi: ShaderApi) {

    fun fetch(id: String): Single<Shader> {
        return shaderApi.getShader(id, BuildConfig.SHADERTOY_API_KEY)
                .subscribeOn(Schedulers.io())
    }
}