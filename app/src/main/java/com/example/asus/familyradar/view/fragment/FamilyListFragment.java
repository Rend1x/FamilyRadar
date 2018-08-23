package com.example.asus.familyradar.view.fragment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.familyradar.R;
import com.example.asus.familyradar.model.SQlite.DatabaseHelper;
import com.example.asus.familyradar.model.User;
import com.example.asus.familyradar.model.adapter.FamilyListAdapter;
import com.example.asus.familyradar.view.FamilyListActivity;

import java.util.ArrayList;
import java.util.List;

public class FamilyListFragment extends Fragment{

    private View view;
    private RecyclerView recyclerView;
    private List<User> listUser;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    public FamilyListAdapter familyListAdapter;

    public FamilyListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listUser = new ArrayList<>();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_family_list,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        familyListAdapter = new FamilyListAdapter(getActivity(),listUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(familyListAdapter);
        databaseHelper = new DatabaseHelper(getActivity());
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        getDataFromSQLite();
        return view;
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
