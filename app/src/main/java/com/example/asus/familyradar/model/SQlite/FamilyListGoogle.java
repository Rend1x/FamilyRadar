package com.example.asus.familyradar.model.SQlite;

import android.provider.BaseColumns;

public class FamilyListGoogle {

    public static final class FamilyListEntry implements BaseColumns {

        public static final String TABLE_NAME = "familylist";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
    }
}
