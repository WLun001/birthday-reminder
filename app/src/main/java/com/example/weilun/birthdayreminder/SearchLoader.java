package com.example.weilun.birthdayreminder;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.example.weilun.birthdayreminder.db.PersonContract;
import com.example.weilun.birthdayreminder.db.PersonDBHelper;
import com.example.weilun.birthdayreminder.db.PersonDBQueries;

/**
 * Created by Wei Lun on 8/10/2017.
 */

public class SearchLoader extends AsyncTaskLoader<Cursor> {
    private Cursor cursor;
    private Context context;

    public SearchLoader(Context context, Cursor cursor){
        super(context);
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public Cursor loadInBackground() {
        PersonDBQueries dbQuery = new PersonDBQueries(new PersonDBHelper(context));
        String[] columns = PersonContract.columns;
        cursor = dbQuery.query(columns, null, null, null, null
                , PersonContract.PersonEntry.COLUMN_NAME_DOB + " ASC");
        return cursor;
    }
}
