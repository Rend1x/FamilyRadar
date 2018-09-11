package com.example.asus.familyradar.model.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import com.example.asus.familyradar.R;
import com.example.asus.familyradar.model.SQlite.DatabaseHelper;
import com.example.asus.familyradar.model.SQlite.FamilyList;
import com.example.asus.familyradar.model.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;

public class SQLUtils {

    private DatabaseHelper databaseHelper;
    private User user;
    private Context context;
    private List<String> familyUpdate;
    private String[] columns;


    public SQLUtils(Context context) {
        this.context = context;
    }

    private void init(){
        familyUpdate = new ArrayList<>();
        user = new User();
        databaseHelper = new DatabaseHelper(context);
        familyUpdate.addAll(databaseHelper.getEmailFriends());
    }

    public void postDataToDataBase(double latitude, double longitude){

        init();

        user.setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        user.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        user.setPhoto(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());

        user.setLatitude(latitude);
        user.setLongitude(longitude);

        if (databaseHelper.checkEmailUser(user.getEmail())){
            updateDataToSql(latitude,longitude);
        }else {
            databaseHelper.addUser(user);
        }
    }

    public void updateDataToSql(double latitude, double longitude) {

        user.setLatitude(latitude);
        user.setLongitude(longitude);

        databaseHelper.updateUser(user,FirebaseAuth.getInstance().getCurrentUser().getEmail());

        for (int i = 0; i < familyUpdate.size(); i++) {

            user.setEmail(String.valueOf(familyUpdate.get(i)));

            databaseHelper.updatePositionFamily(user.getEmail());
        }
    }

    public void postDataToSQLite(String email) {

        init();

        user.setEmail(email);

        if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(user.getEmail())){

            Toast.makeText(context,R.string.add_error_1,Toast.LENGTH_SHORT).show();

        } else if(!databaseHelper.checkEmailUser(user.getEmail())){

            Toast.makeText(context,R.string.add_error_2,Toast.LENGTH_SHORT).show();

        } else if (databaseHelper.checkEmailFamily(user.getEmail())){

            Toast.makeText(context,R.string.add_error_3,Toast.LENGTH_SHORT).show();

        } else if (databaseHelper.checkEmailUser(user.getEmail())&& !databaseHelper.checkEmailFamily(user.getEmail())){

            databaseHelper.addFamily(user.getEmail());

            Toast.makeText(context, R.string.add_successful,Toast.LENGTH_SHORT).show();

        }
    }

    public ArrayList<LatLng> selectFriend(String name){


        columns = new String[]{

                FamilyList.FamilyListEntry.COLUMN_LATITUDE,
                FamilyList.FamilyListEntry.COLUMN_LONGITUDE,

        };

        String where =
                FamilyList.FamilyListEntry.COLUMN_NAME  + " =\'" + name + "\'";

        ArrayList<LatLng> userList = new ArrayList<>();

        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.query(FamilyList.FamilyListEntry.TABLE_NAME,
                columns,
                where,
                null,
                null,
                null,
                null);


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
}
