package com.example.asus.familyradar.model.SQlite;

import android.provider.BaseColumns;

public class UserList {

    public static final class UserListEntry implements BaseColumns {

        public static final String TABLE_NAME = "userlist";
        public static final String COLUMN_USER_ID = "_id";
        public static final String COLUMN_USER_NAME = "name";
        public static final String COLUMN_USER_EMAIL = "email";
        public static final String COLUMN_USER_PHOTO = "photo";
        public static final String COLUMN_USER_LATITUDE = "latitude";
        public static final String COLUMN_USER_LONGITUDE = "longitude";
    }

}
