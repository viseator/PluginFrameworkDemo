package com.viseator.binder_service_hook;

import android.content.ClipboardManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hookSystemService();
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ((TextView) findViewById(R.id.text)).setText(
                clipboardManager.getPrimaryClip().getItemAt(0).getText());
    }

    public void hookSystemService() {
        try {
            Class<?> serviceManagerClass = Class.forName("android.os.ServiceManager");
            Method getServiceMethod =
                    serviceManagerClass.getDeclaredMethod("getService", String.class);
            IBinder rawBinder = (IBinder) getServiceMethod.invoke(null, CLIPBOARD_SERVICE);
            IBinder proxyBinder =
                    (IBinder) Proxy.newProxyInstance(rawBinder.getClass().getClassLoader(),
                            new Class[] { IBinder.class }, new BinderProxyHookHandler(rawBinder));

            Field serviceCache = serviceManagerClass.getDeclaredField("sCache");
            serviceCache.setAccessible(true);
            Map<String, IBinder> cache = (Map<String, IBinder>) serviceCache.get(null);
            cache.put(CLIPBOARD_SERVICE, proxyBinder);
        } catch (ClassNotFoundException | NoSuchMethodException |
                IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
