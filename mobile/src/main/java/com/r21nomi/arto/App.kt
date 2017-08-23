package com.r21nomi.arto

import android.app.Application
import com.r21nomi.arto.di.ApplicationComponent
import com.r21nomi.arto.di.ApplicationModule
import com.r21nomi.arto.di.DaggerApplicationComponent
import kotlin.properties.Delegates

/**
 * Created by r21nomi on 2017/08/23.
 */
class App : Application() {

    var applicationComponent: ApplicationComponent by Delegates.notNull<ApplicationComponent>()

    override fun onCreate() {
        super.onCreate()

        initInjector()
    }

    private fun initInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }
}