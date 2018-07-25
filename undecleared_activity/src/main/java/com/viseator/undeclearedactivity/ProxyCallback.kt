package com.viseator.undeclearedactivity

import android.content.Intent
import android.os.Handler
import android.os.Message

/**
 * Created on 2018/7/24.
 * wudi.viseator@bytedance.com
 */

class ProxyCallback(private val base: Handler) : Handler.Callback {

    private var launchActivity = 0

    init {
        val clazz = Class.forName("android.app.ActivityThread\$H")
        val field = clazz.getDeclaredField("LAUNCH_ACTIVITY")
        field.isAccessible = true
        launchActivity = field.getInt(null)
    }

    override fun handleMessage(msg: Message?): Boolean {
        when (msg?.what) {
            launchActivity -> {
                val obj = msg.obj
                val intent = obj.javaClass.getDeclaredField("intent")
                intent.isAccessible = true
                val rawIntent = intent.get(obj) as Intent
                val targetIntent =
                    rawIntent.getParcelableExtra<Intent>(IActivityManagerHandler.TARGET_RAW_INTENT)
                rawIntent.component = targetIntent.component
            }
        }

        base.handleMessage(msg)
        return true
    }

}