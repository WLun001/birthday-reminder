package com.example.weilun.birthdayreminder.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.weilun.birthdayreminder.Person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Wei Lun on 8/7/2017.
 */

public class PersonDBQueries {

    private PersonDBHelper helper;

    public PersonDBQueries(PersonDBHelper helper) {
        this.helper = helper;
    }

    public static Person getPerson(Cursor cursor) {
        Person person = null;
        if (cursor.moveToNext()) {
            person = new Person(
                    cursor.getString(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_NAME)),
                    cursor.getString(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_PHONE)),
                    new Date(cursor.getLong(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_DOB))),
                    checkBoolean(cursor.getInt(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_NOFITY))),
                    cursor.getInt(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_IMAGERESOUCEID))
            );
            person.setId(cursor.getLong(cursor.getColumnIndex(PersonContract.PersonEntry._ID)));
        }
        return person;
    }

    public static List<Person> getPersonList(Cursor cursor){
        List<Person> persons = new ArrayList<>();
        while (cursor.moveToNext()) {
            Person person = new Person(
                    cursor.getString(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_NAME)),
                    cursor.getString(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_PHONE)),
                    new Date(cursor.getLong(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_DOB))),
                    PersonDBQueries.checkBoolean(cursor.getInt(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_NOFITY))),
                    cursor.getInt(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_IMAGERESOUCEID))
            );
            person.setId(cursor.getLong(cursor.getColumnIndex(PersonContract.PersonEntry._ID)));
            persons.add(person);
        }

        return persons;
    }


    private  boolean checkBoolean(int value) {
        return value > 0;
    }

    public Cursor query(String[] columns, String selection, String[] selectionArgs, String groupBy
            , String having, String orderBy) {
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(PersonContract.PersonEntry.TABLE_NAME, columns, selection, selectionArgs, groupBy
                , having, orderBy);
    }

    public long insert(Person person) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = putValues(person);
        long id = db.insert(PersonContract.PersonEntry.TABLE_NAME, null, values);
        person.setId(id);

        return id;
    }

    public int update(Person person) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = putValues(person);
        String selection = PersonContract.PersonEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(person.getId())};

        return db.update(PersonContract.PersonEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public void deleteOne(Long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String selection = PersonContract.PersonEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};

        db.delete(PersonContract.PersonEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void deleteAll() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(PersonContract.PersonEntry.TABLE_NAME, null, null);
    }

    private ContentValues putValues(Person person){
        ContentValues values = new ContentValues();
        values.put(PersonContract.PersonEntry.COLUMN_NAME_NAME, person.getName());
        values.put(PersonContract.PersonEntry.COLUMN_NAME_EMAIL, person.getEmail());
        values.put(PersonContract.PersonEntry.COLUMN_NAME_PHONE, person.getPhone());
        values.put(PersonContract.PersonEntry.COLUMN_NAME_DOB, person.getDOBAsCalender().getTimeInMillis());
        values.put(PersonContract.PersonEntry.COLUMN_NAME_NOFITY, person.isNotify());
        values.put(PersonContract.PersonEntry.COLUMN_NAME_IMAGERESOUCEID, person.getImageResourceId());

        return values;
    }


}
