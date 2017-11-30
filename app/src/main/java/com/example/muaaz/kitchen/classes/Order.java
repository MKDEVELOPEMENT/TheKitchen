package com.example.muaaz.kitchen.classes;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by muaaz on 2017/10/14.
 */

public class Order {

    public int OrderNumber;
    public ArrayList<KitchenItem> Items;
    public Double TotalPrice;
    public Date PickUpDate;
    public Boolean Confirmed;
    public String Owner;

    public Order(){}

    public Order(int orderNumber, ArrayList<KitchenItem> kitchenItems, Double Total, Date pickUpDate, Boolean confirmed, String owner){
        OrderNumber = orderNumber;
        Items = kitchenItems;
        TotalPrice = Total;
        PickUpDate = pickUpDate;
        Confirmed = confirmed;
        Owner = owner;
    }

    public void changeDate(Date newDate){
        PickUpDate = newDate;
    }
}
