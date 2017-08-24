package com.r21nomi.arto.model

import com.r21nomi.arto.BuildConfig
import com.r21nomi.arto.data.shader.ShaderResponseApi
import com.r21nomi.arto.data.shaderResponse.entity.PreviewShader
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by r21nomi on 2017/08/23.
 */
@Singleton
class PreviewShaderRepository @Inject constructor(private val shaderResponseApi: ShaderResponseApi) {

    fun fetch(limit: Int): Single<List<PreviewShader>> {
        return shaderResponseApi.getShaderResponse(BuildConfig.SHADERTOY_API_KEY, limit)
                .map { it.shaderIds.map { PreviewShader(it) } }
                .subscribeOn(Schedulers.io())
    }
}