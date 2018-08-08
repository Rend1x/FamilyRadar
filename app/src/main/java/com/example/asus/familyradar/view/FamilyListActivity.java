package com.example.asus.familyradar.view;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.asus.familyradar.R;
import com.example.asus.familyradar.model.FamilyListAdapter;
import com.example.asus.familyradar.model.SQlite.DatabaseHelper;
import com.example.asus.familyradar.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class FamilyListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<User> listUser;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private User user;
    FamilyListAdapter familyListAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        databaseHelper = new DatabaseHelper(this);
        sqLiteDatabase = databaseHelper.getWritableDatabase();

        listUser = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        familyListAdapter = new FamilyListAdapter(getApplicationContext(),listUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(familyListAdapter);

        getDataFromSQLite();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.add_user:
                Toast.makeText(this,"Add users",Toast.LENGTH_LONG).show();
                break;
            case R.id.familyList:
                Intent family = new Intent(this,FamilyListActivity.class);
                startActivity(family);
                break;
            case R.id.logout:
                Intent logout = new Intent(this,SingInActivity.class);
                startActivity(logout);
                break;
            default:
                Intent maps = new Intent(this,MapsActivity.class);
                startActivity(maps);
                break;
        }

        return true;
    }

    private void getDataFromSQLite() {

         new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                listUser.clear();
                listUser.addAll(databaseHelper.getAllBeneficiary());

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                familyListAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

}
