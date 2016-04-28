package ru.onvit.sjpricechecker;

import java.util.ArrayList;
import java.util.List;

public class WareBarcodePriceJSONResponse {
    private List<ru.onvit.sjpricechecker.WareBarcodePrice> WareBarcodePrice = new ArrayList<ru.onvit.sjpricechecker.WareBarcodePrice>();

    public List<ru.onvit.sjpricechecker.WareBarcodePrice> getData() {
        return WareBarcodePrice;
    }
    public void setData(List<ru.onvit.sjpricechecker.WareBarcodePrice> WareBarcodePrice) {
        this.WareBarcodePrice = WareBarcodePrice;
    }
}