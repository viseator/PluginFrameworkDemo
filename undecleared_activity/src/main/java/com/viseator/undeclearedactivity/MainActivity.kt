package com.viseator.undeclearedactivity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import java.lang.reflect.Proxy

class MainActivity : AppCompatActivity() {

    private var launchActivity = 0

    init {
        val clazz = Class.forName("android.app.ActivityThread\$H")
        val field = clazz.getDeclaredField("LAUNCH_ACTIVITY")
        field.isAccessible = true
        launchActivity = field.getInt(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hook()

        startActivity(Intent(this, TargetActivity::class.java))
    }

    private fun hook() {
        val amsNativeClass = Class.forName("android.app.ActivityManager")
        val gDefaultField = amsNativeClass.getDeclaredField("IActivityManagerSingleton")
        gDefaultField.isAccessible = true
        val gDefault = gDefaultField.get(null)

        val singleton = Class.forName("android.util.Singleton")
        val mInstanceField = singleton.getDeclaredField("mInstance")
        mInstanceField.isAccessible = true
        val rawIActivityManager = mInstanceField.get(gDefault)
        val iAMInterface = Class.forName("android.app.IActivityManager")
        val proxy = Proxy.newProxyInstance(Thread.currentThread().contextClassLoader,
                arrayOf(iAMInterface), IActivityManagerHandler(rawIActivityManager))
        mInstanceField.set(gDefault, proxy)

        val hClazz = Class.forName("android.os.Handler")
        val mCallbackField = hClazz.getDeclaredField("mCallback")
        mCallbackField.isAccessible = true
        val contextImplClass = Class.forName("android.app.ContextImpl")
        val activityThreadField = contextImplClass.getDeclaredField("mMainThread")
        activityThreadField.isAccessible = true
        val mainThread = activityThreadField.get(this.baseContext)
        val hField = mainThread.javaClass.getDeclaredField("mH")
        hField.isAccessible = true
        val h = hField.get(mainThread)
        mCallbackField.set(h, ProxyCallback(h as Handler))

        val iPMInterface = Class.forName("android.content.pm.IPackageManager")
        val activityThreadClazz = Class.forName("android.app.ActivityThread")
        val sPMField = activityThreadClazz.getDeclaredField("sPackageManager").apply {
            isAccessible = true
        }
        val hookedPm = Proxy.newProxyInstance(contextImplClass.classLoader, arrayOf(iPMInterface),
                IPackageManagerHandler(this, sPMField.get(null)))
        sPMField.set(null, hookedPm)
        val pmField = contextImplClass.getDeclaredField("mPackageManager")
        pmField.isAccessible = true
        pmField.set(baseContext, null)
    }
}
