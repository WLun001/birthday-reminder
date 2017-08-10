package com.example.weilun.birthdayreminder;

import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by Wei Lun on 8/11/2017.
 */

public abstract class DBLoader extends AsyncTaskLoader<Cursor> {

    protected Context context;
    protected  ForceLoadContentObserver observer;
    public DBLoader(Context context){
        super(context);
        this.context = context;
        observer = new ForceLoadContentObserver();
    }
    @Override
    abstract public Cursor loadInBackground() ;

    protected void registerContentObserver(Cursor cursor){
        cursor.registerContentObserver(observer);
    }
}
