package com.example.muaaz.kitchen.classes;

/**
 * Created by muaaz on 2017/09/06.
 */

public class KitchenItem implements java.io.Serializable {
    public String itemName;
    public double itemPrice;
    public String description;
    public String cloudStorageUri;

    public KitchenItem(){}

    public KitchenItem(String itemName, double itemPrice, String description, String cloudStorageUri){
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.description = description;
        this.cloudStorageUri = cloudStorageUri;
    }
}