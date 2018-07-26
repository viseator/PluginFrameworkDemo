package com.viseator.apkactivity

import dalvik.system.PathClassLoader

/**
 * Created on 2018/7/26.
 * wudi.viseator@bytedance.com
 */

class CustomClassLoader(dexPath: String, parent: ClassLoader) :
        PathClassLoader(dexPath, parent) {

}