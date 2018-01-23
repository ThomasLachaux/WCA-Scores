package com.adrastel.niviel.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.assets.WcaUrl;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.models.readable.Record;
import com.adrastel.niviel.models.readable.User;
import com.adrastel.niviel.models.readable.history.History;
import com.adrastel.niviel.providers.HistoryProvider;
import com.adrastel.niviel.providers.RecordProvider;
import com.adrastel.niviel.providers.UserProvider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EditRecordService extends IntentService {


    public static final String WCA_ID = "wca_id";
    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String IS_PERSONAL = "is_personal";
    public static final String ACTION = "action";
    public static final int ADD_FOLLOWER = 0;
    public static final int DELETE_FOLLOWER = 1;
    public static final String FOLLOWS = "follows";

    private String wca_id = null;
    private String username = null;
    private boolean isPersonal = false;

    private boolean follows = true;
    private DatabaseHelper db;
    private Handler handler;

    // Receiver
    public static final String INTENT_FILTER = "editrecordservice";
    public static final int ADD_RECORD_SUCCESS = 0;
    public static final int ADD_RECORD_FAILURE = 1;


    public EditRecordService() {
        super("EditRecordService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        db = DatabaseHelper.getInstance(this);
        handler = new Handler(Looper.getMainLooper());

        int action = intent.getIntExtra(ACTION, ADD_FOLLOWER);

        long follower_id = intent.getLongExtra(ID, -1);
        wca_id = intent.getStringExtra(WCA_ID);
        username = intent.getStringExtra(USERNAME);
        isPersonal = intent.getBooleanExtra(IS_PERSONAL, false);
        follows = intent.getBooleanExtra(FOLLOWS, true);

        if(action == ADD_FOLLOWER) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), R.string.toast_adding, Toast.LENGTH_LONG).show();
                }
            });

            getRecords(new recordsCallback() {
                @Override
                public void onSuccess(User user, ArrayList<Record> records, ArrayList<History> histories) {

                    final long follower = db.insertFollower(username, wca_id, user.getCountry(), user.getGender(), user.getCompetitions());
                    insertRecords(follower, records);
                    insertHistories(follower, histories);

                    if(isPersonal) {
                        PreferenceManager
                                .getDefaultSharedPreferences(EditRecordService.this)
                                .edit()
                                .putLong(getString(R.string.pref_personal_id), follower)
                                .apply();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String confirmation = follows ? getString(R.string.toast_follow_confirmation, username) : getString(R.string.toast_switch_profile_confirmation, username);
                            Toast.makeText(getApplicationContext(), confirmation, Toast.LENGTH_LONG).show();


                            Intent intent = new Intent(INTENT_FILTER);
                            intent.putExtra(ACTION, ADD_RECORD_SUCCESS);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        }
                    });
                }
            });
        }

        else if(action == DELETE_FOLLOWER) {

            if(follower_id == -1) {
                follower_id = db.selectFollowerIdFromWca(wca_id);
            }
            db.deleteHistories(follower_id);
            db.deleteRecords(follower_id);
            db.deleteFollower(follower_id);


            handler.post(new Runnable() {
                @Override
                public void run() {
                    String confirmation = getString(R.string.toast_unfollow_confirmation, username);
                    if (follows) Toast.makeText(getApplicationContext(), confirmation, Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Destroy EditRecordService");
    }

    private void getRecords(final recordsCallback callback) {

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new WcaUrl()
                .profile(wca_id)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.error_connection, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(INTENT_FILTER);
                        intent.putExtra(ACTION, ADD_RECORD_FAILURE);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()) {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), R.string.error_connection, Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(INTENT_FILTER);
                            intent.putExtra(ACTION, ADD_RECORD_FAILURE);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        }
                    });
                    return;
                }

                Document document = Jsoup.parse(response.body().string());
                ArrayList<Record> records = RecordProvider.getRecord(getApplicationContext(), document);

                ArrayList<History> histories = HistoryProvider.getHistory(document);

                User user = UserProvider.getUser(document);

                response.close();

                callback.onSuccess(user, records, histories);


            }
        });
    }

    private interface recordsCallback {
        void onSuccess(User user, ArrayList<Record> records, ArrayList<History> histories);
    }

    private void insertRecords(long follower, ArrayList<Record> records) {


        int s = records.size();

        String[] events = new String[s];
        String[] singles = new String[s];
        long[] nr_singles = new long[s];
        long[] cr_singles = new long[s];
        long[] wr_singles = new long[s];
        String[] averages = new String[s];
        long[] nr_average = new long[s];
        long[] cr_average = new long[s];
        long[] wr_average = new long[s];

        for(int i = 0; i < s; i++) {

            Record record = records.get(i);

            try {
                events[i] = record.getEvent();
            }

            catch (Exception e) {
                e.printStackTrace();
            }

            try {
                singles[i] = record.getSingle();
                nr_singles[i] = Long.parseLong(record.getNr_single());
                cr_singles[i] = Long.parseLong(record.getCr_single());
                wr_singles[i] = Long.parseLong(record.getWr_single());
            }

            catch (Exception e) {
                singles[i] = null;
                nr_singles[i] = 0;
                cr_singles[i] = 0;
                wr_singles[i] = 0;

                e.printStackTrace();
            }

            try {
                averages[i] = record.getAverage();
                nr_average[i] = Long.parseLong(record.getNr_average());
                cr_average[i] = Long.parseLong(record.getCr_average());
                wr_average[i] = Long.parseLong(record.getWr_average());
            }

            catch (Exception e) {
                averages[i] = null;
                nr_average[i] = 0;
                nr_average[i] = 0;
                nr_average[i] = 0;

                e.printStackTrace();
            }

            db.insertRecord(
                    follower, events[i],
                    singles[i], nr_singles[i], cr_singles[i], wr_singles[i],
                    averages[i], nr_average[i], cr_average[i], wr_average[i]
            );
        }
    }

    private void insertHistories(long follower, ArrayList<History> histories) {

        for(History history : histories) {

            db.insertHistory(
                    follower,
                    history.getEvent(),
                    history.getCompetition(), history.getRound(),
                    history.getPlace(), history.getBest(), history.getAverage(), history.getResult_details());

        }

    }
}