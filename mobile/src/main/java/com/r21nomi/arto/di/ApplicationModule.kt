package com.r21nomi.arto.di

import android.content.Context
import com.r21nomi.arto.App
import com.r21nomi.arto.lib.Dispatcher
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by r21nomi on 2017/08/23.
 */
@Module
class ApplicationModule(private val app: App) {

    @Provides
    fun provideApp(): App {
        return app
    }

    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return app.applicationContext
    }

    @Provides
    @Singleton
    fun provideDispatcher(): Dispatcher {
        return Dispatcher()
    }
}