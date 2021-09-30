package me.mclellan.servicecreator2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.widget.Toast;

public class wifi extends Service {
    public wifi() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wi = wm.getConnectionInfo();
        String ssid = wi.getSSID();
        Toast.makeText(this, ssid, Toast.LENGTH_LONG).show();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}