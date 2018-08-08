package com.example.asus.familyradar.model.SQlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.asus.familyradar.model.User;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase database;

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "FamilyListGoogle.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + FamilyListGoogle.FamilyListEntry.TABLE_NAME + " (" +
                FamilyListGoogle.FamilyListEntry.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                FamilyListGoogle.FamilyListEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                FamilyListGoogle.FamilyListEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
                FamilyListGoogle.FamilyListEntry.COLUMN_PHOTO + " INTEGER, " +
                FamilyListGoogle.FamilyListEntry.COLUMN_LATITUDE + " INTEGER, " +
                FamilyListGoogle.FamilyListEntry.COLUMN_LONGITUDE + " INTEGER " +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String DROP_BENEFICIARY_TABLE =
                "DROP TABLE IF EXISTS " + FamilyListGoogle.FamilyListEntry.TABLE_NAME;

        db.execSQL(DROP_BENEFICIARY_TABLE);

        onCreate(db);
    }


    public void addUser(User family) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(FamilyListGoogle.FamilyListEntry.COLUMN_NAME,family.getName());
        contentValues.put(FamilyListGoogle.FamilyListEntry.COLUMN_EMAIL,family.getEmail());
        contentValues.put(FamilyListGoogle.FamilyListEntry.COLUMN_PHOTO,family.getPhoto());
        contentValues.put(FamilyListGoogle.FamilyListEntry.COLUMN_LATITUDE,family.getLatitude());
        contentValues.put(FamilyListGoogle.FamilyListEntry.COLUMN_LONGITUDE,family.getLongitude());


        db.insert(FamilyListGoogle.FamilyListEntry.TABLE_NAME, null, contentValues);


    }

    public ArrayList<User> getAllBeneficiary() {


        String[] columns = {

                FamilyListGoogle.FamilyListEntry.COLUMN_NAME,
                FamilyListGoogle.FamilyListEntry.COLUMN_EMAIL,

        };

        String sortOrder =
                FamilyListGoogle.FamilyListEntry.COLUMN_NAME + " ASC";

        ArrayList<User> userList = new ArrayList<>();

        database = this.getReadableDatabase();


        Cursor cursor = database.query(FamilyListGoogle.FamilyListEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);


        if (cursor.moveToFirst()) {
            do {
                User user = new User();

                user.setName(cursor.getString(cursor.getColumnIndex(FamilyListGoogle.FamilyListEntry.COLUMN_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(FamilyListGoogle.FamilyListEntry.COLUMN_EMAIL)));

                userList.add(user);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return userList;

    }

}
