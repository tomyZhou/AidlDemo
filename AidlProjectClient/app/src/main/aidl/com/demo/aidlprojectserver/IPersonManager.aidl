// IPersonManager.aidl
package com.demo.aidlprojectserver;

import com.demo.aidlprojectserver.Person;

interface IPersonManager {
    List<Person> getPersons();

    void addPerson(in Person person); //除了基本数据类型，要添加 in out inout

    String greet(String name);

}
