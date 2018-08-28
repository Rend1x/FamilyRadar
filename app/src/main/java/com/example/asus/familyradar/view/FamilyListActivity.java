package com.example.asus.familyradar.view;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.familyradar.R;
import com.example.asus.familyradar.model.SQlite.UserList;
import com.example.asus.familyradar.model.adapter.FamilyListAdapter;
import com.example.asus.familyradar.model.SQlite.DatabaseHelper;
import com.example.asus.familyradar.model.User;
import com.example.asus.familyradar.model.adapter.ViewPageAdapter;
import com.example.asus.familyradar.view.fragment.AddFriendFragment;
import com.example.asus.familyradar.view.fragment.FamilyListFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class FamilyListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter viewPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_list);

        initToolbar();
        initTabBar();

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

    private void initToolbar(){

        toolbar = (Toolbar) findViewById(R.id.family_toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void initTabBar(){

        tabLayout  = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPage);
        viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        viewPageAdapter.AddFragment(new FamilyListFragment(),"");
        viewPageAdapter.AddFragment(new AddFriendFragment(),"");

        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_format_list);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_add);


    }

}
