package com.example.weilun.birthdayreminder;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.weilun.birthdayreminder.db.PersonContract;
import com.example.weilun.birthdayreminder.db.PersonDBHelper;
import com.example.weilun.birthdayreminder.db.PersonDBQueries;

import java.text.SimpleDateFormat;

import static com.example.weilun.birthdayreminder.db.PersonDBQueries.getPerson;

//TODO : delete birthday, send intent message
public class ViewBirthdayActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.example.weilun.birthdayreminder.ID";
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
                Intent intent = new Intent(ViewBirthdayActivity.this, EditBirthdayActivity.class);
                intent.putExtra(EXTRA_ID, person);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();

        long id = 0;
        long idFromUpComingFragment = intent.getLongExtra(UpComingBirthdayFragment.EXTRA_ID, 0);
        long idFromContactFragment = intent.getLongExtra(ContactListFragment.EXTRA_ID, 0);
        if (idFromUpComingFragment != 0) {
            id = idFromUpComingFragment;
            Log.v("VIewAcitivty", "id from upcoming fragment");
        } else {
            id = idFromContactFragment;
            Log.v("VIewAcitivty", "id from contact fragment");
        }

        PersonDBQueries dbQueries = new PersonDBQueries(new PersonDBHelper(getApplicationContext()));
        String[] columns = PersonContract.columns;
        String selection = PersonContract.PersonEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};

        Cursor cursor = dbQueries.query(columns, selection, selectionArgs, null, null, null);
        person = getPerson(cursor);
        setView();
    }

    public void showDatePickerDialog(View view) {
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void setView() {
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

        setTitle(person.getName());

    }
}
