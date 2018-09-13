package com.example.asus.familyradar.view.fragment;

import android.content.Intent;
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
import com.example.asus.familyradar.model.utils.SQLUtils;
import com.example.asus.familyradar.view.FamilyListActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AddFriendFragment extends Fragment {

    private final static String TAG = "AddFriendFragment";

    private View view;
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
        mAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDataToSQLite(friendEmail.getText().toString());
            }
        });

        return view;
    }

    private void init(){
        friendEmail = view.findViewById(R.id.enter_email_friend);
        mAddFriend = view.findViewById(R.id.addFriendButton);
        friendEmail.setText(friendEmail.getText().toString().trim());
        user = new User();
        databaseHelper = new DatabaseHelper(getContext());
    }

    public void postDataToSQLite(String email) {

        user.setEmail(email);

        if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(user.getEmail())){

            Toast.makeText(getContext(),R.string.add_error_1,Toast.LENGTH_SHORT).show();

        } else if(!databaseHelper.checkEmailUser(user.getEmail())){

            Toast.makeText(getContext(),R.string.add_error_2,Toast.LENGTH_SHORT).show();

        } else if (databaseHelper.checkEmailFamily(user.getEmail())){

            Toast.makeText(getContext(),R.string.add_error_3,Toast.LENGTH_SHORT).show();

        } else if (databaseHelper.checkEmailUser(user.getEmail())&& !databaseHelper.checkEmailFamily(user.getEmail())){

            databaseHelper.addFamily(user.getEmail());

            Toast.makeText(getContext(), R.string.add_successful,Toast.LENGTH_SHORT).show();
            Intent accountsIntent = new Intent(getContext(), FamilyListActivity.class);
            startActivity(accountsIntent);

        }
    }
}
