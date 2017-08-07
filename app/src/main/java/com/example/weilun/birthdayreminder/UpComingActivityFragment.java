package com.example.weilun.birthdayreminder;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class UpComingActivityFragment extends Fragment {

    public UpComingActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      return  inflater.inflate(R.layout.fragment_up_coming, container, false);

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
        listView.setAdapter(adapter);
        listView.setEmptyView(getActivity().findViewById(R.id.no_birthday));
    }
}
