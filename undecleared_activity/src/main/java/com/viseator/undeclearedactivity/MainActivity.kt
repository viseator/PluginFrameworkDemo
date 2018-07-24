package com.viseator.undeclearedactivity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.lang.reflect.Proxy

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hook()
        startActivity(Intent(this, TargetAcitivity::class.java))
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

    }
}
