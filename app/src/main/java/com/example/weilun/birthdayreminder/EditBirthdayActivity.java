package com.example.weilun.birthdayreminder;

import android.content.Intent;
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
import android.widget.Toast;

import com.example.weilun.birthdayreminder.db.PersonDBHelper;
import com.example.weilun.birthdayreminder.db.PersonDBQueries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EditBirthdayActivity extends AppCompatActivity {
    private Person person;
    private ImageView image;
    private EditText etName, etEmail, etPhone, etDob;
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_birthday);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        person = (Person) intent.getSerializableExtra(ViewBirthdayActivity.EXTRA_ID);
        setView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    //TODO: set image resources id
                    person.setImageResourceId(R.drawable.ic_account_circle_black_24dp);
                    person.setName(etName.getText().toString());
                    person.setEmail(etEmail.getText().toString());
                    person.setPhone(etPhone.getText().toString());
                    person.setDob(new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).parse(etDob.getText().toString()));
                    person.setNotify(aSwitch.isChecked());

                    PersonDBQueries dbQueries = new PersonDBQueries(new PersonDBHelper(getApplicationContext()));
                    if (dbQueries.update(person) != 0) {
                        Toast.makeText(EditBirthdayActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    } else
                        Toast.makeText(EditBirthdayActivity.this, "error", Toast.LENGTH_SHORT).show();
                } catch (ParseException e) {
                    Log.v("EditBIrthdayActivity", e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setView() {
        image = (ImageView) findViewById(R.id.icon);
        etName = (EditText) findViewById(R.id.add_birthday_name);
        etEmail = (EditText) findViewById(R.id.add_birthday_email);
        etPhone = (EditText) findViewById(R.id.add_birthday_phone);
        etDob = (EditText) findViewById(R.id.birthday_date);
        aSwitch = (Switch) findViewById(R.id.show_noti);

        image.setImageResource(person.getImageResourceId());
        etName.setText(person.getName());
        etEmail.setText(person.getEmail());
        etPhone.setText(person.getPhone());
        etDob.setText(new SimpleDateFormat("EEEE, MMMM d, yyyy").format(person.getDOB()));
        aSwitch.setChecked(person.isNotify());
    }

    public void showDatePickerDialog(View view) {
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "datePicker");
    }

}
