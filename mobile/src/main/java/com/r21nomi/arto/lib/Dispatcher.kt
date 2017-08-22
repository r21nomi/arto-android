package com.r21nomi.arto.lib

import io.reactivex.Flowable
import io.reactivex.functions.Consumer
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.FlowableProcessor

/**
 * Created by r21nomi on 2017/08/23.
 */
class Dispatcher {

    private val dispatcherProcessor: FlowableProcessor<Action<Any>>
            = BehaviorProcessor.create<Action<Any>>().toSerialized()

    fun on(key: Action.Key, consumer: Consumer<Action<Any>>): Flowable<Action<Any>> {
        return dispatcherProcessor
                .filter { action -> action.key == key }
                .doOnNext(consumer)
    }

    fun dispatch(action: Action<Any>) {
        dispatcherProcessor.onNext(action)
    }
}