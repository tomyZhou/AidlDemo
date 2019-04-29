package com.demo.aidlprojectserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.demo.aidlprojectserver.IPersonManager;
import com.demo.aidlprojectserver.Person;

import java.util.ArrayList;
import java.util.List;

public class AIDLService extends Service {

    public static final String TAG = AIDLService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mManage.addPerson(new Person("张三", "男", 22));
            mManage.addPerson(new Person("李四", "男", 30));
            mManage.addPerson(new Person("婉儿", "女", 18));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private List<Person> mPersons = new ArrayList<>();

    private IPersonManager.Stub mManage = new IPersonManager.Stub() {
        @Override
        public List<Person> getPersons() throws RemoteException {
            return mPersons;
        }

        @Override
        public void addPerson(Person person) throws RemoteException {
            Log.d(TAG, "client add person-----" + person.toString());
            mPersons.add(person);
        }

        @Override
        public String greet(String name) {
            return "热烈祝贺" + name + "来我院视察";
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mManage;
    }
}
