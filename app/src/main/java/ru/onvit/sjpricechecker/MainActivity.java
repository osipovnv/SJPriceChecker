package ru.onvit.sjpricechecker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "ru.onvit.sjpricechecker";

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;

    private ArrayList<WareBarcodePrice> data;

    private static final String url = "http://1c.ps-it.ru";
    private static final String SECRET_KEY = "Basic " + Base64.encodeToString(("StudyJamsEKB:StudyJamsEKB").getBytes(), Base64.NO_WRAP);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabaseHelper = new DatabaseHelper(this);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();

        findViewById(R.id.btn_Scan).setOnClickListener(this);
        findViewById(R.id.btn_updatedata).setOnClickListener(this);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Scan:
                EditText barcodeEdit = (EditText) findViewById(R.id.barcode);
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.initiateScan();
                break;
            case R.id.btn_updatedata:
                Retrofit radapter = new Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                SJPCInterface request = radapter.create(SJPCInterface.class);
                Call<WareBarcodePriceJSONResponse> call = request.getJSON(SECRET_KEY);
                call.enqueue(new Callback<WareBarcodePriceJSONResponse>() {
                    @Override
                    public void onResponse(Call<WareBarcodePriceJSONResponse> call, Response<WareBarcodePriceJSONResponse> response) {
                        WareBarcodePriceJSONResponse jsonResponse = response.body();
                        if (jsonResponse != null) {
                            data = new ArrayList<WareBarcodePrice>(jsonResponse.getData());
                            // Здесь надо делать загрузку в фоновом режиме
                            for(int i=0; i< data.size(); i++){
                                insertToDataBase(data.get(i));
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<WareBarcodePriceJSONResponse> call, Throwable t) {
                        Log.d(TAG, t.getMessage());
                    }
                });
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        CheckBox checkoffline = (CheckBox) findViewById(R.id.checkoffline);
        if (scanningResult != null) {
            String contents = scanningResult.getContents();
            String format = scanningResult.getFormatName();
            Log.i(TAG, "Barcode: " + contents + " Format: " + format);
            if (contents != null) {
                if (checkoffline.isChecked()) {
                    seachInDataBase(contents);
                } else {
                    Retrofit radapter = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    SJPCInterfaceByBarcode request = radapter.create(SJPCInterfaceByBarcode.class);
                    Call<WareBarcodePriceJSONResponse> call = request.getJSON(SECRET_KEY, contents);
                    call.enqueue(new Callback<WareBarcodePriceJSONResponse>() {
                        @Override
                        public void onResponse(Call<WareBarcodePriceJSONResponse> call, Response<WareBarcodePriceJSONResponse> response) {
                            WareBarcodePriceJSONResponse jsonResponse = response.body();
                            if (jsonResponse != null) {
                                data = new ArrayList<WareBarcodePrice>(jsonResponse.getData());
                                updateView(data.get(0).getName(), data.get(0).getBarcode(), Float.parseFloat(data.get(0).getPrice()));
                            }
                        }

                        @Override
                        public void onFailure(Call<WareBarcodePriceJSONResponse> call, Throwable t) {
                            Log.d(TAG, t.getMessage());
                        }
                    });
                }
            }
        } else {
            String nodata = getResources().getString(R.string.nodata);
            Toast toast = Toast.makeText(getApplicationContext(), nodata, Toast.LENGTH_SHORT);
            toast.show();
            Log.i(TAG, nodata);
        }
    }

    private void seachInDataBase(String s) {
        String warename = getResources().getString(R.string.emptystring);
        float price = 0;
        Cursor cursor = mSqLiteDatabase.query(DatabaseHelper.DATABASE_TABLE, new String[]{DatabaseHelper.BARCODE_COLUMN, DatabaseHelper.WARENAME_COLUMN, DatabaseHelper.PRICE_COLUMN},
                DatabaseHelper.BARCODE_COLUMN + " = ?", new String[]{s}, null, null, null);
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            warename = cursor.getString(cursor.getColumnIndex(DatabaseHelper.WARENAME_COLUMN));
            price = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.PRICE_COLUMN));
        }
        cursor.close();
        updateView(warename, s, price);
    }

    private void insertToDataBase(WareBarcodePrice data) {
        mSqLiteDatabase.delete(DatabaseHelper.DATABASE_TABLE, null, null);
        mSqLiteDatabase.insert(DatabaseHelper.DATABASE_TABLE, null, data.getValues());
    }
    private void updateView(String warename, String barcode, float price) {
        EditText barcodeEdit = (EditText) findViewById(R.id.barcode);
        TextView warenameView = (TextView) findViewById(R.id.warename);
        TextView priceView = (TextView) findViewById(R.id.price);

        barcodeEdit.setText(barcode);
        warenameView.setText(warename);
        priceView.setText(String.valueOf(price));
    }
}
