package com.r21nomi.arto.di

import com.r21nomi.arto.lib.Dispatcher
import com.r21nomi.arto.model.di.ModelModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by r21nomi on 2017/08/23.
 */
@Singleton
@Component(
        modules = arrayOf(
                ApplicationModule::class,
                ModelModule::class
        )
)
interface ApplicationComponent : ModelModule.Provider {
    fun dispatcher(): Dispatcher
}