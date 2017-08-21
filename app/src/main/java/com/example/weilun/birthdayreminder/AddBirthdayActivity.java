package com.example.weilun.birthdayreminder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.weilun.birthdayreminder.db.PersonDBHelper;
import com.example.weilun.birthdayreminder.db.PersonDBQueries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddBirthdayActivity extends AppCompatActivity {
    private ImageView image;
    private EditText etName, etEmail, etPhone, etDob;
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_birthday);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        image = (ImageView) findViewById(R.id.icon);
        etName = (EditText) findViewById(R.id.add_birthday_name);
        etEmail = (EditText) findViewById(R.id.add_birthday_email);
        etPhone = (EditText) findViewById(R.id.add_birthday_phone);
        etDob = (EditText) findViewById(R.id.birthday_date);
        aSwitch = (Switch) findViewById(R.id.show_noti);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //TODO ; set iamge reources id
                    int imageResourceId = R.drawable.ic_account_circle_black_24dp;
                    String name = etName.getText().toString();
                    String email = etEmail.getText().toString();
                    String phone = etPhone.getText().toString();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH);
                    Date date = dateFormat.parse(etDob.getText().toString());
                    Boolean isChecked = aSwitch.isChecked();

                    PersonDBQueries dbQueries = new PersonDBQueries(new PersonDBHelper(getApplicationContext()));
                    Person person = new Person(name, email, phone, date, isChecked, imageResourceId);
                    if (dbQueries.insert(person) != 0) {
                        Toast.makeText(AddBirthdayActivity.this, "inserted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (ParseException e) {
                    Toast.makeText(AddBirthdayActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showDatePickerDialog(View view) {
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "datePicker");
    }
}
