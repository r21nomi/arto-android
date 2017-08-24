package com.r21nomi.arto.data.shader

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by r21nomi on 2017/08/25.
 */
@Module
class ShaderModule {
    @Provides
    @Singleton
    fun provideShaderApi(retrofit: Retrofit): ShaderApi {
        return retrofit.create(ShaderApi::class.java)
    }
}