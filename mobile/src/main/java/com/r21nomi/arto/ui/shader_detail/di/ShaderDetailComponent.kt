package com.r21nomi.arto.ui.shader_detail.di

import com.r21nomi.arto.di.ApplicationComponent
import com.r21nomi.arto.ui.shader_detail.ShaderDetailActivity
import dagger.Component

/**
 * Created by r21nomi on 2017/09/06.
 */
@Component(
        dependencies = arrayOf(ApplicationComponent::class),
        modules = arrayOf(
                ShaderDetailModule::class
        )
)
@ShaderDetailScope
interface ShaderDetailComponent : ShaderDetailModule.Provider {
    fun inject(shaderDetailActivity: ShaderDetailActivity)
}