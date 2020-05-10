package com.example.inlocker_shopkeeper;

public class StoreItem {
    private String Name;
    private int Price;
    private int Amount;
    private String Category;

    public StoreItem() {
        //empty constructor needed
    }

    public StoreItem(String Name, int Price, int Amount, String Category) {
        this.Name = Name;
        this.Price = Price;
        this.Amount = Amount;
        this.Category = Category;
    }

    public String getName() {
        return Name;
    }

    public int getPrice() {
        return Price;
    }

    public int getAmount() {
        return Amount;
    }

    public String getItemCategory() {
        return Category;
    }
}
