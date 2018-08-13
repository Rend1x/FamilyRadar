package com.example.asus.familyradar.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;

import com.example.asus.familyradar.R;
import com.example.asus.familyradar.model.SQlite.DatabaseHelper;
import com.example.asus.familyradar.model.SQlite.UserList;
import com.example.asus.familyradar.model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;

public class MapsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapsActivity";
    public static final String ANONYMOUS = "anonymous";


    private GoogleMap mMap;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private GoogleApiClient mGoogleApiClient;
    private String mPhotoUrl;
    private LocationManager locationManager;
    private SupportMapFragment mapFragment;
    private double Latitude;
    private double Longitude;
    private Toolbar toolbar;

    private DatabaseHelper databaseHelper;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        init();
        initSQl();
        isSignedIn();

    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        setSupportActionBar(toolbar);
        setUpMapIfNeeded();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        firebaseAuth = FirebaseAuth.getInstance();
        mUsername = ANONYMOUS;
        mFirebaseUser = firebaseAuth.getCurrentUser();
    }

    private void initSQl(){
        databaseHelper = new DatabaseHelper(this);
        user = new User();
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
                postDataToDataBase();
                break;
            case R.id.refresh:
                updateDataToSql();
                break;
            case R.id.familyList:
                Intent family = new Intent(this,FamilyListActivity.class);
                startActivity(family);
                break;
            case R.id.logout:
                firebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SingInActivity.class));
                finish();
            default:
                break;
        }

        return true;
    }

    private void delete() {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        database.delete(UserList.UserListEntry.TABLE_NAME, null, null);

    }

    private void updateDataToSql() {

        user.setLatitude(Latitude);
        user.setLongitude(Longitude);

        databaseHelper.updateUser(user);

        Toast.makeText(this, "Refresh Successful!", Toast.LENGTH_SHORT).show();

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        Cursor cursor = database.query(UserList.UserListEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(UserList.UserListEntry.COLUMN_USER_ID);
            int nameIndex = cursor.getColumnIndex(UserList.UserListEntry.COLUMN_USER_NAME);
            int emailIndex = cursor.getColumnIndex(UserList.UserListEntry.COLUMN_USER_EMAIL);
            int photoIndex = cursor.getColumnIndex(UserList.UserListEntry.COLUMN_USER_PHOTO);
            int latitide = cursor.getColumnIndex(UserList.UserListEntry.COLUMN_USER_LATITUDE);
            int longitude = cursor.getColumnIndex(UserList.UserListEntry.COLUMN_USER_LONGITUDE);
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

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                }
            });
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
            Log.d(TAG,"Координаты " + location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            /*if (provider.equals(LocationManager.GPS_PROVIDER)) {
                tvStatusGPS.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                tvStatusNet.setText("Status: " + String.valueOf(status));
            }*/
        }
    };

    private void checkEnabled() {
        //tvEnabledGPS.setText("Enabled: " + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        //tvEnabledNet.setText("Enabled: " + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            //tvLocationGPS.setText(formatLocation(location));
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();
        } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            //tvLocationNet.setText(formatLocation(location));
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();

        }
    }

    @SuppressLint("MissingPermission")
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        LatLng coordinate = new LatLng(Latitude, Longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(coordinate).title("Test");
        mMap.addMarker(new MarkerOptions().position(new LatLng(Latitude, Longitude)).title("Test2"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 0));
    }

    private void postDataToDataBase(){

        user.setName(mFirebaseUser.getDisplayName());
        user.setEmail(mFirebaseUser.getEmail());
        user.setPhoto(mFirebaseUser.getPhotoUrl().toString());
        user.setLatitude(Latitude);
        user.setLongitude(Longitude);

        databaseHelper.addUser(user);

        Toast.makeText(this, "Refresh Successful!", Toast.LENGTH_SHORT).show();

        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        Cursor cursor = database.query(UserList.UserListEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(UserList.UserListEntry.COLUMN_USER_ID);
            int nameIndex = cursor.getColumnIndex(UserList.UserListEntry.COLUMN_USER_NAME);
            int emailIndex = cursor.getColumnIndex(UserList.UserListEntry.COLUMN_USER_EMAIL);
            int photoIndex = cursor.getColumnIndex(UserList.UserListEntry.COLUMN_USER_PHOTO);
            int latitide = cursor.getColumnIndex(UserList.UserListEntry.COLUMN_USER_LATITUDE);
            int longitude = cursor.getColumnIndex(UserList.UserListEntry.COLUMN_USER_LONGITUDE);
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


    private void isSignedIn(){

        if (mFirebaseUser == null) {
            startActivity(new Intent(this, SingInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
