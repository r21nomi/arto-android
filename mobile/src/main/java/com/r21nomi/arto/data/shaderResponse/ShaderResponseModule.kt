package com.r21nomi.arto.data.shaderResponse

import com.r21nomi.arto.data.shader.ShaderResponseApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by r21nomi on 2017/08/24.
 */
@Module
class ShaderResponseModule {

    @Provides
    @Singleton
    fun provideShaderResponseApi(retrofit: Retrofit): ShaderResponseApi {
        return retrofit.create(ShaderResponseApi::class.java)
    }
}