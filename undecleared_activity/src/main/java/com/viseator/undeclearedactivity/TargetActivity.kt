package com.viseator.undeclearedactivity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

/**
 * Created on 2018/7/24.
 * wudi.viseator@bytedance.com
 */

class TargetActivity : AppCompatActivity() {
    init {
        Log.d("@vir", "Target Activity inited")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.target_activity_layout)
    }
}