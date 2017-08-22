package com.r21nomi.arto.lib

/**
 * Created by r21nomi on 2017/08/23.
 */
abstract class ActionCreator(private val dispatcher: Dispatcher) {

    protected fun dispatch(key: Action.Key, value: Any) {
        dispatcher.dispatch(Action(key, value, true))
    }

    protected fun dispatchSkipNotify(key: Action.Key, value: Any) {
        dispatcher.dispatch(Action(key, value, false))
    }
}