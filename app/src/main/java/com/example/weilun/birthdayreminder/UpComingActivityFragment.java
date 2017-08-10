package com.example.weilun.birthdayreminder;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
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
public class UpComingActivityFragment extends Fragment
implements SearchView.OnQueryTextListener,
        SearchView.OnCloseListener,
        LoaderManager.LoaderCallbacks<Cursor>{
    public static final String EXTRA_ID = "com.example.weilun.birthdayreminder.ID";
    private ListView listView;
    private SearchView searchView;
    private String currentFilter;

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
        String[] columns = PersonContract.columns;
        Cursor cursor = dbQuery.query(columns, null, null, null, null
                , PersonContract.PersonEntry.COLUMN_NAME_DOB + " ASC");
        PersonCursorAdapter adapter = new PersonCursorAdapter(getActivity(), cursor, 0);
        ListView listView =(ListView) getActivity().findViewById(R.id.listview);

        TextView tv = (TextView)getActivity().findViewById(R.id.no_birthday);
        listView.setEmptyView(tv);
        tv.setText(getString(R.string.no_birthday));

        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        if(searchView == null)
            return ;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
    }

    @Override
    public boolean onClose() {
        if(!TextUtils.isEmpty(searchView.getQuery())){
            searchView.setQuery(null, true);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String newFilter;
        if(!TextUtils.isEmpty(newText))
            newFilter = newText;
        else
            newFilter = null;

        if(currentFilter == null && newFilter == null)
            return true;
        if(currentFilter != null && currentFilter.equals(newFilter))
            return true;

        currentFilter = newFilter;
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
