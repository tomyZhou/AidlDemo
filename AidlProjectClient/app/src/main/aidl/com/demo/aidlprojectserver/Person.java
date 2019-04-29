package com.demo.aidlprojectserver;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {

    private String name;
    private String sex;
    private int age;

    protected Person(Parcel in) {
        this.name = in.readString();  //读取的顺序要和writeToParcel的write顺序一致
        this.sex = in.readString();
        this.age = in.readInt();
    }

    public Person(String name, String sex, int age) {
        this.name = name;
        this.sex = sex;
        this.age = age;
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(sex);
        dest.writeInt(age);
    }

    @Override
    public String toString() {
        return name + ":" + sex + ":" + age;

    }
}
