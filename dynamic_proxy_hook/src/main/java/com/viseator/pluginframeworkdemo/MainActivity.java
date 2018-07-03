package com.viseator.pluginframeworkdemo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends Activity {
    private static final String TAG = "@vir MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        test();
        setContentView(R.layout.activity_main);
        getApplicationContext().startActivity(new Intent(this, TargetActivity.class));
    }

    void test() {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);

            Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            Instrumentation instrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);

            Instrumentation virInstrumentation = new VirInstrumentation(instrumentation);

            mInstrumentationField.set(currentActivityThread, virInstrumentation);
        } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException |
                InvocationTargetException | NoSuchMethodException e) {
            Log.e(TAG, "", e);
        }
    }
}
