package com.example.weilun.birthdayreminder;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.weilun.birthdayreminder.db.PersonContract;
import com.example.weilun.birthdayreminder.db.PersonDBHelper;
import com.example.weilun.birthdayreminder.db.PersonDBQueries;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewBirthdayActivity extends AppCompatActivity {
    private Person person;
    private EditText etName, etEmail, etPhone, etBirthday;
    private Switch aSwitch;
    private ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_birthday);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        long id = intent.getLongExtra(UpComingActivityFragment.EXTRA_ID, 0);

        PersonDBQueries dbQueries = new PersonDBQueries(new PersonDBHelper(getApplicationContext()));

        String[] columns = {
                PersonContract.PersonEntry._ID,
                PersonContract.PersonEntry.COLUMN_NAME_NAME,
                PersonContract.PersonEntry.COLUMN_NAME_EMAIL,
                PersonContract.PersonEntry.COLUMN_NAME_PHONE,
                PersonContract.PersonEntry.COLUMN_NAME_DOB,
                PersonContract.PersonEntry.COLUMN_NAME_NOFITY,
                PersonContract.PersonEntry.COLUMN_NAME_IMAGERESOUCEID,

        };

        String selection =  PersonContract.PersonEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};

        Cursor cursor = dbQueries.query(columns, selection, selectionArgs, null, null, null);
        if(cursor.moveToNext()){
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
        setView();
    }

    private boolean checkBoolean(int value) {
        return value > 0;
    }

    private void setView(){
        icon = (ImageView) findViewById(R.id.icon);
        etName = (EditText) findViewById(R.id.add_birthday_name);
        etEmail = (EditText) findViewById(R.id.add_birthday_email);
        etPhone = (EditText) findViewById(R.id.add_birthday_phone);
        etBirthday = (EditText) findViewById(R.id.birthday_date);
        aSwitch = (Switch) findViewById(R.id.show_noti);

        icon.setImageResource(person.getImageResourceId());
        etName.setText(person.getName());
        etEmail.setText(person.getEmail());
        etPhone.setText(person.getPhone());
        etBirthday.setText(new SimpleDateFormat("EEEE, MMMM d, yyyy").format(person.getDOB()));
        aSwitch.setChecked(person.isNotify());
    }
}
