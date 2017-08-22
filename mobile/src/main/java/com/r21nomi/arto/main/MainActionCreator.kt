package com.r21nomi.arto.main

import android.util.Log
import com.r21nomi.arto.lib.ActionCreator
import com.r21nomi.arto.lib.Dispatcher
import com.r21nomi.arto.model.ShaderRepository
import io.reactivex.schedulers.Schedulers

/**
 * Created by r21nomi on 2017/08/23.
 */
class MainActionCreator(dispatcher: Dispatcher) : ActionCreator(dispatcher) {

    private val shaderRepository: ShaderRepository = ShaderRepository()

    fun init() {
        shaderRepository
                .getShader()
                .doOnSuccess { dispatchSkipNotify(MainAction.SHADER, it) }
                .subscribeOn(Schedulers.io())
                .subscribe({
                    dispatch(MainAction.STORE_INITIALIZED, true)
                }, {
                    Log.e("", it.localizedMessage)
                })
    }

    fun changeShader(shader: String) {
        dispatch(MainAction.CHANGE_SHADER, shader)
    }
}