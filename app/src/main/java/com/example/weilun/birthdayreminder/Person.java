package com.example.weilun.birthdayreminder;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Wei Lun on 8/7/2017.
 */

public class Person implements Serializable{
    private long id;
    private String name, email, phone;
    private Date dob;
    private Boolean notify;
    private int imageResourceId;

    public Person (){}

    public Person (String name, String email, String phone, Date dob, Boolean notify){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.notify = notify;
    }
    public Person (String name, String email, String phone, Date dob, Boolean notify, int imageResourceId){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dob = dob;
        this.notify = notify;
        this.imageResourceId = imageResourceId;
    }

    public long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getPhone(){
        return phone;
    }

    public Date getDOB(){
        return dob;
    }

    public Calendar getDOBAsCalender(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dob.getTime());
        return calendar;
    }

    public Boolean isNotify(){
        return notify;
    }

    public int getImageResourceId(){
        return imageResourceId;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public void setDob(Date dob){
        this.dob = dob;
    }

    public void setNotify(Boolean notify){
        this.notify = notify;
    }

    public void setImageResourceId(int imageResourceId){
        this.imageResourceId = imageResourceId;
    }

    public void setId(Long id){
        this.id = id;
    }
}
