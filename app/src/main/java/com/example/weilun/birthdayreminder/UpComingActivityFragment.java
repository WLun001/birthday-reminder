package com.example.weilun.birthdayreminder;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
    private PersonCursorAdapter adapter;
    private TextView tv;


    public UpComingActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_up_coming, container, false);
        ListView listView =(ListView) rootView.findViewById(R.id.listview);

        tv = (TextView)rootView.findViewById(R.id.no_birthday);
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
                startActivity(intent);
            }
        });
        setHasOptionsMenu(true);
        //getLoaderManager().restartLoader(SEARCH_LOADER_ID, null, this);
        return rootView;

    }
}
