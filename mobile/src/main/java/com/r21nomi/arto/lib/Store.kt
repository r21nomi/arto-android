package com.r21nomi.arto.lib

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.FlowableProcessor
import io.reactivex.schedulers.Schedulers

/**
 * Created by r21nomi on 2017/08/23.
 */
abstract class Store(private val dispatcher: Dispatcher) {

    private val storeProcessor: FlowableProcessor<Action<Any>>
            = BehaviorProcessor.create<Action<Any>>().toSerialized()

    private val cd: CompositeDisposable = CompositeDisposable()

    fun observeOnMainThread(consumer: Consumer<Action<Any>>) {
        observe(AndroidSchedulers.mainThread(), consumer)
    }

    fun observeOnBackgroundThread(consumer: Consumer<Action<Any>>) {
        observe(Schedulers.io(), consumer)
    }

    fun notifyStoreChanged(action: Action<Any>) {
        storeProcessor.onNext(action)
    }

    protected fun on(key: Action.Key, consumer: Consumer<Action<Any>>) {
        dispatcher.on(key, consumer)
                .subscribeOn(Schedulers.io())
                .subscribe { action ->
                    if (action.notifyStoreChanged) {
                        notifyStoreChanged(action)
                    }
                }
    }

    private fun observe(scheduler: Scheduler, consumer: Consumer<Action<Any>>) {
        storeProcessor
                .subscribeOn(Schedulers.io())
                .observeOn(scheduler)
                .subscribe(consumer)
                .run {
                    cd.add(this)
                }
    }
}