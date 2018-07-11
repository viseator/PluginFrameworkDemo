package com.viseator.amshook;

import android.util.Log;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created on 2018/7/11.
 * wudi.viseator@bytedance.com
 */
public class IActivityManagerHandler implements InvocationHandler {
    private static final String TAG = "@vir ";
    private Object mBase;

    public IActivityManagerHandler(Object base) {
        this.mBase = base;
    }

    @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.d(TAG, "hhhhhooooooked");
        Log.d(TAG, method.getName() + " " + args.toString());
        return method.invoke(mBase, args);
    }
}
