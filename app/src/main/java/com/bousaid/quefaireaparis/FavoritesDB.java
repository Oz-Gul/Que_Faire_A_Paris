package com.bousaid.quefaireaparis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FavoritesDB extends SQLiteOpenHelper {

    private static int DB_VERSION = 1;
    private static String DATABASE_NAME = "FavoritesDB";
    private static String TABLE_NAME = "Activities";
    public static String KEY_ID = "id";
    public static String ITEM_TITLE = "title";
    public static String ITEM_ADDRESS = "address";
    public static String ITEM_TEXT = "text";
    public static String ITEM_URL = "url";
    public static String FAVORITE_STATUS = "favoriteStatus";
    public static String DATE_DESCRIPTION = "dateDescription";
    public static String PRICE = "price";

    public static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + KEY_ID + " TEXT," + ITEM_TITLE + " TEXT," + ITEM_ADDRESS + " TEXT," +
            ITEM_TEXT+ " TEXT," + ITEM_URL + " TEXT," + FAVORITE_STATUS + " TEXT,"  + PRICE + " TEXT," + DATE_DESCRIPTION  + " TEXT)";


    public  FavoritesDB(Context context){
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Create empty table
    /*public void insertEmpty(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        for(int x=1;x<11;x++){
            cv.put(KEY_ID, x);
            cv.put(FAVORITE_STATUS, "0");

            db.insert(TABLE_NAME, null, cv);
        }
    }

     */

    //Checks if data is in database (= 1 if exists, = 0 if does not exist)
    public Cursor isInDatabase(String id){
        String exists = "SELECT EXISTS(SELECT 1 FROM " + TABLE_NAME + " WHERE " + KEY_ID + "='" + id +"')";
        SQLiteDatabase db;
        db = this.getWritableDatabase();
        return db.rawQuery(exists,null,null);
    }

    //Insert data into database
    public void insertIntoDatabase(String item_title, String item_address, String item_text, String item_url, String favorite_status, String dateDescription, String id, String price){
        SQLiteDatabase db;
        db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ITEM_TITLE, item_title);
        cv.put(ITEM_ADDRESS, item_address);
        cv.put(ITEM_TEXT, item_text);
        cv.put(ITEM_URL, item_url);
        cv.put(FAVORITE_STATUS, favorite_status);
        cv.put(KEY_ID, id);
        cv.put(DATE_DESCRIPTION, dateDescription);
        cv.put(PRICE, price);

        db.insert(TABLE_NAME, null, cv);
        Log.d("FavoritesDB Status", item_title + ", favorite_status - " + favorite_status +" - . " + cv);
    }

    //Read data
    public Cursor readData(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID + "=" + id;
        return db.rawQuery(sql,null,null);
    }

    //Remove line from database
    public void removeFavorite(String id){
        Log.d("remove", "" +id);
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + KEY_ID + "='" + id + "'";
        db.execSQL(sql);
    }

    //Select all favorites
    public Cursor selectFavorites(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + FAVORITE_STATUS + " ='1'";
        return db.rawQuery(sql, null,null);
    }
}
