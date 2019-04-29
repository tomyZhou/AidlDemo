package com.demo.aidlprojectclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.demo.aidlprojectserver.IPersonManager;
import com.demo.aidlprojectserver.Person;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private boolean connected = false;
    private IPersonManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt_bind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.demo.aidlprojectserver", "com.demo.aidlprojectserver.AIDLService");
                connected = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        });

        findViewById(R.id.bt_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected) {
                    Person person = new Person("lili", "女", 18);
                    try {
                        manager.addPerson(person);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        findViewById(R.id.bt_greet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected && manager != null) {
                    try {
                        Toast.makeText(MainActivity.this, manager.greet("曹操"), 0).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            manager = IPersonManager.Stub.asInterface(service);
            try {
                List<com.demo.aidlprojectserver.Person> personList = manager.getPersons();
                for (Person person : personList) {
                    Log.d(TAG, person.toString());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
