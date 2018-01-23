package com.adrastel.niviel.providers;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Log;
import com.adrastel.niviel.models.writeable.BufferRecord;
import com.adrastel.niviel.models.readable.Record;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class RecordProvider {


    public static ArrayList<Record> getRecord(final Context context, Document document) {

        ArrayList<Record> arrayList = new ArrayList<>();

        Activity activity = null;

        try {
            activity = (Activity) context;
        }

        catch (ClassCastException e) {
            Log.e("Application cannot be cast to Activity");
        }

        try {
            // Gets the table
            Element table = document.select("table").get(1);

            // Gets the lines
            Elements trs = table.select("tbody tr");

            BufferRecord bufferRecord;


            // Starts from the third line

            for (int i = 0; i < trs.size(); i++) {

                // Gets the line
                Element tr = trs.get(i);

                bufferRecord = hydrate(tr);

                arrayList.add(bufferRecord);
            }


        }

        catch (Exception exception) {

            if(activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, context.getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                    }
                });
            }

        }

        return arrayList;
    }

    private static BufferRecord hydrate(BufferRecord bufferRecord, Element tr) {

        for(int j = 0; j < tr.children().size(); j++) {

            Element td = tr.children().get(j);

            switch (j) {

                case 0:
                    bufferRecord.setEvent(td.text());
                    break;

                case 1:
                    bufferRecord.setNr_single(td.text());
                    break;

                case 2:
                    bufferRecord.setCr_single(td.text());
                    break;

                case 3:
                    bufferRecord.setWr_single(td.text());
                    break;

                case 4:
                    bufferRecord.setSingle(td.text());
                    break;

                case 5:
                    bufferRecord.setAverage(td.text());
                    break;

                case 6:
                    bufferRecord.setWr_average(td.text());
                    break;

                case 7:
                    bufferRecord.setCr_average(td.text());
                    break;

                case 8:
                    bufferRecord.setNr_average(td.text());
                    break;
            }
        }

        return bufferRecord;

    }

    private static BufferRecord hydrate(Element tr) {
        BufferRecord bufferRecord = new BufferRecord();

        return hydrate(bufferRecord, tr);
    }
}
