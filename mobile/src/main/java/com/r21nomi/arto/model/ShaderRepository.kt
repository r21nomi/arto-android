package com.r21nomi.arto.model

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Created by r21nomi on 2017/08/23.
 */
class ShaderRepository {

    fun getShader(): Single<MutableList<String>> {
        return Single.just(mutableListOf(
                "this is an awesome shader!!",
                "this is a next shader!!"
        ))
                .subscribeOn(Schedulers.io())
    }
}