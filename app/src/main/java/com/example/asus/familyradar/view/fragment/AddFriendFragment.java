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
import com.example.asus.familyradar.model.User;
import com.example.asus.familyradar.view.FamilyListActivity;
import com.example.asus.familyradar.model.SQlite.FamilyList.FamilyListEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class AddFriendFragment extends Fragment {

    private final static String TAG = "AddFriendFragment";

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

        return view;
    }

    private void postDataToSQLite() {

        user.setEmail(friendEmail.getText().toString().trim());

        Log.d(TAG,"Email " + user.getEmail());

        if (databaseHelper.checkEmailUser(user.getEmail())&& !databaseHelper.checkEmailFamily(user.getEmail())){

            databaseHelper.addFamily(user.getEmail());

            Toast.makeText(getActivity(),"Такой email есть",Toast.LENGTH_SHORT).show();
            Intent accountsIntent = new Intent(getActivity(), FamilyListActivity.class);
            Toast.makeText(getActivity(), "Add Successful!", Toast.LENGTH_SHORT)
                    .show();
            startActivity(accountsIntent);

        }else if(!databaseHelper.checkEmailUser(user.getEmail())){

            Toast.makeText(getActivity(),"Нет такого пользователя",Toast.LENGTH_SHORT).show();

        }else if (databaseHelper.checkEmailFamily(user.getEmail())){

            Toast.makeText(getActivity(),"Этот пользователь есть в списке друзей",Toast.LENGTH_SHORT).show();

        } else if (mFirebaseUser.getEmail().equals(user.getEmail())){

            Toast.makeText(getActivity(),"Вы не можите внести свой аккаунт в список друзей",Toast.LENGTH_SHORT).show();

        }
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

    private void delete() {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.delete(FamilyListEntry.TABLE_NAME, null, null);

    }

}
