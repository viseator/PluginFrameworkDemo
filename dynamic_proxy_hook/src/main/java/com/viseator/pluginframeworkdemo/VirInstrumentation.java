package com.viseator.pluginframeworkdemo;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wudi.viseator on 2018/7/3.
 * Wu Di
 * wudi.viseator@bytedance.com
 */
public class VirInstrumentation extends Instrumentation {
    private static final String TAG = "@vir VirInstrumentation";

    Instrumentation mBase;

    public VirInstrumentation(Instrumentation base) {
        mBase = base;
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target, Intent intent,
            int requestCode, Bundle options) {
        Log.d(TAG, "hooked");
        try {
            try {
                Method execStartActivity = Instrumentation.class.getDeclaredMethod("execStartActivity", Context.class,
                        IBinder.class, IBinder.class, Activity.class, Intent.class, int.class, Bundle.class);
                execStartActivity.setAccessible(true);
                return (ActivityResult) execStartActivity.invoke(mBase, who, contextThread, token, target, intent,
                        requestCode, options);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                Log.e(TAG, "", e);
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return null;
    }

}
