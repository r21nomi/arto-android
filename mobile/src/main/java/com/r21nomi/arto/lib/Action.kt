package com.r21nomi.arto.lib

/**
 * Created by r21nomi on 2017/08/23.
 */
class Action<T>(
        val key: Action.Key,
        val value: T,
        val notifyStoreChanged: Boolean
) {
    interface Key
}