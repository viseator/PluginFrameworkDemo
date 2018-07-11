package com.viseator.amshook;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Created on 2018/7/11.
 * wudi.viseator@bytedance.com
 */
public class MainActivity extends Activity {
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test();
    }

    private void test() {
        try {
            Class<?> amsNativeClass = Class.forName("android.app.ActivityManager");
            Field gDefaultField = amsNativeClass.getDeclaredField("IActivityManagerSingleton");
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);

            Class<?> singleton = Class.forName("android.util.Singleton");
            Field mInstanceField = singleton.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object rawIActivityManager = mInstanceField.get(gDefault);
            Class<?> iAMInterface = Class.forName("android.app.IActivityManager");
            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{ iAMInterface }, new IActivityManagerHandler(rawIActivityManager));
            mInstanceField.set(gDefault, proxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
