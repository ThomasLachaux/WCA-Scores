package com.adrastel.niviel.assets;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;

import com.adrastel.niviel.database.DatabaseHelper;
import com.adrastel.niviel.database.Follower;
import com.adrastel.niviel.database.Record;

import java.util.ArrayList;

public class Assets {

    public static boolean isConnected(@NonNull ConnectivityManager manager) {
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }
    public static boolean isConnectionMobile(ConnectivityManager manager) {
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public static String wrapStrong(String text) {
        return "<strong>" + text + "</strong>";
    }

    @SuppressWarnings("SameParameterValue")
    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static int spToPx(Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        }

        else {
            return Html.fromHtml(html);
        }
    }

    @SuppressWarnings("deprecation")
    public static int getColor(Context context, int id) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public static Spanned formatHtmlAverageDetails(String average, String details) {

        String html = "<strong>" + average + "</strong>" + " (" + details + ")";
        html = html.replace("DNF", "<font color=\"#CC0000\">DNF</font>");
        html = html.replace("DNS", "<font color=\"#FF8800\">DNS</font>");
        html = html.trim();
        return fromHtml(html);
    }

    public static boolean isFollowing(Context context, String wca_id) {

        DatabaseHelper db = DatabaseHelper.getInstance(context);

        ArrayList<Follower> followers = db.selectAllFollowers();

        for(Follower follower : followers) {
            if(follower.wca_id().equals(wca_id)) {
                return true;
            }
        }

        return false;

    }
    /**
     * Compare les records et si il y a un nouveau record, l'ajoute dans un ArrayList
     * La taille d'oldRecord est toujours inférieure ou égale à celle des newRecords
     *
     * Compares the records between the oldRecord and the newRecords
     * If there is a new record adds it in the ArrayList in return
     *
     * The oldRecord size must be lower than the newRecord one
     *
     * @param oldRecords old records
     * @param newRecords new records
     * @return difference
     */
    public static ArrayList<com.adrastel.niviel.models.readable.Record> getNewRecords(ArrayList<Record> oldRecords, ArrayList<com.adrastel.niviel.models.readable.Record> newRecords) {

        ArrayList<com.adrastel.niviel.models.readable.Record> newEvents = new ArrayList<>();

        // Position in the array
        int i = 0;

        try {

            for(com.adrastel.niviel.models.readable.Record newRecord : newRecords) {

                Record oldRecord = oldRecords.get(i);

                if(!oldRecord.event().equals(newRecord.getEvent())) {

                    newEvents.add(newRecord);

                    // Decrements to not being offset
                    i--;
                }


                i++;

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return newEvents;

    }

    public static boolean isDark(String darkness) {
        return darkness.equals("1");

    }


    public static String dayNightBooleanToString(boolean isDark) {
        return isDark ? "1" : "0";
    }
}
