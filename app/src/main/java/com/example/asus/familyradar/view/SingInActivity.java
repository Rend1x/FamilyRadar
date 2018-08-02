package com.example.asus.familyradar.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.asus.familyradar.R;

public class SingInActivity extends AppCompatActivity {

    private Button mSingIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);

        mSingIn = (Button) findViewById(R.id.singInButton);

        mSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingInActivity.this,MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
