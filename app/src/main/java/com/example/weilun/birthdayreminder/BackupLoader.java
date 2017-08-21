package com.example.weilun.birthdayreminder;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.weilun.birthdayreminder.db.PersonContract;
import com.example.weilun.birthdayreminder.db.PersonDBHelper;
import com.example.weilun.birthdayreminder.db.PersonDBQueries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Wei Lun on 8/16/2017.
 */

public class BackupLoader extends android.content.AsyncTaskLoader<JSONObject> {
    private static final String JSON_URL = "http://labs.jamesooi.com/uecs3253-asg.php";
    private static String LOG_TAG = BackupLoader.class.getSimpleName();
    private List<Person> persons;
    private ProgressDialog progressDialog;
    private Context context;

    public BackupLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(R.string.progress_dialog_uploading);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        forceLoad();
    }

    public boolean progressDialogIsShow() {
        return progressDialog.isShowing();
    }

    public void stopProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    public JSONObject loadInBackground() {
        JSONObject jsonRespose = null;
        try {
            readFromDb();
            jsonRespose = postJSON();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return jsonRespose;
    }

    private void readFromDb() {
        PersonDBQueries dbQuery = new PersonDBQueries(new PersonDBHelper(getContext()));
        String[] columns = PersonContract.columns;
        Cursor cursor = dbQuery.query(columns, null, null, null, null
                , PersonContract.PersonEntry.COLUMN_NAME_NAME + " ASC");
        persons = PersonDBQueries.getPersonList(cursor);
    }

    private JSONArray putAsJSONArray() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < persons.size(); i++) {
                jsonArray.put(
                        new JSONObject().put("id", persons.get(i).getId())
                                .put("name", persons.get(i).getName())
                                .put("email", persons.get(i).getEmail())
                                .put("phone", persons.get(i).getPhone())
                                .put("dob", persons.get(i).getDOB())
                                .put("notify", persons.get(i).isNotify())
                );
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return jsonArray;
    }

    private JSONObject postJSON() throws IOException {
        InputStream inputStream = null;
        URL url = new URL(JSON_URL);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("POST");
        connection.setDoInput(true);

        try {
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(getPostDataString(putAsJSONArray()));
            writer.flush();
            writer.close();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                inputStream = connection.getInputStream();

                // Convert the InputStream into ArrayList<Person>
                return readInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "ERROR:" + Integer.toString(responseCode));
                return null;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            return null;
        } finally {
            inputStream.close();
        }
    }

    private String getPostDataString(JSONArray data) throws Exception {

        StringBuilder result = new StringBuilder();

        result.append(URLEncoder.encode("data", "UTF-8"));
        result.append("=");
        result.append(URLEncoder.encode(data.toString(), "UTF-8"));

        return result.toString();
    }

    private JSONObject readInputStream(InputStream is)
            throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder builder = new StringBuilder();

        String input;
        while ((input = reader.readLine()) != null)
            builder.append(input);

        return new JSONObject(builder.toString());
    }
}