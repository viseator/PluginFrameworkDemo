package com.viseator.undeclearedactivity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.util.Log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * Created on 2018/7/25.
 * wudi.viseator@bytedance.com
 */

class IPackageManagerHandler(private val context: Context, private val base: Any) :
        InvocationHandler {

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        Log.d("@vir", "hooked pm: ${method?.name}")
        when (method?.name) {
            "getActivityInfo" -> {
                val intent = Intent(context, StubActivity::class.java)
                return method.invoke(base, intent.component, args!![1], args[2]) as ActivityInfo
            }
        }
        return method?.invoke(base, *args ?: arrayOf())
    }

}