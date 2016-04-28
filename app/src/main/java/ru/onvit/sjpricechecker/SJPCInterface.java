package ru.onvit.sjpricechecker;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface SJPCInterface {
    @GET("/SMB/hs/WareBarcodePrice")
    Call<WareBarcodePriceJSONResponse> getJSON(@Header("Authorization") String authorization);
}
