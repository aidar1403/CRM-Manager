package com.crm.model;

import java.io.Serializable;

public abstract class Person implements Serializable{
private static final long serialVersionUID = 1L;

private String name;
private String email;
private String phone;

public Person(String name,String email,String phone) {
    this.name = name;
    this.email = email;
    this.phone = phone;
}

public String getName(){return name;}
    public String getEmail(){return email;}
    public String getPhone(){return phone;}

    public void setName(String name){this.name = name;}
    public void setEmail(String email){this.email = email;}
    public void setPhone(String phone){this.phone = phone;}

    public abstract String getDisplayInfo();
}
