package com.example.weilun.birthdayreminder;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.weilun.birthdayreminder.db.PersonContract;
import com.example.weilun.birthdayreminder.db.PersonDBHelper;
import com.example.weilun.birthdayreminder.db.PersonDBQueries;

/**
 * A placeholder fragment containing a simple view.
 */
public class UpComingActivityFragment extends Fragment {
    public static final String EXTRA_ID = "com.example.weilun.birthdayreminder.ID";
    private ListView listView;

    public UpComingActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_up_coming, container, false);
        listView =(ListView) rootView.findViewById(R.id.listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), ViewBirthdayActivity.class);
                intent.putExtra(EXTRA_ID, c.getLong(c.getColumnIndex(PersonContract.PersonEntry._ID)));
                startActivity(intent);
            }
        });
        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        PersonDBQueries dbQuery = new PersonDBQueries(new PersonDBHelper(getActivity()));
        String[] columns = {
                PersonContract.PersonEntry._ID,
                PersonContract.PersonEntry.COLUMN_NAME_NAME,
                PersonContract.PersonEntry.COLUMN_NAME_DOB,
                PersonContract.PersonEntry.COLUMN_NAME_IMAGERESOUCEID,

        };
        Cursor cursor = dbQuery.query(columns, null, null, null, null
                , PersonContract.PersonEntry.COLUMN_NAME_DOB + " ASC");
        PersonCursorAdapter adapter = new PersonCursorAdapter(getActivity(), cursor, 0);
        ListView listView =(ListView) getActivity().findViewById(R.id.listview);

        TextView tv = (TextView)getActivity().findViewById(R.id.no_birthday);
        listView.setEmptyView(tv);
        tv.setText(getString(R.string.no_birthday));

        listView.setAdapter(adapter);




    }
}
