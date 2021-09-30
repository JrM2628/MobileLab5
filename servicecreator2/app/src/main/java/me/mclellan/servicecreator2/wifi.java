package me.mclellan.servicecreator2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

public class wifi extends Service {
    private static WifiManager wm;
    private static WifiInfo wi;
    static final int MSG_GET_SSID = 1;

    //Handler stuff to handle incoming messages
    class IncomingHandler extends Handler {
        private Context applicationContext;

        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case MSG_GET_SSID:
                    try {
                        Message m = Message.obtain(null, MSG_GET_SSID, 0, 0);
                        Bundle bundle = new Bundle();
                        bundle.putString("ssid", wi.getSSID());
                        m.setData(bundle);
                        msg.replyTo.send(m);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    // Default constructor
    public wifi() {
    }


    @Override
    // Creates WiFiManager and WiFi connection info objects
    public int onStartCommand(Intent intent, int flags, int startId){
        this.wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        this.wi  = this.wm.getConnectionInfo();
        return Service.START_NOT_STICKY;
    }

    public void onCreate(){
        return;
    }

    @Override
    // Returns binder
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}