package com.viseator.binder_service_hook;

import android.os.IBinder;
import android.os.IInterface;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by wudi.viseator on 2018/7/8.
 * Wu Di
 * wudi.viseator@bytedance.com
 */
public class BinderProxyHookHandler implements InvocationHandler {

    private IBinder mBase;
    private Class<?> stub;
    private Class<?> iInterface;

    public BinderProxyHookHandler(IBinder base) {
        mBase = base;
        try {
            this.stub = Class.forName("android.content.IClipboard$Stub");
            this.iInterface = Class.forName("android.content.IClipboard");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("queryLocalInterface".equals(method.getName())) {
            return Proxy.newProxyInstance(proxy.getClass().getClassLoader(),
                    new Class[] { IBinder.class, IInterface.class, iInterface },
                    new BinderHookHandler(mBase, stub));
        }
        return method.invoke(mBase, args);
    }
}
