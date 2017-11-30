package com.example.muaaz.kitchen.classes;

import java.util.ArrayList;

/**
 * Created by muaaz on 2017/08/23.
 */

public class User {
    public String email;
    public String name;
    public String surname;
    public long phoneNo;
    public String password;
    public ArrayList<Order> userOrders;

    public User(){}

    public User(String email, String name, String surname,long phoneNo, String password){
        //User this = ......
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.phoneNo = phoneNo;
        this.password = password;
    }

    public void addOrder(Order order){
        userOrders.add(order);
    }
}
