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
import android.support.v4.app.NotificationCompat;

import com.adrastel.niviel.R;
import com.adrastel.niviel.activities.MainActivity;
import com.adrastel.niviel.activities.SettingsActivity;
import com.adrastel.niviel.assets.Assets;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.assets.WcaUrl;
import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.models.readable.competition.Competition;
import com.adrastel.niviel.providers.CompetitionProvider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CheckCompetitionService extends Service {

    DatabaseHelper database;

    private long freq = 3600000;

    @Override
    public void onCreate() {
        super.onCreate();

        database = DatabaseHelper.getInstance(this);
        Log.i("Create CheckCompetitionService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        freq = Long.parseLong(preferences.getString(getString(R.string.pref_check_freq), "3600000"));

        boolean canUseMobile = preferences.getString(getString(R.string.pref_check_network), "1").equals("1");

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        // If the datas are enabled the option is disabled, terminates the service
        if(Assets.isConnectionMobile(connectivityManager) && !canUseMobile) {
            stopSelf();
        }

        // Last competition name the last time the service was run
        final String last_competition_checked = preferences.getString(getString(R.string.pref_last_competitions_checked), null);

        // Country the user has chosen
        final String personal_country = preferences.getString(getString(R.string.pref_country), null);

        if(personal_country == null || personal_country.equalsIgnoreCase("")) {
            stopSelf();
        }

        callData(personal_country, new Callback() {
            @Override
            public void onSuccess(ArrayList<Competition> competitions) {

                if(competitions == null || competitions.size() == 0) {
                    stopSelf();
                }
                else {
                    Competition competition = competitions.get(0);

                    String title = competition.getCompetition();

                    if(title != null) {
                        // If nothing has changed
                        if(last_competition_checked != null && title.equals(last_competition_checked)) {
                            stopSelf();
                        }

                        else {
                            preferences
                                    .edit()
                                    .putString(getString(R.string.pref_last_competitions_checked), title)
                                    .apply();


                            sendNotification();
                        }
                    }


                }
            }

            @Override
            public void onFailure() {
                stopSelf();
            }
        });




        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void callData(String country, final Callback callback) {

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new WcaUrl()
                .competition(new ArrayList<String>(), country)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                callback.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if(!response.isSuccessful()) {
                    callback.onFailure();
                    return;
                }

                Document document = Jsoup.parse(response.body().string());
                response.close();

                ArrayList<Competition> competitions = CompetitionProvider.getCompetition(document, CompetitionProvider.UPCOMING_COMPS);

                callback.onSuccess(competitions);

            }
        });

    }

    private void sendNotification() {

        Intent gotoCompetitions = new Intent(this, MainActivity.class);
        PendingIntent gotoCompetitionAction = PendingIntent.getActivity(this, 0, gotoCompetitions, 0);

        Intent gotoSettings = new Intent(this, SettingsActivity.class);
        PendingIntent gotoSettingsAction = PendingIntent.getActivity(this, 0, gotoSettings, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setTicker(getString(R.string.competitions_available_title))
                .setContentTitle(getString(R.string.competitions_available_title))
                .setContentText(getString(R.string.competitions_available_content))
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(gotoCompetitionAction)
                .addAction(R.drawable.ic_settings, getString(R.string.settings), gotoSettingsAction)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.competitions_available_content)));

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        int notif_id = 1000;
        manager.notify(notif_id, notification.build());

    }

    @Override
    public void onDestroy() {

        if(freq != 0) {
            Intent checkRecords = new Intent(this, CheckCompetitionService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, checkRecords, 0);

            AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

            alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + freq, pendingIntent);
        }

        super.onDestroy();
    }

    public interface Callback {
        void onSuccess(ArrayList<Competition> competitions);
        void onFailure();
    }
}
