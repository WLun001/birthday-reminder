package com.example.weilun.birthdayreminder.db;

import android.provider.BaseColumns;

/**
 * Created by Wei Lun on 8/7/2017.
 */

public class PersonContract {

    public static String[] columns = {
            PersonContract.PersonEntry._ID,
            PersonContract.PersonEntry.COLUMN_NAME_NAME,
            PersonContract.PersonEntry.COLUMN_NAME_EMAIL,
            PersonContract.PersonEntry.COLUMN_NAME_PHONE,
            PersonContract.PersonEntry.COLUMN_NAME_DOB,
            PersonContract.PersonEntry.COLUMN_NAME_NOFITY,
            PersonContract.PersonEntry.COLUMN_NAME_IMAGERESOUCEID,

    };
    public static class PersonEntry implements BaseColumns{
        public static final String TABLE_NAME = "person";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_DOB = "dob";
        public static final String COLUMN_NAME_NOFITY = "notify";
        public static final String COLUMN_NAME_IMAGERESOUCEID = "imageResourceId";
    }
}
