package com.viseator.remoteservice;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class MessengerService extends Service {
    private static final String TAG = "@vir MessengerService";

    public MessengerService() {
    }

    final private Messenger mMessenger = new Messenger(new ServiceHandler());
    private Messenger mMainMessenger;

    class ServiceHandler extends Handler {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    Message response = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("string", "string from service");
                    response.obj = bundle;
                    try {
                        mMainMessenger.send(response);
                    } catch (RemoteException e) {
                        Log.e(TAG, "", e);
                    }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        mMainMessenger = intent.getParcelableExtra("msg");
        return mMessenger.getBinder();
    }
}
