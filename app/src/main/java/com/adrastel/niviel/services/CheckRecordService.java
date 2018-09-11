package com.adrastel.niviel.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.adrastel.niviel.R;
import com.adrastel.niviel.RecordModel;
import com.adrastel.niviel.activities.NotificationActivity;
import com.adrastel.niviel.activities.SettingsActivity;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.assets.WcaUrl;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.database.Record;
import com.adrastel.niviel.models.writeable.OldNewRecord;
import com.adrastel.niviel.providers.RecordProvider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CheckRecordService extends Service {

    DatabaseHelper database;

    private static int notif_id = 0;

    private long freq = 3600000;


    @Override
    public void onCreate() {
        super.onCreate();
        database = DatabaseHelper.getInstance(this);
        Log.i("Create CheckRecordService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        freq = Long.parseLong(preferences.getString(getString(R.string.pref_check_freq), "3600000"));

        boolean canUseMobile = preferences.getString(getString(R.string.pref_check_network), "1").equals("1");

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        // If the datas are enabled the option is disabled, terminates the service
        if(Assets.isConnectionMobile(connectivityManager) && !canUseMobile) {
            stopSelf();
        }

        ArrayList<Follower> followers = database.selectAllFollowers();

        for(final Follower follower : followers) {

            long follower_id = follower._id();

            final ArrayList<Record> oldRecords = database.selectRecordsFromFollower(follower_id);

            callData(follower.wca_id(), new dataCallback() {
                @Override
                public void onSuccess(ArrayList<com.adrastel.niviel.models.readable.Record> newRecords) {

                    compareRecords(follower, oldRecords, newRecords);
                    stopSelf();
                }

                @Override
                public void onFailure() {
                    stopSelf();
                }
            });

        }

        return START_NOT_STICKY;
    }

    @SuppressWarnings("deprecation")
    private void compareRecords(Follower follower, ArrayList<Record> oldRecords, ArrayList<com.adrastel.niviel.models.readable.Record> newRecords) {

        Log.d(String.valueOf(oldRecords.size()) + "<->" + String.valueOf(newRecords.size()));

        try {
            Collections.sort(oldRecords, new Record.Comparator());
            Collections.sort(newRecords, new com.adrastel.niviel.models.readable.Record.Comparator());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if(oldRecords.size() == newRecords.size()) {

            boolean hasToNotify = false;

            ArrayList<OldNewRecord> oldNewRecords = new ArrayList<>();

            String notificationMessage = "";

            for(int i = 0; i < oldRecords.size(); i++) {

                Record oldRecord = oldRecords.get(i);
                com.adrastel.niviel.models.readable.Record newRecord = newRecords.get(i);

                boolean scoresHasChanged = false;

                if((oldRecord == null || newRecord == null) || !oldRecord.event().equals(newRecord.getEvent())) continue;

                RecordModel.Marshal values = Record.FACTORY.marshal();

                if(singleChanged(oldRecord, newRecord)) {
                    try {

                        Log.v(oldRecord.single() + "->" + newRecord.getSingle());

                        values.single(newRecord.getSingle());
                        values.nr_single(Long.parseLong(newRecord.getNr_single()));
                        values.cr_single(Long.parseLong(newRecord.getCr_single()));
                        values.wr_single(Long.parseLong(newRecord.getWr_single()));

                        hasToNotify = true;
                        scoresHasChanged = true;

                        OldNewRecord oldNewRecord = new OldNewRecord(
                                this,
                                newRecord.getEvent(), OldNewRecord.SINGLE, oldRecord.single(),
                                newRecord.getSingle(), oldRecord.nr_single(), oldRecord.cr_single(), oldRecord.wr_single(),
                                newRecord.getNr_single(), newRecord.getCr_single(), newRecord.getWr_single());

                        oldNewRecords.add(oldNewRecord);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                // Check for average
                if(averageChanged(oldRecord, newRecord)) {

                    try {
                        Log.v(oldRecord.average() + "->" + newRecord.getAverage());


                        // Update database
                        values.average(newRecord.getAverage());
                        values.nr_average(Long.parseLong(newRecord.getNr_average()));
                        values.cr_average(Long.parseLong(newRecord.getCr_average()));
                        values.wr_average(Long.parseLong(newRecord.getWr_average()));

                        hasToNotify = true;
                        scoresHasChanged = true;

                        OldNewRecord oldNewRecord = new OldNewRecord(
                                this,
                                newRecord.getEvent(),
                                OldNewRecord.AVERAGE,
                                oldRecord.average(), newRecord.getAverage(),
                                oldRecord.nr_average(), oldRecord.cr_average(), oldRecord.wr_average(),
                                newRecord.getNr_average(), newRecord.getCr_average(), newRecord.getWr_average()
                        );

                        oldNewRecords.add(oldNewRecord);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                }

                if(scoresHasChanged) {
                    database.updateRecord(follower._id(), newRecord.getEvent(), values.asContentValues());
                }

            }

            if(hasToNotify) {

                for(OldNewRecord record : oldNewRecords) {
                    notificationMessage += record.getEvent() + ", ";
                }
                // Removes the last comma
                notificationMessage = getString(R.string.notif_new_event, notificationMessage.substring(0, notificationMessage.length() - 2));

                // More details
                Intent moreDetails = new Intent(this, NotificationActivity.class);
                moreDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                moreDetails.putExtra(NotificationActivity.CONTENT, toHtmlText(oldNewRecords));
                moreDetails.putExtra(NotificationActivity.NAME, getString(R.string.two_infos, follower.name(), follower.wca_id()));
                PendingIntent moreDetailsAction = PendingIntent.getActivity(this, 0, moreDetails, PendingIntent.FLAG_CANCEL_CURRENT);

                // Share
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, getString(R.string.notif_share, follower.name(), notificationMessage));
                PendingIntent shareAction = PendingIntent.getActivity(this, 0, share, 0);

                // Parameters
                Intent gotoSettings = new Intent(this, SettingsActivity.class);
                PendingIntent gotoSettingsAction = PendingIntent.getActivity(this, 0, gotoSettings, 0);



                NotificationCompat.Builder notification = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(follower.name())
                        .setContentText(notificationMessage)
                        .setTicker(notificationMessage)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setContentIntent(moreDetailsAction)
                        .addAction(R.drawable.ic_settings, getString(R.string.settings), gotoSettingsAction)
                        .addAction(R.drawable.ic_share, getString(R.string.share), shareAction)
                        .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(notificationMessage));


                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.notify(notif_id, notification.build());

                notif_id++;
            }

        }

        // If there is a new event
        else if (oldRecords.size() < newRecords.size()) {

            ArrayList<com.adrastel.niviel.models.readable.Record> filtredNewRecords;

            try {
                filtredNewRecords = Assets.getNewRecords(oldRecords, newRecords);


                for (com.adrastel.niviel.models.readable.Record record : filtredNewRecords) {

                    long Snr, Scr, Swr, Anr, Acr, Awr;

                    try {
                        Snr = Long.parseLong(record.getNr_single());
                        Scr = Long.parseLong(record.getCr_single());
                        Swr = Long.parseLong(record.getWr_single());
                    }

                    catch (Exception e) {
                        Snr = 0;
                        Scr = 0;
                        Swr = 0;
                    }

                    try {
                        Anr = Long.parseLong(record.getNr_single());
                        Acr = Long.parseLong(record.getCr_single());
                        Awr = Long.parseLong(record.getWr_single());
                    }

                    catch (Exception e) {
                        Anr = 0;
                        Acr = 0;
                        Awr = 0;
                    }

                    database.insertRecord(follower._id(), record.getEvent(), record.getSingle(), Snr, Scr, Swr, record.getAverage(), Anr, Acr, Awr);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<Record> oldFollowersUpdated = database.selectRecordsFromFollower(follower._id());

            // If the records were added succesfully, calls the method again
            if(oldFollowersUpdated.size() == newRecords.size()) {
                compareRecords(follower, oldFollowersUpdated, newRecords);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private boolean singleChanged(Record oldRecord, com.adrastel.niviel.models.readable.Record newRecord) {
        return oldRecord.single() != null && newRecord.getSingle() != null && !oldRecord.single().equals(newRecord.getSingle());
    }


    @SuppressWarnings("ConstantConditions")
    private boolean averageChanged(Record oldRecord, com.adrastel.niviel.models.readable.Record newRecord) {
        return oldRecord.average() != null && newRecord.getAverage() != null && !oldRecord.average().equals(newRecord.getAverage());
    }

    @Override
    public void onDestroy() {

        if(freq != 0) {
            Intent checkRecords = new Intent(this, CheckRecordService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, checkRecords, 0);

            AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

            alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + freq, pendingIntent);
        }

        super.onDestroy();
    }

    private void callData(String wca_id, final dataCallback callback) {

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
                callback.onFailure();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if(!response.isSuccessful()) {
                    callback.onFailure();
                    return;
                }

                Document document = Jsoup.parse(response.body().string());
                response.close();

                ArrayList<com.adrastel.niviel.models.readable.Record> records = RecordProvider.getRecord(getApplicationContext(), document);

                callback.onSuccess(records);
            }
        });

    }

    private interface dataCallback {
        void onSuccess(ArrayList<com.adrastel.niviel.models.readable.Record> records);
        void onFailure();
    }

    private String toHtmlText(ArrayList<OldNewRecord> records) {

        String content = "";

        for(OldNewRecord record : records) {

            String type = "<strong><big>" + record.getEvent() + " " + record.getType() + "</big></strong><br/><br/>";

            String time_result = record.getOldTime() + " -> " + record.getNewTime() + "<br/>";
            String time = "\t" + getString(R.string.record_time, time_result);

            String nr_result = record.getOldNr() + " -> " + record.getNewNr() + "<br/>";
            String nr = "\t" + getString(R.string.record_nr_format, nr_result);

            String cr_result = record.getOldCr() + " -> " + record.getNewCr() + "<br/>";
            String cr = "\t" + getString(R.string.record_cr_format, cr_result);

            String wr_result = record.getOldWr() + " -> " + record.getNewWr() + "<br/><br/><br/>";
            String wr = "\t" + getString(R.string.record_wr_format, wr_result);

            content += type + time + nr + cr + wr;
        }

        return content;
    }
}
