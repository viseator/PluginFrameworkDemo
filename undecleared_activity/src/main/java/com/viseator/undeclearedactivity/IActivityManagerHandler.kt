package com.viseator.undeclearedactivity

import android.content.ComponentName
import android.content.Intent
import android.util.Log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * Created on 2018/7/11.
 * wudi.viseator@bytedance.com
 */
class IActivityManagerHandler(private val mBase: Any) : InvocationHandler {

    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any? {
        when (method.name) {
            "startActivity" -> {
                var index = 0
                var rawIntent: Intent? = null
                for (arg in args) {
                    if (arg is Intent) {
                        rawIntent = arg
                        index = args.indexOf(arg)
                        break
                    }
                }
                val newIntent = Intent()
                val componentName =
                    ComponentName(TARGET_PACKAGE, StubActivity::class.java.canonicalName)
                newIntent.component = componentName
                newIntent.putExtra(TARGET_RAW_INTENT, rawIntent)
                args[index] = newIntent
                Log.d(TAG, "hooked intent send success")
                return method.invoke(mBase, *args)
            }
        }
        return method.invoke(mBase, *args)
    }

    companion object {
        private val TAG = "@vir"
        private const val TARGET_PACKAGE = "com.viseator.undeclearedactivity"
        private const val TARGET_RAW_INTENT = "intent"
    }
}
