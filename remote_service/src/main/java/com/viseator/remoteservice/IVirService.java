package com.viseator.remoteservice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by wudi.viseator on 2018/7/6.
 * Wu Di
 * wudi.viseator@bytedance.com
 */
public interface IVirService extends IInterface {
    int FUNCTION_CODE = 0x2;
    String requestStringFromService(int requestCode) throws RemoteException;

    abstract class Stub extends Binder implements IVirService {

        public Stub() {
            attachInterface(this, "VirService");
        }

        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply,
                int flags) throws RemoteException {
            switch (code) {
                case FUNCTION_CODE:
                    String result = requestStringFromService(data.readInt());
                    reply.writeString(result);
                    return true;
                default:
                    break;
            }
            return false;
        }

        @Override public IBinder asBinder() {
            return this;
        }

        public static IVirService asInterface(IBinder binder) {
            return new Proxy(binder);
        }

        static class Proxy implements IVirService {
            private IBinder mRemote;

            public Proxy(IBinder binder) {
                mRemote = binder;
            }

            @Override public String requestStringFromService(int requestCode) throws RemoteException{
                Parcel data = Parcel.obtain();
                data.writeInt(requestCode);
                Parcel reply = Parcel.obtain();
                mRemote.transact(FUNCTION_CODE, data, reply, 0);
                return reply.readString();
            }

            @Override public IBinder asBinder() {
                return mRemote;
            }
        }
    }
}
