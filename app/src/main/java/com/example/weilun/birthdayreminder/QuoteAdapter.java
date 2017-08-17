package com.example.weilun.birthdayreminder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Wei Lun on 8/17/2017.
 */

public class QuoteAdapter extends ArrayAdapter<Quote> {

    private Context context;
    public QuoteAdapter(Context context, List<Quote> quotes){
        super(context, 0, quotes);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rootView = convertView;

        if(rootView == null)
            rootView = LayoutInflater.from(context).inflate(R.layout.list_item_quote, parent, false);

        Quote quote = getItem(position);

        TextView tvAuthor = (TextView) rootView.findViewById(R.id.author);
        TextView tvCategory = (TextView) rootView.findViewById(R.id.category);

        tvAuthor.setText(quote.getAuthor());
        tvCategory.setText(quote.getCategory());

        return rootView;
    }
}
