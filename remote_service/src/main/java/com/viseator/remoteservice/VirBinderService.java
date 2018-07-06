package com.viseator.remoteservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by wudi.viseator on 2018/7/6.
 * Wu Di
 * wudi.viseator@bytedance.com
 */
public class VirBinderService extends Service {
    private static final String TAG = "@vir VirBinderService";

    private IVirService.Stub mService = new IVirService.Stub() {
        @Override public String requestStringFromService(int requestCode) {
            Log.d(TAG, String.valueOf(requestCode));
            return "response from vir binder service";
        }
    };

    @Nullable @Override public IBinder onBind(Intent intent) {
        return mService.asBinder();
    }
}
