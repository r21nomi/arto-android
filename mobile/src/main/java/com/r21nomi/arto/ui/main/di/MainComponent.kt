package com.r21nomi.arto.ui.main.di

import com.r21nomi.arto.di.ApplicationComponent
import com.r21nomi.arto.ui.main.MainActivity
import dagger.Component

/**
 * Created by r21nomi on 2017/08/23.
 */
@Component(
        dependencies = arrayOf(ApplicationComponent::class),
        modules = arrayOf(
                MainModule::class
        )
)
@MainScope
interface MainComponent : MainModule.Provider {
    fun inject(mainActivity: MainActivity)
}