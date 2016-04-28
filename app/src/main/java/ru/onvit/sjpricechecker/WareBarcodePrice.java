package ru.onvit.sjpricechecker;

import android.content.ContentValues;

public class WareBarcodePrice {

    private String Name;
    private String Barcode;
    private String Price;

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String Barcode) {
        this.Barcode = Barcode;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String Price) {
        this.Price = Price;
    }

    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.WARENAME_COLUMN, getName());
        values.put(DatabaseHelper.BARCODE_COLUMN, getBarcode());
        values.put(DatabaseHelper.PRICE_COLUMN, getPrice());
        return values;
    }

}
