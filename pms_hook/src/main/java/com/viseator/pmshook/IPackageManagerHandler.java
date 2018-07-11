package com.viseator.pmshook;

import android.util.Log;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created on 2018/7/11.
 * wudi.viseator@bytedance.com
 */
public class IPackageManagerHandler implements InvocationHandler {
    private static final String TAG = "@vir";
    private Object mBase;
    public IPackageManagerHandler(Object base) {
        mBase = base;
    }

    @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d(TAG, "hhhhhooooooked");
        Log.d(TAG, method.getName());
        return method.invoke(mBase, args);
    }
}
