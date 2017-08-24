package com.r21nomi.arto.ui.main

import android.util.Log
import com.r21nomi.arto.lib.ActionCreator
import com.r21nomi.arto.lib.Dispatcher
import com.r21nomi.arto.model.PreviewShaderRepository
import com.r21nomi.arto.model.ShaderRepository
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by r21nomi on 2017/08/23.
 */
class MainActionCreator @Inject constructor(
        dispatcher: Dispatcher,
        private val previewShaderRepository: PreviewShaderRepository,
        private val shaderRepository: ShaderRepository
) : ActionCreator(dispatcher) {

    companion object {
        private val LIMIT = 10
    }

    fun init() {
        previewShaderRepository
                .fetch(LIMIT)
                .doOnSuccess { dispatchSkipNotify(MainAction.PREVIEW_SHADERS, it) }
                .subscribeOn(Schedulers.io())
                .subscribe({
                    dispatch(MainAction.STORE_INITIALIZED, true)
                }, {
                    Log.e("", it.localizedMessage)
                })
    }

    fun changeShader(id: String) {
        shaderRepository.fetch(id)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    dispatch(MainAction.CHANGE_SHADER, it)
                }, {
                    Log.e("", it.localizedMessage)
                })
    }
}