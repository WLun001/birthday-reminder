package com.example.weilun.birthdayreminder.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Wei Lun on 8/7/2017.
 */

public class PersonDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "person.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PersonContract.PersonEntry.TABLE_NAME + " (" +
                    PersonContract.PersonEntry._ID + " INTEGER PRIMARY KEY," +
                    PersonContract.PersonEntry.COLUMN_NAME_NAME + " TEXT," +
                    PersonContract.PersonEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    PersonContract.PersonEntry.COLUMN_NAME_PHONE + " TEXT," +
                    PersonContract.PersonEntry.COLUMN_NAME_DOB + " INTEGER," +
                    PersonContract.PersonEntry.COLUMN_NAME_NOFITY + " INTEGER," +
                    PersonContract.PersonEntry.COLUMN_NAME_IMAGERESOUCEID + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS" + PersonContract.PersonEntry.TABLE_NAME;

    public PersonDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
