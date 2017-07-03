package com.usamakzafar.trackerzandroidapplication.TrucksClasses;

/**
 * Created by usamazafar on 23/05/2017.
 */

public class TruckMenuItem {

    private String itemID;
    private String itemName;
    private String itemDescription;
    private int itemPrice;
    private String itemPicture;

    public TruckMenuItem(){}

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemPicture() {
        return itemPicture;
    }

    public void setItemPicture(String itemPicture) {
        this.itemPicture = itemPicture;
    }
}
