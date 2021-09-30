package me.mclellan.servicecreator2;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.logging.Level;
import java.util.logging.Logger;

public class wifi extends Service {
    private static WifiManager wm;
    private static WifiInfo wi;
    private static ContentResolver contentResolver;
    static final int MSG_GET_SSID = 1;
    static final int MSG_GET_CONTACTS = 2;

    //Handler stuff to handle incoming messages
    class IncomingHandler extends Handler {
        private Context applicationContext;

        @SuppressLint("Range")
        @Override
        public void handleMessage(Message msg){
            Bundle bundle;
            Message m;
            switch (msg.what) {
                // Reply to message with SSID content
                case MSG_GET_SSID:
                    try {
                        m = Message.obtain(null, MSG_GET_SSID, 0, 0);
                        bundle = new Bundle();
                        bundle.putString("ssid", wi.getSSID());
                        m.setData(bundle);
                        msg.replyTo.send(m);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;

                // Reply with contact details
                case MSG_GET_CONTACTS:
                    contentResolver = getContentResolver();
                    Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);

                    if (cursor.moveToFirst()){
                        do {
                            try {
                                m = Message.obtain(null, MSG_GET_CONTACTS, 0, 0);
                                bundle = new Bundle();

                                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                if(cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER)) == 1) {
                                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    bundle.putString("contact", contactName + ": " + phoneNumber);
                                }
                                m.setData(bundle);
                                msg.replyTo.send(m);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }while (cursor.moveToNext());
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
        wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wi  = wm.getConnectionInfo();
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