package com.example.asus.familyradar.model.SQlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.asus.familyradar.model.User;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase database;

    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "FamilyList.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private final String SQL_TABLE_USER = "CREATE TABLE " + UserList.UserListEntry.TABLE_NAME + " (" +
            UserList.UserListEntry.COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " +
            UserList.UserListEntry.COLUMN_USER_NAME + " TEXT NOT NULL, " +
            UserList.UserListEntry.COLUMN_USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
            UserList.UserListEntry.COLUMN_USER_PHOTO + " TEXT, " +
            UserList.UserListEntry.COLUMN_USER_LATITUDE + " DOUBLE, " +
            UserList.UserListEntry.COLUMN_USER_LONGITUDE + " DOUBLE, " +
            "UNIQUE (" + UserList.UserListEntry.COLUMN_USER_EMAIL + ") ON CONFLICT IGNORE " +
            "); ";

    private final String SQL_TABLE_FAMILY = "CREATE TABLE " + FamilyList.FamilyListEntry.TABLE_NAME + " (" +
            FamilyList.FamilyListEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FamilyList.FamilyListEntry.COLUMN_NAME + " TEXT NOT NULL, " +
            FamilyList.FamilyListEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
            FamilyList.FamilyListEntry.COLUMN_PHOTO + " INTEGER, " +
            FamilyList.FamilyListEntry.COLUMN_LATITUDE + " DOUBLE, " +
            FamilyList.FamilyListEntry.COLUMN_LONGITUDE + " DOUBLE " +
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
                "INSERT INTO " + FamilyList.FamilyListEntry.TABLE_NAME + " ("
                        + FamilyList.FamilyListEntry.COLUMN_NAME + ", "
                        + FamilyList.FamilyListEntry.COLUMN_EMAIL +", "
                        + FamilyList.FamilyListEntry.COLUMN_PHOTO + ", "
                        + FamilyList.FamilyListEntry.COLUMN_LATITUDE + ", "
                        + FamilyList.FamilyListEntry.COLUMN_LONGITUDE + ") "
                        + " SELECT "
                        + UserList.UserListEntry.COLUMN_USER_NAME + ", "
                        + UserList.UserListEntry.COLUMN_USER_EMAIL + ", "
                        + UserList.UserListEntry.COLUMN_USER_PHOTO + ", "
                        + UserList.UserListEntry.COLUMN_USER_LATITUDE + ", "
                        + UserList.UserListEntry.COLUMN_USER_LONGITUDE
                        + " FROM " + UserList.UserListEntry.TABLE_NAME
                        + " WHERE " + UserList.UserListEntry.COLUMN_USER_EMAIL + " =\'" + email + "\'";

        db.execSQL(ADD_FAMILY);
    }

    public ArrayList<User> getAllBeneficiary() {


        String[] columns = {

                FamilyList.FamilyListEntry.COLUMN_NAME,
                FamilyList.FamilyListEntry.COLUMN_EMAIL,
                FamilyList.FamilyListEntry.COLUMN_PHOTO,

        };

        String sortOrder =
                FamilyList.FamilyListEntry.COLUMN_NAME + " ASC";

        ArrayList<User> userList = new ArrayList<>();

        database = this.getReadableDatabase();


        Cursor cursor = database.query(FamilyList.FamilyListEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);


        if (cursor.moveToFirst()) {
            do {
                User user = new User();

                user.setName(cursor.getString(cursor.getColumnIndex(FamilyList.FamilyListEntry.COLUMN_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(FamilyList.FamilyListEntry.COLUMN_EMAIL)));
                user.setPhoto(cursor.getString(cursor.getColumnIndex(FamilyList.FamilyListEntry.COLUMN_PHOTO)));

                userList.add(user);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return userList;

    }

    public ArrayList<LatLng> getFamilyPlace() {


        String[] columns = {

                FamilyList.FamilyListEntry.COLUMN_LATITUDE,
                FamilyList.FamilyListEntry.COLUMN_LONGITUDE,

        };

        String sortOrder =
                FamilyList.FamilyListEntry.COLUMN_NAME + " ASC";

        ArrayList<LatLng> userList = new ArrayList<>();

        database = this.getReadableDatabase();


        Cursor cursor = database.query(FamilyList.FamilyListEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);


        if (cursor.moveToFirst()) {
            do {
                User user = new User();

                user.setLatitude(cursor.getDouble(cursor.getColumnIndex(FamilyList.FamilyListEntry.COLUMN_LATITUDE)));
                user.setLongitude(cursor.getDouble(cursor.getColumnIndex(FamilyList.FamilyListEntry.COLUMN_LONGITUDE)));

                userList.add(new LatLng(user.getLatitude(),user.getLongitude()));

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return userList;

    }

    public void addUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(UserList.UserListEntry.COLUMN_USER_NAME,user.getName());
        contentValues.put(UserList.UserListEntry.COLUMN_USER_EMAIL,user.getEmail());
        contentValues.put(UserList.UserListEntry.COLUMN_USER_PHOTO,user.getPhoto());
        contentValues.put(UserList.UserListEntry.COLUMN_USER_LATITUDE,user.getLatitude());
        contentValues.put(UserList.UserListEntry.COLUMN_USER_LONGITUDE,user.getLongitude());

        db.insert(UserList.UserListEntry.TABLE_NAME, null, contentValues);


    }

    public void updateUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(UserList.UserListEntry.COLUMN_USER_LATITUDE,user.getLatitude());
        contentValues.put(UserList.UserListEntry.COLUMN_USER_LONGITUDE,user.getLongitude());

        db.update(UserList.UserListEntry.TABLE_NAME, contentValues,null,null);
    }

    public boolean checkEmailFamily(String email) {

            SQLiteDatabase database = getWritableDatabase();

            String selectQuery = "SELECT  * FROM " + FamilyList.FamilyListEntry.TABLE_NAME + " WHERE "
                    + FamilyList.FamilyListEntry.COLUMN_EMAIL + " =? ";
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

        String selectQuery = "SELECT  * FROM " + UserList.UserListEntry.TABLE_NAME + " WHERE "
                + UserList.UserListEntry.COLUMN_USER_EMAIL + " =? ";
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
}
