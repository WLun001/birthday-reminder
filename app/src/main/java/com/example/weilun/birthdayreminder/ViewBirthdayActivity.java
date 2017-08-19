package com.example.weilun.birthdayreminder;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.weilun.birthdayreminder.db.PersonContract;
import com.example.weilun.birthdayreminder.db.PersonDBHelper;
import com.example.weilun.birthdayreminder.db.PersonDBQueries;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.weilun.birthdayreminder.db.PersonDBQueries.getPerson;

//TODO : delete birthday, send intent message
public class ViewBirthdayActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;
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
        long idFromTodayBirthdayActivity = intent.getLongExtra(TodayBirthdayActivity.EXTRA_ID, 0);
        if (idFromUpComingFragment != 0) {
            id = idFromUpComingFragment;
            Log.v("VIewAcitivty", "id from upcoming fragment");
        } else if (idFromContactFragment != 0) {
            id = idFromContactFragment;
            Log.v("VIewAcitivty", "id from contact fragment");
        } else {
            id = idFromTodayBirthdayActivity;
            Log.v("VIewAcitivty", "id from TodayBirthdayActivity");
        }

        PersonDBQueries dbQueries = new PersonDBQueries(new PersonDBHelper(getApplicationContext()));
        String[] columns = PersonContract.columns;
        String selection = PersonContract.PersonEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};

        Cursor cursor = dbQueries.query(columns, selection, selectionArgs, null, null, null);
        person = getPerson(cursor);
        setView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_birthday, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_one) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
            builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title)
                    .setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PersonDBQueries dbQueries = new PersonDBQueries(new PersonDBHelper(getApplication()));
                            dbQueries.deleteOne(person.getId());
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
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
        aSwitch.setClickable(false);
        Calendar countdown = Calendar.getInstance();
        countdown.setTimeInMillis(person.getDOBAsCalender().getTimeInMillis());
        countdown.set(Calendar.YEAR, 2017);
        new CountDownTimer(countdown.getTimeInMillis(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                onTickCalculation(millisUntilFinished);
            }

            @Override
            public void onFinish() {

            }
        }.start();
        setTitle(person.getName());
    }

    private void onTickCalculation(long millisUntilFinished) {
        TextView tvDay = (TextView) findViewById(R.id.countdown_day);
        TextView tvHour = (TextView) findViewById(R.id.countdown_hour);
        TextView tvMinute = (TextView) findViewById(R.id.countdown_minute);
        TextView tvSecond = (TextView) findViewById(R.id.countdown_second);

        long beginTime = System.currentTimeMillis();
        beginTime = beginTime - 1;
        long serverUpTimeSeconds = (millisUntilFinished - beginTime) / 1000;

        tvDay.setText(Long.toString(serverUpTimeSeconds / 86400));
        tvHour.setText(Long.toString((serverUpTimeSeconds % 86400) / 3600));
        tvMinute.setText(Long.toString(((serverUpTimeSeconds % 86400) % 3600) / 60));
        tvSecond.setText(Long.toString(((serverUpTimeSeconds % 86400) % 3600) % 60));
    }

    public void sendEmail(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_TEXT, "happy birthday");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void sendSms(View view) {
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
