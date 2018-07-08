package com.viseator.binder_service_hook;

import android.content.ClipData;
import android.os.IBinder;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wudi.viseator on 2018/7/8.
 * Wu Di
 * wudi.viseator@bytedance.com
 */
public class BinderHookHandler implements InvocationHandler {

    private Object mBase;

    public BinderHookHandler(Object obj, Class<?> stubClass) {
        try {
            Method asInterfaceMethod = stubClass.getDeclaredMethod("asInterface", IBinder.class);
            mBase = asInterfaceMethod.invoke(null, obj);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("getPrimaryClip".equals(method.getName())) {
            return ClipData.newPlainText(null, "hooked");
        }

        if ("hasPrimaryClip".equals(method.getName())) {
            return true;
        }

        return method.invoke(mBase, args);
    }
}
