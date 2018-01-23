package com.adrastel.niviel.providers;

import android.net.Uri;

import com.adrastel.niviel.models.writeable.BufferRanking;
import com.adrastel.niviel.models.readable.Ranking;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class RankingProvider {

    private static String rank_buffer = null;

    public static ArrayList<Ranking> getRanking(Document document) {

        ArrayList<Ranking> arrayList = new ArrayList<>();

        try {

            // Gets the table
            Element table = document.select("table").first();

            // Gets the lines
            Elements trs = table.select("tbody tr");

            BufferRanking bufferRanking;

            for (int i = 4; i < trs.size(); i++) {

                // Gets the line
                Element tr = trs.get(i);

                bufferRanking = hydrate(tr);

                arrayList.add(bufferRanking);
            }

        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }

    private static BufferRanking hydrate(Element tr) {

        BufferRanking ranking = new BufferRanking();
        


        return hydrate(ranking, tr);
    }

    private static BufferRanking hydrate(BufferRanking bufferRanking, Element tr) {

        for(int i = 0; i < tr.children().size(); i++) {

            Element td = tr.children().get(i);
            String text = td.text();

            switch (i) {

                case 0:

                    if(text.replaceAll("\\s", "").length() == 0) {
                        bufferRanking.setRank(rank_buffer);
                    }

                    else {
                        bufferRanking.setRank(text);
                        rank_buffer = text;
                    }

                    break;

                case 1:
                    bufferRanking.setPerson(text);


                    String url = td.child(0).attr("href");

                    Uri uri = Uri.parse(url);
                    String wca_id = uri.getQueryParameter("i");
                    bufferRanking.setWca_id(wca_id);
                    break;

                case 2:
                    bufferRanking.setResult(text);
                    break;

                case 3:
                    bufferRanking.setCitizen(text);
                    break;

                case 4:
                    bufferRanking.setCompetition(text);
                    break;

                case 5:
                    bufferRanking.setDetails(text);
            }

        }


        return bufferRanking;

    }

}
