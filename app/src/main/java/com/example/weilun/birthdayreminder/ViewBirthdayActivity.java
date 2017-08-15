package com.example.weilun.birthdayreminder;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.weilun.birthdayreminder.db.PersonContract;
import com.example.weilun.birthdayreminder.db.PersonDBHelper;
import com.example.weilun.birthdayreminder.db.PersonDBQueries;

import java.text.SimpleDateFormat;

import static com.example.weilun.birthdayreminder.db.PersonDBQueries.getPerson;

//TODO : delete birthday, send intent message
public class ViewBirthdayActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.example.weilun.birthdayreminder.ID";
    private Person person;
    private TextView tvName, tvEmail, tvPhone, tvBirthday;
    private ImageButton sendMessage, sendEmail;
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
                if (intent.resolveActivity(getPackageManager()) != null)
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

    private void setView() {
        icon = (ImageView) findViewById(R.id.icon);
        tvName = (TextView) findViewById(R.id.name);
        tvEmail = (TextView) findViewById(R.id.email);
        tvPhone = (TextView) findViewById(R.id.phone);
        tvBirthday = (TextView) findViewById(R.id.birthday);
        aSwitch = (Switch) findViewById(R.id.show_noti);

        icon.setImageResource(person.getImageResourceId());
        tvName.setText(person.getName());
        tvEmail.setText(person.getEmail());
        tvPhone.setText(person.getPhone());
        tvBirthday.setText(new SimpleDateFormat("EEEE, MMMM d, yyyy").format(person.getDOB()));
        aSwitch.setChecked(person.isNotify());

        setTitle(person.getName());

    }

    public void sendEmail(View view){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_TEXT, "happy birthday");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    public void sendSms(View view){

//        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
//        smsIntent.setType("vnd.android-dir/mms-sms");
//        smsIntent.putExtra("address", person.getPhone());
//        //TODO: birthday wish
//        smsIntent.putExtra("sms_body", "happy birthday");
//        startActivity(smsIntent);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "happy birthday");
        intent.putExtra(Intent.EXTRA_PHONE_NUMBER, person.getPhone());
        intent.setType("text/plain");
        startActivity(intent);
    }
}
