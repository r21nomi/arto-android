package com.r21nomi.arto.model.di

import com.r21nomi.arto.data.di.DataModule
import com.r21nomi.arto.model.PreviewShaderRepository
import com.r21nomi.arto.model.ShaderRepository
import dagger.Module

/**
 * Created by r21nomi on 2017/08/24.
 */
@Module(
        includes = arrayOf(
                DataModule::class
        )
)
class ModelModule {

    /**
     * Publish to another module.
     */
    interface Provider {
        fun previewShaderRepository(): PreviewShaderRepository

        fun shaderRepository(): ShaderRepository
    }
}