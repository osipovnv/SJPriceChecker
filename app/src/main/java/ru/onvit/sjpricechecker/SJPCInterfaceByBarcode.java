package ru.onvit.sjpricechecker;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface SJPCInterfaceByBarcode {
    @GET("/SMB/hs/WareBarcodePrice/{barcode}")
    Call<WareBarcodePriceJSONResponse> getJSON(@Header("Authorization") String authorization, @Path("barcode") String barcode);
}
