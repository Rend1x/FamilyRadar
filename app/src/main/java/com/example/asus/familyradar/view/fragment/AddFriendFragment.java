package com.example.asus.familyradar.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.example.asus.familyradar.view.SingInActivity;


public class AddFriendFragment extends Fragment {

    private View view;
    private EditText friendName;
    private EditText friendEmail;

    private Button mAddFriend;

    private User user;
    private DatabaseHelper databaseHelper;

    public AddFriendFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        user.setName(friendName.getText().toString().trim());
        user.setEmail(friendEmail.getText().toString().trim());
        user.setPhoto(String.valueOf(R.drawable.ic_account_circle_black_36dp));
        user.setLatitude(45.111);
        user.setLongitude(33.111);

        databaseHelper.addFamily(user);

        Intent accountsIntent = new Intent(getActivity(), FamilyListActivity.class);
        Toast.makeText(getActivity(), "Add Successful!", Toast.LENGTH_SHORT)
                .show();
        startActivity(accountsIntent);

    }

    private void init(){
        friendName = view.findViewById(R.id.enter_name_friend);
        friendEmail = view.findViewById(R.id.enter_email_friend);
        mAddFriend = view.findViewById(R.id.addFriendButton);
    }

    private void initObjects() {
        databaseHelper = new DatabaseHelper(getContext());
        user = new User();
    }

}
