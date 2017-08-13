package com.example.weilun.birthdayreminder;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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
public class ContactActivityFragment extends Fragment
implements SearchView.OnQueryTextListener,
        SearchView.OnCloseListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final String EXTRA_ID = "com.example.weilun.birthdayreminder.ID";
    public static final int SEARCH_LOADER_ID = 1;
    private PersonCursorAdapter adapter;
    private SearchView searchView;
    private String searchKeyword = null;
    private TextView tv;

    public ContactActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_contact, container, false);

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

    @Override
    public void onResume() {
        super.onResume();
        Log.v("onResume", "Creating Loader");
        getLoaderManager().restartLoader(SEARCH_LOADER_ID, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        if(searchView == null)
            return;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
    }

    @Override
    public boolean onClose() {
        searchKeyword = null;
        Log.v("onClose", "Restarting Loader");
        getLoaderManager().restartLoader(SEARCH_LOADER_ID, null, this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if(!TextUtils.isEmpty(query)){
            searchKeyword = query;
            Log.v("onQueryTextSubmit", "Restarting Loader");
            getLoaderManager().restartLoader(SEARCH_LOADER_ID, null, this);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v("onCreateLoader","creating loader" );
        return new SearchLoader(getActivity(), searchKeyword);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v("onLoadFinish", Integer.toString(data.getColumnCount()));
        adapter.swapCursor(data);
        tv.setVisibility(View.GONE);
        Log.v("onLoadFinish", "Swapping adapter");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v("onLoadReset","resetting loader" );
        adapter.swapCursor(null);
    }


    public static final class SearchLoader extends AsyncTaskLoader<Cursor> {
        private String keyword;
        private Context context;

        public SearchLoader(Context context, String keyword){
            super(context);
            Log.v("SearchLoader","SearchLoader instantiated" );
            this.context = context;
            this.keyword = keyword;
            Log.v("SearchLoader", "Keyword value: " + keyword);
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Override
        public Cursor loadInBackground() {
            PersonDBQueries dbQuery = new PersonDBQueries(new PersonDBHelper(context));
            Cursor cursor;
            String[] columns = PersonContract.columns;
            if(keyword != null) {
                Log.v("loadInBackground", "LIKE QUERY");
                String[] selectionArgs = {"%" + keyword + "%"};
                cursor = dbQuery.query(columns, PersonContract.PersonEntry.COLUMN_NAME_NAME + " LIKE ?"
                        , selectionArgs, null, null
                        , null);
            }
            else {
                Log.v("loadInBackgrond","query in background" );
                cursor = dbQuery.query(columns, null, null, null, null
                        , PersonContract.PersonEntry.COLUMN_NAME_NAME + " ASC");

            }
//            if(cursor != null)
//                registerContentObserver(cursor);
            return cursor;
        }
    }
}
