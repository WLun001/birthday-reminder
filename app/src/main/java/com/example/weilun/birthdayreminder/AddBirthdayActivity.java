package com.example.weilun.birthdayreminder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.weilun.birthdayreminder.db.DbBitmapUtility;
import com.example.weilun.birthdayreminder.db.PersonDBHelper;
import com.example.weilun.birthdayreminder.db.PersonDBQueries;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddBirthdayActivity extends AppCompatActivity {
    private static final String LOG_TAG = AddBirthdayActivity.class.getSimpleName();
    private static final int SELECT_IMAGE = 1;
    private ImageView image;
    private EditText etName, etEmail, etPhone, etDob;
    private Switch aSwitch;
    private Bitmap bitmap = null;
    private boolean saved = false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_birthday);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences("AddSavingState", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

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
                    String name = etName.getText().toString();
                    String email = etEmail.getText().toString();
                    String phone = etPhone.getText().toString();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH);
                    Date date = dateFormat.parse(etDob.getText().toString());
                    Boolean isChecked = aSwitch.isChecked();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || bitmap == null) {
                        Toast.makeText(AddBirthdayActivity.this, R.string.warning_message_no_fillup, Toast.LENGTH_SHORT).show();
                    } else {
                        PersonDBQueries dbQueries = new PersonDBQueries(new PersonDBHelper(getApplicationContext()));
                        Person person = new Person(name, email, phone, date, isChecked, DbBitmapUtility.getBytes(bitmap));
                        if (dbQueries.insert(person) != 0) {
                            saved = true;
                            Toast.makeText(AddBirthdayActivity.this, R.string.inserted, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                } catch (ParseException e) {
                    Log.e(LOG_TAG, e.getMessage());
                    Toast.makeText(AddBirthdayActivity.this, R.string.warning_message_no_fullup_date, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {
                if (data != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    } catch (IOException ex) {
                        Log.wtf("IOException", ex);
                    }
                }
                image.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (saved) {
            editor.clear();
        } else {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();
            String birthday = etDob.getText().toString();

            editor.putString("SAVE_STATE_NAME", name);
            editor.putString("SAVE_STATE_EMAIL", email);
            editor.putString("SAVE_STATE_PHONE", phone);
            editor.putString("SAVE_STATE_DOB", birthday);
        }
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        String name = sharedPreferences.getString("SAVE_STATE_NAME", "");
        String email = sharedPreferences.getString("SAVE_STATE_EMAIL", "");
        String phone = sharedPreferences.getString("SAVE_STATE_PHONE", "");
        String birthday = sharedPreferences.getString("SAVE_STATE_DOB", "");

        etName.setText(name);
        etEmail.setText(email);
        etPhone.setText(phone);
        etDob.setText(birthday);
    }

    /**
     * show dataPickerDialog
     *
     * @param view
     */
    public void showDatePickerDialog(View view) {
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * select image from gallery
     *
     * @param view
     */
    public void selectImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image)), SELECT_IMAGE);
    }
}