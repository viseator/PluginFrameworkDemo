package com.viseator.apkactivity

import android.util.Log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * Created on 2018/7/27.
 * wudi.viseator@bytedance.com
 */

class IContentProviderHandler(private val base: Any) : InvocationHandler {

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        Log.d("@vir", "proxy ContextProvider: ${method?.name}")
        when (method?.name) {
            "call" -> {
                //     public Bundle call(
                //            String callingPkg, String method, @Nullable String arg, @Nullable Bundle extras)
                args?.let {
                    Log.d("@vir",
                            "callingPkg:${it[0]} method: ${it[1]} arg: ${it[2]} extras:${it[3]}")
                }
                return method.invoke(base, "com.viseator.apkactivity", args!![1], args[2], args[3])
            }
        }
        return method?.invoke(base, *args ?: arrayOf())
    }

}