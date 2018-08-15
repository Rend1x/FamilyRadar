package com.example.asus.familyradar.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity
        extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        LocationListener{

    private static final String TAG = "MapsActivity";
    public static final String ANONYMOUS = "anonymous";
    private final static int PERMISSION_ALL = 1;
    private final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};


    private GoogleMap mMap;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser mFirebaseUser;

    private String mUsername;

    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private SupportMapFragment mapFragment;
    private double latitude;
    private double longitude;
    private MarkerOptions markerOptions;
    private Marker marker;

    private List<LatLng> familyPlace;

    private String mPhotoUrl;

    private Toolbar toolbar;

    private DatabaseHelper databaseHelper;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        isSignedIn();
        init();
        initSQl();


    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        setSupportActionBar(toolbar);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    }

    private void initSQl(){
        familyPlace = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);
        user = new User();
        familyPlace.addAll(databaseHelper.getFamilyPlace());
        setUpMapIfNeeded();
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

        user.setLatitude(latitude);
        user.setLongitude(longitude);

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
    }

    private void setUpMapIfNeeded() {
       mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
       mapFragment.getMapAsync(this);
       locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
       markerOptions = new MarkerOptions().position(new LatLng(latitude,longitude)).title("You");
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        } else requestLocation();
        if (!isLocationEnabled())
            showAlert(1);
    }

    @SuppressLint("MissingPermission")
    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(provider, 10000, 10, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void postDataToDataBase(){

        user.setName(mFirebaseUser.getDisplayName());
        user.setEmail(mFirebaseUser.getEmail());
        user.setPhoto(mFirebaseUser.getPhotoUrl().toString());
        user.setLatitude(latitude);
        user.setLongitude(longitude);

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

        firebaseAuth = FirebaseAuth.getInstance();
        mUsername = ANONYMOUS;
        mFirebaseUser = firebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            startActivity(new Intent(this, SingInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        marker = mMap.addMarker(markerOptions);

        MarkerOptions[] markerOptions = new MarkerOptions[familyPlace.size()];
        for (int i = 0; i < familyPlace.size(); i++){
            markerOptions[i] = new MarkerOptions().position(familyPlace.get(i));
            mMap.addMarker(markerOptions[i]);

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        marker.setPosition(myCoordinates);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isPermissionGranted() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("mylog", "Permission is granted");
            return true;
        } else {
            Log.v("mylog", "Permission not granted");
            return false;
        }
    }
    private void showAlert(final int status) {
        String message, title, btnText;
        if (status == 1) {
            message = "Your Locations Settings is set to 'Off'.\nPlease Enable Location to use this app";
            title = "Enable Location";
            btnText = "Location Settings";
        } else {
            message = "Please allow this app to access location!";
            title = "Permission access";
            btnText = "Grant";
        }

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        if (status == 1) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        } else
                            requestPermissions(PERMISSIONS, PERMISSION_ALL);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    public boolean isLocationEnabled() {
        return  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}
