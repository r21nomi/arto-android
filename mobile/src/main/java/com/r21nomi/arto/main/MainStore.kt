package com.r21nomi.arto.main

import com.r21nomi.arto.lib.Dispatcher
import com.r21nomi.arto.lib.Store
import io.reactivex.functions.Consumer


/**
 * Created by r21nomi on 2017/08/23.
 */
class MainStore(dispatcher: Dispatcher) : Store(dispatcher) {

    var shaderList: MutableList<String> = mutableListOf()
    var shader = ""
    var initialized = false

    init {
        on(MainAction.SHADER, Consumer {
            shaderList.addAll(it.value as MutableList<String>)
        })

        on(MainAction.STORE_INITIALIZED, Consumer {
            initialized = it.value as Boolean
        })

        on(MainAction.CHANGE_SHADER, Consumer {
            shader = it.value as String
        })
    }
}