package com.example.weilun.birthdayreminder;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by Wei Lun on 8/11/2017.
 */

public abstract class DBLoader extends AsyncTaskLoader<Cursor> {

    private Context context;
    public DBLoader(Context context){
        super(context);
        this.context = context;
    }
    @Override
    abstract public Cursor loadInBackground() ;
}
