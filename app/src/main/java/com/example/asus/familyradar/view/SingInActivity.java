package com.example.asus.familyradar.view;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.familyradar.R;
import com.example.asus.familyradar.model.SQlite.DatabaseHelper;
import com.example.asus.familyradar.model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.example.asus.familyradar.view.MapsActivity.ANONYMOUS;

public class SingInActivity extends AppCompatActivity  implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private static final String TAG = "SingInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private static GoogleSignInOptions googleSignInOptions;

    private FirebaseAuth mFirebaseAuth;

    private User user;
    private DatabaseHelper databaseHelper;

    private EditText yourName;
    private EditText yourEmail;

    private Button mSingIn;
    private Button singInGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);


        yourName = (EditText) findViewById(R.id.enter_name);
        yourEmail = (EditText) findViewById(R.id.enter_email);
        singInGoogle = (Button) findViewById(R.id.login_button_google);
        mSingIn = (Button) findViewById(R.id.singInButton);

        init();
        initObjects();


        mSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDataToSQLite();
            }
        });

        singInGoogle.setOnClickListener(this);


    }

    private void postDataToSQLite() {

        user.setName(yourName.getText().toString().trim());
        user.setEmail(yourEmail.getText().toString().trim());

        databaseHelper.addUser(user);

        Intent accountsIntent = new Intent(this, FamilyListActivity.class);
        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT)
                .show();

        accountsIntent.putExtra("NAME", yourName.getText().toString().trim());
        accountsIntent.putExtra("EMAIL", yourEmail.getText().toString().trim());

        startActivity(accountsIntent);

    }

    private void initObjects() {
        databaseHelper = new DatabaseHelper(SingInActivity.this);
        user = new User();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button_google:
                signIn();
                break;
            default:
                return;
        }
    }

    private void init(){
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.e(TAG, "Ошибка при авторизации.");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(SingInActivity.this, "Ошибка авторизации.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(SingInActivity.this, MapsActivity.class));
                            finish();
                        }
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
