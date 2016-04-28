package ru.onvit.sjpricechecker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns{

    // названия столбцов
    public static final String BARCODE_COLUMN = "barcode";
    public static final String WARENAME_COLUMN = "warename";
    public static final String PRICE_COLUMN = "price";
    // имя базы данных
    private static final String DATABASE_NAME = "sjmaintask.db";
    // версия базы данных
    private static final int DATABASE_VERSION = 1;
    // имя таблицы
    public static final String DATABASE_TABLE = "wareprice";

    private static final String DATABASE_CREATE_SCRIPT = "create table " + DATABASE_TABLE + " ("
            + BaseColumns._ID + " integer primary key autoincrement, "
            + BARCODE_COLUMN + " text not null, "
            + WARENAME_COLUMN + " text not null, "
            + PRICE_COLUMN + " real);";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Удаляем старую таблицу и создаём новую
        db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
        // Создаём новую таблицу
        onCreate(db);
    }
}
