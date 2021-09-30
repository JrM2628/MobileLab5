package me.mclellan.serviceuser2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Messenger mService = null;
    boolean mIsBound;

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case 1:
                    Bundle data = msg.getData();
                    String ssid = data.getString("ssid");
                    Toast.makeText(getApplicationContext(), "FROM SERVICE INVOKER:" + ssid, Toast.LENGTH_LONG).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mService = new Messenger(service);
            Toast.makeText(getApplicationContext(), "Attached", Toast.LENGTH_LONG).show();

            try{
                Message msg = Message.obtain(null, 1);
                msg.replyTo = mMessenger;
                mService.send(msg);

            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent();
        i.putExtra("message", "Hello World AAAA");
        i.setComponent(new ComponentName("me.mclellan.servicecreator2", "me.mclellan.servicecreator2.wifi"));
        startService(i);
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
    }
}