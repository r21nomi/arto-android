package com.r21nomi.arto.ui.main.di

import com.r21nomi.arto.ui.main.MainActionCreator
import com.r21nomi.arto.ui.main.MainStore
import dagger.Module

/**
 * Created by r21nomi on 2017/08/23.
 */
@Module
class MainModule {

    interface Provider {
        fun mainActionCreator(): MainActionCreator

        fun mainStore(): MainStore
    }
}