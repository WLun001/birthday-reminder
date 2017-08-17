package com.example.weilun.birthdayreminder;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weilun.birthdayreminder.db.PersonDBHelper;
import com.example.weilun.birthdayreminder.db.PersonDBQueries;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Wei Lun on 8/17/2017.
 */

public class FamousQuotesFragment extends Fragment
implements android.support.v4.app.LoaderManager.LoaderCallbacks<List<Quote>>{

    public static final int QUOTE_LOADER_ID = 1;
    private ProgressBar loadingBar;
    private TextView emptyView;
    private QuoteAdapter adapter;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.listview, container, false);

        listView = (ListView) rootView.findViewById(R.id.listview);
        loadingBar = (ProgressBar) rootView.findViewById(R.id.loading_bar);
        emptyView = (TextView) rootView.findViewById(R.id.no_birthday);
        listView.setEmptyView(emptyView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Quote quote = (Quote) parent.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(quote.getQuote()).setTitle(quote.getAuthor())
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNeutralButton("copy text", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText(getString(R.string.dialog_copy_text), quote.getQuote() );
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(getActivity(), getString(R.string.copied_success), Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        getLoaderManager().restartLoader(QUOTE_LOADER_ID, null, this);
        return rootView;
    }

    @Override
    public android.support.v4.content.Loader<List<Quote>> onCreateLoader(int id, Bundle args) {
        return new fetchQuoteTask(getActivity());
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Quote>> loader, List<Quote> data) {
        loadingBar.setVisibility(View.GONE);
        emptyView.setText(getString(R.string.no_quote_found));
        adapter = new QuoteAdapter(getActivity(), data);
        listView.setAdapter(adapter);

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<Quote>> loader) {
        adapter.clear();;
        adapter.notifyDataSetChanged();
    }

    public static class fetchQuoteTask extends android.support.v4.content.AsyncTaskLoader<List<Quote>> {
        public static final String QUOTE_URL = "https://talaikis.com/api/quotes/";
        private Context context;

        public fetchQuoteTask(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Override
        public List<Quote> loadInBackground() {
            List<Quote> quotes;
            try {
                quotes = getFromJson();
            }catch (IOException e){
                return null;
            }
            return quotes;
        }

        private List<Quote> getFromJson() throws IOException {
            InputStream inputStream = null;
            List<Quote> quotes = null;

            URL url = new URL(QUOTE_URL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");

            // Starts the query
            conn.connect();
            if (conn.getResponseCode() == 200) {
                inputStream = conn.getInputStream();
                quotes = readInputStream(inputStream);
            }
            if (inputStream != null)
                inputStream.close();
            return quotes;
        }

        private List<Quote> readInputStream(InputStream inputStream) throws IOException {
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
            List<Quote> quotes = readAlbumsArray(reader);
            reader.close();
            return quotes;
        }

        private List<Quote> readAlbumsArray(JsonReader reader) {
            List<Quote> quotes = new ArrayList<>();

            try {
                reader.beginArray();
                while (reader.hasNext()) {
                    quotes.add(readQuote(reader));
                }
                reader.endArray();
            } catch (IOException e) {
                return null;
            }
            return quotes;
        }

        private Quote readQuote(JsonReader reader) throws IOException {
            Quote quote = new Quote();

            reader.beginObject();
            while (reader.hasNext()) {
                String key = reader.nextName();
                if (key.equals("quote")) {
                    quote.setQuote(reader.nextString());
                } else if (key.equals("author")) {
                    quote.setAuthor(reader.nextString());
                } else if (key.equals("cat")) {
                    quote.setCategory(reader.nextString());
                } else
                    reader.skipValue();
            }
            reader.endObject();
            return quote;
        }
    }
}
