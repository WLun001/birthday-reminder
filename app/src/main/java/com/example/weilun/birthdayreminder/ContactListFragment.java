package com.example.weilun.birthdayreminder;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weilun.birthdayreminder.db.PersonContract;
import com.example.weilun.birthdayreminder.db.PersonDBHelper;
import com.example.weilun.birthdayreminder.db.PersonDBQueries;

/**
 * A placeholder fragment containing a simple view.
 */
public class ContactListFragment extends Fragment
        implements SearchView.OnQueryTextListener,
        SearchView.OnCloseListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int REQUEST_CODE = 1;
    public static final String EXTRA_ID = "com.example.weilun.birthdayreminder.ID";
    public static final int SEARCH_LOADER_ID = 1;
    private PersonCursorAdapter adapter;
    private SearchView searchView;
    private String searchKeyword = null;
    private TextView tv;
    private ProgressBar loadingBar;
    private Refreshable refreshable;

    public ContactListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview);
        loadingBar = (ProgressBar) rootView.findViewById(R.id.loading_bar);

        tv = (TextView) rootView.findViewById(R.id.no_birthday);
        listView.setEmptyView(tv);
        adapter = new PersonCursorAdapter(getActivity(), null, 0);
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
        //getLoaderManager().restartLoader(SEARCH_LOADER_ID, null, this);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            refreshable = (Refreshable) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("must implement Refreshable interface");
        }
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

        inflater.inflate(R.menu.contact_menu, menu);
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (searchView == null)
            return;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint(getString(R.string.action_search));
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_delete) {
            comfirmDeleteAll();
        }
        return super.onOptionsItemSelected(item);
    }

    private void comfirmDeleteAll() {
        DialogFragment fragment = new DeleteRecordsDialogFragment();
        fragment.setTargetFragment(ContactListFragment.this, REQUEST_CODE);
        fragment.show(getActivity().getSupportFragmentManager(), "deleteDialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //result code == -1 when positive button, else is 0
        if (resultCode == -1) {
            PersonDBQueries dbQueries = new PersonDBQueries(new PersonDBHelper(getActivity()));
            dbQueries.deleteAll();
            getLoaderManager().restartLoader(SEARCH_LOADER_ID, null, this);
            refreshable.onRefresh();
            Toast.makeText(getActivity(), R.string.delete_success, Toast.LENGTH_SHORT).show();
        }
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
        if (!TextUtils.isEmpty(query)) {
            searchKeyword = query;
            Log.v("onQueryTextSubmit", "Restarting Loader when have keyword");
            getLoaderManager().restartLoader(SEARCH_LOADER_ID, null, this);
        } else {
            searchKeyword = null;
            Log.v("onQueryTextSubmit", "Restarting Loader when no keyword");
            getLoaderManager().restartLoader(SEARCH_LOADER_ID, null, this);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v("onCreateLoader", "creating loader");
        return new SearchLoader(getActivity(), searchKeyword);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        loadingBar.setVisibility(View.GONE);
        tv.setText(getString(R.string.no_birthday_data));
        Log.v("onLoadFinish", Integer.toString(data.getColumnCount()));
        adapter.swapCursor(data);
        tv.setVisibility(View.GONE);
        Log.v("onLoadFinish", "Swapping adapter");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v("onLoadReset", "resetting loader");
        adapter.swapCursor(null);
    }

    /**
     * A listener to notify program to refresh when have changes in data
     */
    public interface Refreshable {
        void onRefresh();
    }

    /**
     * A class to perform search in background
     */
    public static final class SearchLoader extends AsyncTaskLoader<Cursor> {
        private String keyword;
        private Context context;

        public SearchLoader(Context context, String keyword) {
            super(context);
            Log.v("SearchLoader", "SearchLoader instantiated");
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
            if (keyword != null) {
                Log.v("loadInBackground", "LIKE QUERY");
                String[] selectionArgs = {"%" + keyword + "%"};
                cursor = dbQuery.query(columns, PersonContract.PersonEntry.COLUMN_NAME_NAME + " LIKE ?"
                        , selectionArgs, null, null
                        , null);
            } else {
                Log.v("loadInBackgrond", "query in background");
                cursor = dbQuery.query(columns, null, null, null, null
                        , PersonContract.PersonEntry.COLUMN_NAME_NAME + " ASC");
            }
            return cursor;
        }
    }
}