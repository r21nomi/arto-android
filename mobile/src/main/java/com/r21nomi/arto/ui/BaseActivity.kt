package com.r21nomi.arto.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.r21nomi.arto.App
import com.r21nomi.arto.di.ApplicationComponent

/**
 * Created by r21nomi on 2017/08/23.
 */
abstract class BaseActivity<C> : AppCompatActivity() {

    val component: C by lazy {
        buildComponent()
    }

    protected abstract fun buildComponent(): C

    protected abstract fun injectDependency(component: C)

    fun getApplicationComponent(): ApplicationComponent {
        return (application as App).applicationComponent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectDependency(component)
    }
}