package com.example.weilun.birthdayreminder;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Wei Lun on 8/7/2017.
 */

public class PersonCursorAdapter extends CursorAdapter {
    private LayoutInflater inflater;

    public PersonCursorAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, flags);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        TextView nameTv = (TextView) view.findViewById(R.id.name);
        TextView birthdayTv = (TextView) view.findViewById(R.id.birthday);
        TextView ageTv = (TextView) view.findViewById(R.id.age);

        int imageResourceId = cursor.getInt(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_IMAGERESOUCEID));
        String name = cursor.getString(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_NAME));
        long dob = cursor.getLong(cursor.getColumnIndex(PersonContract.PersonEntry.COLUMN_NAME_DOB));

        imageView.setImageResource(imageResourceId);
        nameTv.setText(name);
        birthdayTv.setText( new SimpleDateFormat("EEEE, MMM,  d").format(dob));
        ageTv.setText(Integer.toString(getAge(dob)));

    }

    public int getAge(long dob){
        Calendar calendar = Calendar.getInstance();
        Calendar age = Calendar.getInstance();
        age.setTimeInMillis(dob);
        return calendar.get(Calendar.YEAR) - age.get(Calendar.YEAR);
    }
}
