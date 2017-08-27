package com.example.weilun.birthdayreminder;

import android.content.Intent;
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
import java.util.Locale;

public class EditBirthdayActivity extends AppCompatActivity {
    private static final String LOG_TAG = EditBirthdayActivity.class.getSimpleName();
    private static final int SELECT_IMAGE = 1;
    private Bitmap bitmap = null;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    String name = etName.getText().toString();
                    String email = etEmail.getText().toString();
                    String phone = etPhone.getText().toString();

                    if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || bitmap == null){
                        Toast.makeText(EditBirthdayActivity.this, R.string.warning_message_no_fillup, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //TODO: set image resources id
                        person.setImage(DbBitmapUtility.getBytes(bitmap));
                        person.setName(name);
                        person.setEmail(email);
                        person.setPhone(phone);
                        person.setDob(new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.ENGLISH).parse(etDob.getText().toString()));
                        person.setNotify(aSwitch.isChecked());

                        PersonDBQueries dbQueries = new PersonDBQueries(new PersonDBHelper(getApplicationContext()));
                        if (dbQueries.update(person) != 0) {
                            Toast.makeText(EditBirthdayActivity.this, R.string.updated, Toast.LENGTH_SHORT).show();
                            finish();
                        } else
                            Toast.makeText(EditBirthdayActivity.this, R.string.db_error, Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    Log.v(LOG_TAG, e.getMessage());
                    Toast.makeText(EditBirthdayActivity.this, R.string.warning_message_no_fullup_date, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
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
    protected void onResume() {
        super.onResume();
    }

    /**
     * helper mehtod to setup UI
     */
    private void setView() {
        image = (ImageView) findViewById(R.id.icon);
        etName = (EditText) findViewById(R.id.add_birthday_name);
        etEmail = (EditText) findViewById(R.id.add_birthday_email);
        etPhone = (EditText) findViewById(R.id.add_birthday_phone);
        etDob = (EditText) findViewById(R.id.birthday_date);
        aSwitch = (Switch) findViewById(R.id.show_noti);

        image.setImageBitmap(DbBitmapUtility.getImage(person.getImage()));
        etName.setText(person.getName());
        etEmail.setText(person.getEmail());
        etPhone.setText(person.getPhone());
        etDob.setText(new SimpleDateFormat("EEEE, MMMM d, yyyy").format(person.getDOB()));
        aSwitch.setChecked(person.isNotify());
    }

    /**
     * show dataPicker dialog
     *
     * @param view
     */
    public void showDatePickerDialog(View view) {
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * select image from gallery
     * @param view
     */
    public void selectImage(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getResources().getString(R.string.select_image)), SELECT_IMAGE);
    }
}