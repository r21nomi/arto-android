package com.r21nomi.arto.ui.shader_detail

import android.util.Log
import com.r21nomi.arto.lib.ActionCreator
import com.r21nomi.arto.lib.Dispatcher
import com.r21nomi.arto.model.ShaderRepository
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by r21nomi on 2017/09/06.
 */
class ShaderDetailActionCreator @Inject constructor(
        dispatcher: Dispatcher,
        private val shaderRepository: ShaderRepository
) : ActionCreator(dispatcher) {

    fun fetchShaderDetail(id: String) {
        shaderRepository.fetch(id)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    dispatch(ShaderDetailAction.FETCH_SHADER_DETAIL, it)
                }, {
                    Log.e("", it.localizedMessage)
                })
    }
}