package com.example.asus.familyradar.view.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.familyradar.R;
import com.example.asus.familyradar.model.SQlite.DatabaseHelper;
import com.example.asus.familyradar.model.SQlite.FamilyList;
import com.example.asus.familyradar.model.User;
import com.example.asus.familyradar.view.FamilyListActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class AddFriendFragment extends Fragment {

    private View view;
    private EditText friendEmail;

    private Button mAddFriend;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private User user;
    private List<User> listUser;
    private DatabaseHelper databaseHelper;

    public AddFriendFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listUser = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_friend,container,false);

        init();
        initObjects();

        mAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDataToSQLite();
            }
        });

        Log.d("addFriend","Массив " + listUser.size());

        return view;
    }

    private void postDataToSQLite() {

        user.setEmail(friendEmail.getText().toString().trim());

        Log.d("addFriend","Email " + user.getEmail());

        if (databaseHelper.checkEmailUser(user.getEmail()) == true && databaseHelper.checkEmailFamily(user.getEmail()) == false){

            databaseHelper.addFamily(user.getEmail());

            Toast.makeText(getActivity(),"Такой email есть",Toast.LENGTH_SHORT).show();
            Intent accountsIntent = new Intent(getActivity(), FamilyListActivity.class);
            Toast.makeText(getActivity(), "Add Successful!", Toast.LENGTH_SHORT)
                    .show();
            startActivity(accountsIntent);

        }else if(databaseHelper.checkEmailUser(user.getEmail()) == false){

            Toast.makeText(getActivity(),"Нет такого пользователя",Toast.LENGTH_SHORT).show();

        }else if (databaseHelper.checkEmailFamily(user.getEmail()) == true){

            Toast.makeText(getActivity(),"Этот пользователь есть в списке друзей",Toast.LENGTH_SHORT).show();

        } else if (mFirebaseUser.getEmail().equals(user.getEmail())){

            Toast.makeText(getActivity(),"Вы не можите внести свой аккаунт в список друзей",Toast.LENGTH_SHORT).show();

        }

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        Cursor cursor = database.query(FamilyList.FamilyListEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(FamilyList.FamilyListEntry.COLUMN_ID);
            int nameIndex = cursor.getColumnIndex(FamilyList.FamilyListEntry.COLUMN_NAME);
            int emailIndex = cursor.getColumnIndex(FamilyList.FamilyListEntry.COLUMN_EMAIL);
            int photoIndex = cursor.getColumnIndex(FamilyList.FamilyListEntry.COLUMN_PHOTO);
            int latitide = cursor.getColumnIndex(FamilyList.FamilyListEntry.COLUMN_LATITUDE);
            int longitude = cursor.getColumnIndex(FamilyList.FamilyListEntry.COLUMN_LONGITUDE);
            do {
                Log.d("mLog", "ID = " + cursor.getInt(idIndex) +
                        ", name = " + cursor.getString(nameIndex) +
                        ", email = " + cursor.getString(emailIndex)+
                        ", photo =  " + cursor.getString(photoIndex)+
                        ", latitude = " + cursor.getDouble(latitide)+
                        ", longitude = " + cursor.getDouble(longitude));
            } while (cursor.moveToNext());
        } else
            Log.d("mLog","0 rows");

        cursor.close();

    }

    private void init(){
        friendEmail = view.findViewById(R.id.enter_email_friend);
        mAddFriend = view.findViewById(R.id.addFriendButton);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    private void initObjects() {
        databaseHelper = new DatabaseHelper(getContext());
        user = new User();
    }
}
