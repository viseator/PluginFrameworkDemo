package com.viseator.apkactivity

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.os.Handler
import android.os.Process
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.util.ArrayMap
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.button
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.lang.reflect.Proxy
import java.util.HashMap
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class MainActivity : AppCompatActivity() {
    private lateinit var newApplicationInfo: ApplicationInfo
    private val activityThreadClazz = Class.forName("android.app.ActivityThread")
    private lateinit var activityThread: Any

    companion object {
        var sLoadedApk: MutableMap<String, Any> = HashMap()
    }

    private val apkFile by lazy {
        File(cacheDir, "target.apk").copyInputStreamToFile(assets.open("target.apk"))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableLog()
        hookLoadedApk()
        hookContentProvider()
        hook()
        button.setOnClickListener {
        }
        val intent = Intent().apply {
            component = ComponentName("com.viseator.undeclearedactivity",
                    "com.viseator.undeclearedactivity.TargetActivity")
        }
        startActivity(intent)
    }

    private fun enableLog() {
        val namevalueCacheClazz = Class.forName("android.provider.Settings\$NameValueCache")
        val debugField = namevalueCacheClazz.getDeclaredField("DEBUG").apply {
            isAccessible = true
        }
        val logVField = Settings::class.java.getDeclaredField("LOCAL_LOGV").apply {
            isAccessible = true
        }
        logVField.set(null, true)
        debugField.set(null, true)
    }

    private fun hookContentProvider() {
        val providerMapField = activityThreadClazz.getDeclaredField("mProviderMap").apply {
            isAccessible = true
        }
        val contentProviderClazz = Class.forName("android.content.IContentProvider")
        val providerMap = providerMapField.get(activityThread) as ArrayMap<Any, Any>

        val providerRecordClazz = Class.forName("android.app.ActivityThread\$ProviderClientRecord")
        val mProviderField = providerRecordClazz.getDeclaredField("mProvider").apply {
            isAccessible = true
        }

        val rawContextProvider = mProviderField.get(providerMap.valueAt(0))
        val proxyContextProvider =
            Proxy.newProxyInstance(rawContextProvider::class.java.classLoader,
                    arrayOf(contentProviderClazz), IContentProviderHandler(rawContextProvider))
        mProviderField.set(providerMap.valueAt(0), proxyContextProvider)
    }

    private fun hookLoadedApk() {
        val applicationInfoClazz = Class.forName("android.content.pm.ApplicationInfo")
        // prepare generate application info
        val packageParserClazz = Class.forName("android.content.pm.PackageParser")
        val packageUserStateClazz = Class.forName("android.content.pm.PackageUserState")
        val packageClazz = Class.forName("android.content.pm.PackageParser\$Package")
        val genApplicationInfoMethod =
            packageParserClazz.getDeclaredMethod("generateApplicationInfo",
                    packageClazz, Int::class.java, packageUserStateClazz)

        // generate package
        val packageParser = packageParserClazz.newInstance()
        val parsePackageMethod =
            packageParserClazz.getDeclaredMethod("parsePackage", File::class.java, Int::class.java)
        val packageObj = parsePackageMethod.invoke(packageParser, apkFile, 0)

        // create packageUserState instance
        val packageUserState = packageUserStateClazz.newInstance()

        newApplicationInfo =
                genApplicationInfoMethod.invoke(packageParser, packageObj, 0,
                        packageUserState) as ApplicationInfo
        newApplicationInfo.sourceDir = apkFile.path
        newApplicationInfo.publicSourceDir = apkFile.path

        // get loadedApk
        val contextImplClass = Class.forName("android.app.ContextImpl")
        val activityThreadField = contextImplClass.getDeclaredField("mMainThread")
        activityThreadField.isAccessible = true
        activityThread = activityThreadField.get(this.baseContext)

        val logField = activityThreadClazz.getDeclaredField("localLOGV").apply {
            isAccessible = true
        }
        val debugMsgField = activityThreadClazz.getDeclaredField("DEBUG_MESSAGES").apply {
            isAccessible = true
        }
        logField.set(null, true)
        debugMsgField.set(null, true)

        val compatibilityInfoClass = Class.forName("android.content.res.CompatibilityInfo")
        val defaultCompatibilityInfoField =
            compatibilityInfoClass.getDeclaredField("DEFAULT_COMPATIBILITY_INFO").apply {
                isAccessible = true
            }
        val compatibilityInfo = defaultCompatibilityInfoField.get(null)

        val getPackageInfoNoCheckMethod =
            activityThreadClazz.getDeclaredMethod("getPackageInfoNoCheck", applicationInfoClazz,
                    compatibilityInfoClass)

        val loadedApk =
            getPackageInfoNoCheckMethod(activityThread, newApplicationInfo, compatibilityInfo)

        // custom classloader
        val classLoader = CustomClassLoader(apkFile.path, ClassLoader.getSystemClassLoader())
        val classLoaderField = loadedApk.javaClass.getDeclaredField("mClassLoader").apply {
            isAccessible = true
        }
        classLoaderField.set(loadedApk, classLoader)
        sLoadedApk["key"] = loadedApk

        // put into mPackages
        val mPackagesField = activityThreadClazz.getDeclaredField("mPackages").apply {
            isAccessible = true
        }

        val mPackages = mPackagesField.get(activityThread) as ArrayMap<String, WeakReference<Any>>
        val weakReference = WeakReference(loadedApk)
        mPackages[newApplicationInfo.packageName] = weakReference
        return
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
        val activityThreadClazz = Class.forName("android.app.ActivityThread")

        val hField = mainThread.javaClass.getDeclaredField("mH")
        hField.isAccessible = true
        val h = hField.get(mainThread)
        mCallbackField.set(h, ProxyCallback(h as Handler, newApplicationInfo))

        val iPMInterface = Class.forName("android.content.pm.IPackageManager")
        val sPMField = activityThreadClazz.getDeclaredField("sPackageManager").apply {
            isAccessible = true
        }
        val hookedPm = Proxy.newProxyInstance(contextImplClass.classLoader, arrayOf(iPMInterface),
                IPackageManagerHandler(this, sPMField.get(null), newApplicationInfo))
        sPMField.set(null, hookedPm)
        val pmField = contextImplClass.getDeclaredField("mPackageManager")
        pmField.isAccessible = true
        pmField.set(baseContext, null)
    }

    class Weak<T>(target: T) : WeakReference<T>(target) {
        override fun get(): T? {
            Log.d("@vir", "get!")
            return super.get()
        }
    }

    fun File.copyInputStreamToFile(inputStream: InputStream): File {
        inputStream.use { input ->
            this.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
        return this
    }
}
