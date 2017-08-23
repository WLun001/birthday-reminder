package com.example.weilun.birthdayreminder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Wei Lun on 8/7/2017.
 */

public class DatePickerFragment extends android.support.v4.app.DialogFragment
        implements DatePickerDialog.OnDateSetListener {
//TODO: set max calendar year to current year
    private static Calendar calendar = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (calendar == null)
            calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
        String date = dateFormat.format(calendar.getTime());
        EditText etDate = (EditText) getActivity().findViewById((R.id.birthday_date));
        etDate.setText(date);
    }
}