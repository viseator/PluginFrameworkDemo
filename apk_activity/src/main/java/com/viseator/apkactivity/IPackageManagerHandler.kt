package com.viseator.apkactivity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.util.Log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * Created on 2018/7/25.
 * wudi.viseator@bytedance.com
 */

class IPackageManagerHandler(private val context: Context, private val base: Any, private val applicationInfo: ApplicationInfo) :
        InvocationHandler {

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        Log.d("@vir", "hooked pm: ${method?.name}")
        when (method?.name) {
            "getActivityInfo" -> {
                val intent = Intent(context, StubActivity::class.java)
                return method.invoke(base, intent.component, args!![1], args[2]) as ActivityInfo
            }
            "getPackageInfo" -> {
                val packageInfo =
                    method.invoke(base, "com.viseator.apkactivity", args!![1], args[2]) as PackageInfo
                packageInfo.applicationInfo = applicationInfo
                return packageInfo
            }
        }
        return method?.invoke(base, *args ?: arrayOf())
    }

}