package com.viseator.remoteservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.support.v7.app.AppCompatActivity
import com.viseator.remote_service.IRemoteService
import kotlinx.android.synthetic.main.activity_main.button1
import kotlinx.android.synthetic.main.activity_main.button2
import kotlinx.android.synthetic.main.activity_main.text_view

class MainActivity : AppCompatActivity() {

    private var serviceMessenger: Messenger? = null
    private var remoteBinder: IRemoteService? = null
    private val handler = Handler {
        text_view.text = (it.obj as Bundle).getString("string")
        true
    }
    private val mainMessenger = Messenger(handler)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (name?.className == "com.viseator.remoteservice.MessengerService") {
                serviceMessenger = Messenger(service)
            } else {
                remoteBinder = IRemoteService.Stub.asInterface(service)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text_view.text = "inited"

        val intent = Intent(this, MessengerService::class.java)
        intent.putExtra("msg", mainMessenger)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        bindService(
                Intent(this, BinderService::class.java), serviceConnection, Context.BIND_AUTO_CREATE
        )

        button1.setOnClickListener {
            val msg = Message.obtain()
            serviceMessenger?.send(msg)
        }
        button2.setOnClickListener {
            text_view.text = remoteBinder?.requestStringFromService(666)
        }
    }
}
