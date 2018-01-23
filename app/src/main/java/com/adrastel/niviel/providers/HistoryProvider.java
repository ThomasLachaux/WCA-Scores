package com.adrastel.niviel.providers;

import com.adrastel.niviel.models.readable.history.History;
import com.adrastel.niviel.models.writeable.BufferHistory;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class HistoryProvider {

    private static String event_buffer = null;
    private static String competition_buffer = null;

    public static ArrayList<History> getHistory(Document document) {


        ArrayList<History> arrayList = new ArrayList<>();


        try {

            // Gets the table
            Element table = document.select(".floatThead").first();

            // Checks to not get the records table
            /*if(table.children().first().tagName().equals("h3")) {
                table = document.select("table").get(document.select("table").size() - 1);
            }*/

            // Gets the lines
            Elements trs = table.select("tbody tr");

            BufferHistory bufferHistory = new BufferHistory();

            for (int i = 0; i < trs.size(); i++) {

                Element tr = trs.get(i);

                if (isEvent(tr)) {
                    event_buffer = tr.children().first().text();
                    bufferHistory.setEvent(tr.children().first().text());
                }

                else if (isCompetition(tr)) {
                    arrayList.add(hydrate(tr));
                }

            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;

    }

    private static boolean isEvent(Element tr) {

        Element td = tr.children().first();

        return td.hasClass("event");

    }
    private static boolean isCompetition(Element tr) {

        return tr.hasClass("result");

    }

    private static BufferHistory hydrate(Element tr) {

        BufferHistory bufferHistory = new BufferHistory();

        StringBuilder result_details = new StringBuilder();

        for(int j = 0; j < tr.children().size(); j++) {

            String text = tr.child(j).text();

            switch (j) {
                case 0:

                    if(text.replaceAll("\\s", "").length() == 0) {
                        bufferHistory.setCompetition(competition_buffer);
                    }

                    else {
                        competition_buffer = text;
                        bufferHistory.setCompetition(text);
                    }
                     break;
                case 1: bufferHistory.setRound(text); break;
                case 2: bufferHistory.setPlace(text); break;
                case 3: bufferHistory.setBest(text); break;
                // case 4: blank
                case 5: bufferHistory.setAverage(text); break;
                // case 6: blank
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                    result_details.append(text).append(" ");
                    break;
            }


        }

        bufferHistory.setEvent(event_buffer);
        bufferHistory.setResult_details(result_details.toString());

        return bufferHistory;
    }



}
