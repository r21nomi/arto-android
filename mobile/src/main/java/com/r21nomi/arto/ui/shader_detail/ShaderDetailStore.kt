package com.r21nomi.arto.ui.shader_detail

import com.r21nomi.arto.data.shader.entity.Shader
import com.r21nomi.arto.lib.Dispatcher
import com.r21nomi.arto.lib.Store
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * Created by r21nomi on 2017/09/06.
 */
class ShaderDetailStore @Inject constructor(dispatcher: Dispatcher) : Store(dispatcher) {

    var shader: Shader? = null

    init {
        on(ShaderDetailAction.FETCH_SHADER_DETAIL, Consumer {
            shader = it.value as Shader
        })
    }
}