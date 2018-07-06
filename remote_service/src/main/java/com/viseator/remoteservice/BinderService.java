package com.viseator.remoteservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import com.viseator.remote_service.IRemoteService;

/**
 * Created by wudi.viseator on 2018/7/4.
 * Wu Di
 * wudi.viseator@bytedance.com
 */
public class BinderService extends Service {
    private static final String TAG = "@vir BinderService";

    private IRemoteService.Stub mService = new IRemoteService.Stub() {
        @Override public String requestStringFromService(int requestCode) throws RemoteException {
            Log.d(TAG, String.valueOf(requestCode));
            return "response from binder service";
        }
    };

    @Nullable @Override public IBinder onBind(Intent intent) {
        return mService.asBinder();
    }
}
