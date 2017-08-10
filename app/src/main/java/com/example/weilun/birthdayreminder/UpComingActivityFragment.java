package com.example.weilun.birthdayreminder;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
    public static final int SEARCH_LOADER_ID = 1;
    private PersonCursorAdapter adapter;
    private SearchView searchView = null;
    private String searchKeyword;

    public UpComingActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_up_coming, container, false);
        ListView listView =(ListView) rootView.findViewById(R.id.listview);
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
        getLoaderManager().initLoader(SEARCH_LOADER_ID, null, this);
        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        //actionbar.setTitle(R.string.title_accounts);
        //actionbar.setDisplayHomeAsUpEnabled(true);

    }

    public void refresh(){
        getLoaderManager().restartLoader(SEARCH_LOADER_ID, null, this);
    }
    @Override
    public void onResume() {
        super.onResume();
        ListView listView =(ListView) getActivity().findViewById(R.id.listview);

        TextView tv = (TextView)getActivity().findViewById(R.id.no_birthday);
        listView.setEmptyView(tv);
        adapter = new PersonCursorAdapter(getActivity(), null, 0);
        tv.setText(getString(R.string.no_birthday));
        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
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
        if(!TextUtils.isEmpty(newText)){
            searchKeyword = newText;
            getLoaderManager().restartLoader(SEARCH_LOADER_ID, null, this);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SearchLoader(getActivity(), searchKeyword);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public static final class SearchLoader extends DBLoader{
        private String keyword;
        private Context context;

        public SearchLoader(Context context, String keyword){
            super(context);
            this.context = context;
            this.keyword = keyword;
        }
        @Override
        public Cursor loadInBackground() {
            PersonDBQueries dbQuery = new PersonDBQueries(new PersonDBHelper(context));
            Cursor cursor;
            String[] columns = PersonContract.columns;
            if(keyword != null) {
                String[] selectionArgs = {"%" + keyword + "%"};
                cursor = dbQuery.query(columns, PersonContract.PersonEntry.COLUMN_NAME_NAME + " LIKE"
                        , selectionArgs, null, null
                        , PersonContract.PersonEntry.COLUMN_NAME_NAME + " ASC");
        }
            else {
                cursor = dbQuery.query(columns, null, null, null, null
                        , PersonContract.PersonEntry.COLUMN_NAME_NAME + " ASC");

            }
            if(cursor != null)
                registerContentObserver(cursor);
            return cursor;
        }
    }


}
