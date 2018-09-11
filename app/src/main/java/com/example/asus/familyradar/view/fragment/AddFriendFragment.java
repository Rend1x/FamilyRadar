package com.example.asus.familyradar.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.example.asus.familyradar.R;
import com.example.asus.familyradar.model.utils.SQLUtils;

public class AddFriendFragment extends Fragment {

    private final static String TAG = "AddFriendFragment";

    private View view;
    private EditText friendEmail;
    private Button mAddFriend;
    private SQLUtils sqlUtils;

    public AddFriendFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqlUtils = new SQLUtils(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_friend,container,false);
        init();
        mAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlUtils.postDataToSQLite(friendEmail.getText().toString());
            }
        });

        return view;
    }

    private void init(){
        friendEmail = view.findViewById(R.id.enter_email_friend);
        mAddFriend = view.findViewById(R.id.addFriendButton);
        friendEmail.setText(friendEmail.getText().toString().trim());
    }
}
