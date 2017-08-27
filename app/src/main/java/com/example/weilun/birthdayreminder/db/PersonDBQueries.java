package com.example.weilun.birthdayreminder.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.weilun.birthdayreminder.Person;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Wei Lun on 8/7/2017.
 * A class that contains all of the database operations
 */

public class PersonDBQueries {

    private PersonDBHelper helper;

    public PersonDBQueries(PersonDBHelper helper) {
        this.helper = helper;
    }

    /**
     * get a {@link Person} object from cursor
     *
     * @param cursor cursor
     * @return {@link Person}
     */
    public static Person getPerson(Cursor cursor) {
        Person person = null;
        if (cursor.moveToNext()) {
            person = setPerson(cursor);
        }
        return person;
    }

    /**
     * get a List<Person> from cursor
     *
     * @param cursor
     * @return List<Person>
     */
    public static List<Person> getPersonList(Cursor cursor) {
        List<Person> persons = new ArrayList<>();
        while (cursor.moveToNext()) {
            Person person = setPerson(cursor);
            persons.add(person);
        }
        return persons;
    }

    /**
     * set {@link Person} object from a cursor
     *
     * @param cursor
     * @return {@link Person}
     */
    private static Person setPerson(Cursor cursor) {
        Person person = new Person(
                cursor.getString(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_NAME)),
                cursor.getString(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_EMAIL)),
                cursor.getString(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_PHONE)),
                new Date(cursor.getLong(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_DOB))),
                checkBoolean(cursor.getInt(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_NOFITY))),
                cursor.getBlob(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_IMAGE))
        );
        person.setId(cursor.getLong(cursor.getColumnIndex(PersonContract.PersonEntry._ID)));

        return person;
    }

    /**
     * helper method to convert integer to boolean
     *
     * @param value
     * @return true of is boolean
     */
    private static boolean checkBoolean(int value) {
        return value > 0;
    }

    /**
     * Qeury database
     *
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return cursor
     */
    public Cursor query(String[] columns, String selection, String[] selectionArgs, String groupBy
            , String having, String orderBy) {
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(PersonContract.PersonEntry.TABLE_NAME, columns, selection, selectionArgs, groupBy
                , having, orderBy);
    }

    /**
     * Query today birthdays
     *
     * @param calender the date of today
     * @return cursor of today birthdays
     */
    public Cursor queryTodayBirthday(Calendar calender) {
        String[] columns = PersonContract.columns;
        String[] selectionArgs = {calender.getTimeInMillis() + "", "" + calender.getTimeInMillis()};

        //to convert millisecond to Unix timestamp, divide by 1000
        return query(columns, "strftime('%m-%d'," + PersonContract.PersonEntry.COLUMN_NAME_DOB + "/1000, 'unixepoch', 'localtime')"
                        + " BETWEEN strftime('%m-%d',?/1000, 'unixepoch', 'localtime') AND strftime('%m-%d',?/1000, 'unixepoch', 'localtime')"
                        + "AND " + PersonContract.PersonEntry.COLUMN_NAME_NOFITY + " = '1'",
                selectionArgs, null, null, null);
    }

    /**
     * insert specify column into database
     *
     * @param person
     * @return column id
     */
    public long insert(Person person) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = putValues(person);
        long id = db.insert(PersonContract.PersonEntry.TABLE_NAME, null, values);
        person.setId(id);

        return id;
    }

    /**
     * update specific column into database
     *
     * @param person
     * @return numbers of the rows affected
     */
    public int update(Person person) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = putValues(person);
        String selection = PersonContract.PersonEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(person.getId())};

        return db.update(PersonContract.PersonEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    /**
     * delete one column from database
     *
     * @param id of the desired column
     */
    public void deleteOne(Long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String selection = PersonContract.PersonEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};

        db.delete(PersonContract.PersonEntry.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * delete all records from database
     */
    public void deleteAll() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(PersonContract.PersonEntry.TABLE_NAME, null, null);
    }

    /**
     * helper method to insert value from person
     *
     * @param person
     * @return {@link ContentValues}
     */
    private ContentValues putValues(Person person) {
        ContentValues values = new ContentValues();
        values.put(PersonContract.PersonEntry.COLUMN_NAME_NAME, person.getName());
        values.put(PersonContract.PersonEntry.COLUMN_NAME_EMAIL, person.getEmail());
        values.put(PersonContract.PersonEntry.COLUMN_NAME_PHONE, person.getPhone());
        values.put(PersonContract.PersonEntry.COLUMN_NAME_DOB, person.getDOBAsCalender().getTimeInMillis());
        values.put(PersonContract.PersonEntry.COLUMN_NAME_NOFITY, person.isNotify());
        values.put(PersonContract.PersonEntry.COLUMN_NAME_IMAGE, person.getImage());

        return values;
    }
}