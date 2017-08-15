package com.example.weilun.birthdayreminder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.weilun.birthdayreminder.db.PersonContract;
import com.example.weilun.birthdayreminder.db.PersonDBHelper;
import com.example.weilun.birthdayreminder.db.PersonDBQueries;

import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class UpComingBirthdayFragment extends Fragment {
    public static final String EXTRA_ID = "com.example.weilun.birthdayreminder.ID";
    private static final String LOG_TAG = "UpComingBirthdayFragment";
    private static Calendar startDate, endDate, dob;
    private PersonCursorAdapter adapter;
    private TextView tv;
    private Countable countable;

    public UpComingBirthdayFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            countable = (Countable) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("must implement Countable interface");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_up_coming, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview);

        tv = (TextView) rootView.findViewById(R.id.no_birthday);
        listView.setEmptyView(tv);
        adapter = new PersonCursorAdapter(getActivity(), null, 0);
        tv.setText(getString(R.string.no_birthday_for_upcoming));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), ViewBirthdayActivity.class);
                intent.putExtra(EXTRA_ID, c.getLong(c.getColumnIndex(PersonContract.PersonEntry._ID)));
                if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                startActivity(intent);
            }
        });
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_MONTH, 2);

        PersonDBQueries dbQuery = new PersonDBQueries(new PersonDBHelper(getActivity()));
        String[] columns = PersonContract.columns;
        String[] selectionArgs = {startDate.getTimeInMillis() + "", "" + endDate.getTimeInMillis()};
        Cursor cursor = dbQuery.query(columns, "strftime('%m-%d'," + PersonContract.PersonEntry.COLUMN_NAME_DOB + "/1000, 'unixepoch')"
                        + " BETWEEN strftime('%m-%d',?/1000, 'unixepoch') AND strftime('%m-%d',?/1000, 'unixepoch')",
                selectionArgs, null, null, null);

        countable.getCount(cursor.getCount());
        onRefresh(cursor);
    }

    public void onRefresh(Cursor cursor) {
        adapter.swapCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    public interface Countable {
        void getCount(int count);
    }
}
