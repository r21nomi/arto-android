package com.r21nomi.arto.ui.shader_detail.di

import com.r21nomi.arto.ui.shader_detail.ShaderDetailActionCreator
import com.r21nomi.arto.ui.shader_detail.ShaderDetailStore
import dagger.Module

/**
 * Created by r21nomi on 2017/09/06.
 */
@Module
class ShaderDetailModule {

    interface Provider {
        fun shaderDetailActionCreator(): ShaderDetailActionCreator

        fun shaderDetailStore(): ShaderDetailStore
    }
}