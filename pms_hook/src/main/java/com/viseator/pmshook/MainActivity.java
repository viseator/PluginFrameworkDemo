package com.viseator.pmshook;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test();
        getPackageManager().getInstalledApplications(0);
    }

    private void test() {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentATMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            Object currentAT = currentATMethod.invoke(null);

            Field pmField = activityThreadClass.getDeclaredField("sPackageManager");
            pmField.setAccessible(true);
            Object pm = pmField.get(currentAT);

            Class<?> ipmInterface = Class.forName("android.content.pm.IPackageManager");
            Object proxy = Proxy.newProxyInstance(ipmInterface.getClassLoader(), new Class[]{ipmInterface},
                    new IPackageManagerHandler(pm));
            pmField.set(currentAT, proxy);

            PackageManager packageManager = this.getPackageManager();
            Field mpmField = packageManager.getClass().getDeclaredField("mPM");
            mpmField.setAccessible(true);
            mpmField.set(packageManager, proxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
