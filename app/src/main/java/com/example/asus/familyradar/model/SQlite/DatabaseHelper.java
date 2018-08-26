package com.example.asus.familyradar.model.SQlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.asus.familyradar.model.SQlite.FamilyList.FamilyListEntry;
import com.example.asus.familyradar.model.SQlite.UserList.UserListEntry;

import com.example.asus.familyradar.model.User;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase database;

    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "FamilyList.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private final String SQL_TABLE_USER = "CREATE TABLE " + UserListEntry.TABLE_NAME + " (" +
            UserListEntry.COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
            UserListEntry.COLUMN_USER_NAME + " TEXT NOT NULL, " +
            UserListEntry.COLUMN_USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
            UserListEntry.COLUMN_USER_PHOTO + " TEXT, " +
            UserListEntry.COLUMN_USER_LATITUDE + " DOUBLE, " +
            UserListEntry.COLUMN_USER_LONGITUDE + " DOUBLE, " +
            "UNIQUE (" + UserListEntry.COLUMN_USER_EMAIL + ") ON CONFLICT IGNORE " +
            "); ";

    private final String SQL_TABLE_FAMILY = "CREATE TABLE " + FamilyListEntry.TABLE_NAME + " (" +
            FamilyListEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FamilyListEntry.COLUMN_NAME + " TEXT NOT NULL, " +
            FamilyListEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
            FamilyListEntry.COLUMN_PHOTO + " INTEGER, " +
            FamilyListEntry.COLUMN_LATITUDE + " DOUBLE, " +
            FamilyListEntry.COLUMN_LONGITUDE + " DOUBLE " +
            "); ";



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(SQL_TABLE_FAMILY);
        sqLiteDatabase.execSQL(SQL_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String DROP_BENEFICIARY_TABLE =
                "DROP TABLE IF EXISTS " + UserList.UserListEntry.TABLE_NAME;

        db.execSQL(DROP_BENEFICIARY_TABLE);

        onCreate(db);

    }

    public void addFamily(String email) {

        SQLiteDatabase db = this.getWritableDatabase();

        final String ADD_FAMILY =
                "INSERT OR REPLACE INTO "
                        + FamilyListEntry.TABLE_NAME + " ("
                        + FamilyListEntry.COLUMN_NAME +", "
                        + FamilyListEntry.COLUMN_EMAIL + ", "
                        + FamilyListEntry.COLUMN_PHOTO + ", "
                        + FamilyListEntry.COLUMN_LATITUDE + ", "
                        + FamilyListEntry.COLUMN_LONGITUDE + ") "
                        + " SELECT "
                        + UserListEntry.COLUMN_USER_NAME + ", "
                        + UserListEntry.COLUMN_USER_EMAIL + ", "
                        + UserListEntry.COLUMN_USER_PHOTO + ", "
                        + UserListEntry.COLUMN_USER_LATITUDE + ", "
                        + UserListEntry.COLUMN_USER_LONGITUDE
                        + " FROM " + UserListEntry.TABLE_NAME
                        + " WHERE " + UserListEntry.COLUMN_USER_EMAIL + " =\'" + email + "\'";

        db.execSQL(ADD_FAMILY);
    }

    public void updatePositionFamily(String email){

        SQLiteDatabase db = this.getWritableDatabase();

        final String UPDATE_FAMILY =
                "UPDATE " + FamilyListEntry.TABLE_NAME
                        + " SET "
                        + FamilyListEntry.COLUMN_LATITUDE + " = ( SELECT " + UserListEntry.COLUMN_USER_LATITUDE
                        + " FROM " + UserListEntry.TABLE_NAME
                        + " WHERE " + UserListEntry.COLUMN_USER_EMAIL + " =\'" + email + "\' ) , "
                        + FamilyListEntry.COLUMN_LONGITUDE + " = ( SELECT " + UserListEntry.COLUMN_USER_LONGITUDE
                        + " FROM " + UserListEntry.TABLE_NAME
                        + " WHERE " + UserListEntry.COLUMN_USER_EMAIL + " =\'" + email + "\' )";

        db.execSQL(UPDATE_FAMILY);


    }

    public void updateNameFamily(String name,String email){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(FamilyListEntry.COLUMN_NAME,name);

        db.update(FamilyListEntry.TABLE_NAME, contentValues,"email " + " =\'" + email + "\'",null);

    }

    public ArrayList<User> getAllBeneficiary() {


        String[] columns = {

                FamilyListEntry.COLUMN_NAME,
                FamilyListEntry.COLUMN_EMAIL,
                FamilyListEntry.COLUMN_PHOTO,

        };

        String sortOrder =
                FamilyListEntry.COLUMN_NAME + " ASC";

        ArrayList<User> userList = new ArrayList<>();

        database = this.getReadableDatabase();


        Cursor cursor = database.query(FamilyListEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);


        if (cursor.moveToFirst()) {
            do {
                User user = new User();

                user.setName(cursor.getString(cursor.getColumnIndex(FamilyListEntry.COLUMN_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(FamilyListEntry.COLUMN_EMAIL)));
                user.setPhoto(cursor.getString(cursor.getColumnIndex(FamilyListEntry.COLUMN_PHOTO)));

                userList.add(user);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return userList;

    }

    public ArrayList<LatLng> getFamilyPlace() {


        String[] columns = {

                FamilyListEntry.COLUMN_LATITUDE,
                FamilyListEntry.COLUMN_LONGITUDE,

        };

        String sortOrder =
                FamilyListEntry.COLUMN_NAME + " ASC";

        ArrayList<LatLng> userList = new ArrayList<>();

        database = this.getReadableDatabase();


        Cursor cursor = database.query(FamilyListEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);


        if (cursor.moveToFirst()) {
            do {
                User user = new User();

                user.setLatitude(cursor.getDouble(cursor.getColumnIndex(FamilyListEntry.COLUMN_LATITUDE)));
                user.setLongitude(cursor.getDouble(cursor.getColumnIndex(FamilyListEntry.COLUMN_LONGITUDE)));

                userList.add(new LatLng(user.getLatitude(),user.getLongitude()));

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return userList;

    }

    public ArrayList<String> getEmailFriends() {


        String[] columns = {

                FamilyListEntry.COLUMN_EMAIL

        };


        ArrayList<String> friendsList = new ArrayList<>();

        database = this.getReadableDatabase();


        Cursor cursor = database.query(FamilyListEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null);


        if (cursor.moveToFirst()) {
            do {
                User user = new User();

                user.setEmail(cursor.getString(cursor.getColumnIndex(FamilyListEntry.COLUMN_EMAIL)));

                friendsList.add(user.getEmail());

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return friendsList;

    }

    public void addUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(UserListEntry.COLUMN_USER_NAME,user.getName());
        contentValues.put(UserListEntry.COLUMN_USER_EMAIL,user.getEmail());
        contentValues.put(UserListEntry.COLUMN_USER_PHOTO,user.getPhoto());
        contentValues.put(UserListEntry.COLUMN_USER_LATITUDE,user.getLatitude());
        contentValues.put(UserListEntry.COLUMN_USER_LONGITUDE,user.getLongitude());

        db.insert(UserListEntry.TABLE_NAME, null, contentValues);


    }

    public void updateUser(User user,String email) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(UserListEntry.COLUMN_USER_LATITUDE,user.getLatitude());
        contentValues.put(UserListEntry.COLUMN_USER_LONGITUDE,user.getLongitude());

        db.update(UserListEntry.TABLE_NAME, contentValues,"email " + " =\'" + email + "\'",null);
    }

    public boolean checkEmailFamily(String email) {

            SQLiteDatabase database = getWritableDatabase();

            String selectQuery = "SELECT  * FROM " + FamilyListEntry.TABLE_NAME + " WHERE "
                    + FamilyListEntry.COLUMN_EMAIL + " =? ";
            Cursor cursor = database.rawQuery(selectQuery, new String[]{email});
            boolean checkEmailFamily = false;
            if (cursor.moveToFirst()) {
                checkEmailFamily = true;
                int count = 0;
                while(cursor.moveToNext()){
                    count++;
                }
                Log.d("dataBase", String.format("%d records found", count));
            }
            cursor.close();
            database.close();
            return checkEmailFamily;
    }

    public boolean checkEmailUser(String email) {

        SQLiteDatabase database = getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + UserListEntry.TABLE_NAME + " WHERE "
                + UserListEntry.COLUMN_USER_EMAIL + " =? ";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{email});
        boolean checkEmailUser = false;
        if (cursor.moveToFirst()) {
            checkEmailUser = true;
            int count = 0;
            while(cursor.moveToNext()){
                count++;
            }
            Log.d("dataBase", String.format("%d records found", count));
        }
        cursor.close();
        database.close();
        return checkEmailUser;
    }

    public void delete(String email) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(FamilyListEntry.TABLE_NAME, "email " + " =\'" + email + "\'", null);

    }
}
