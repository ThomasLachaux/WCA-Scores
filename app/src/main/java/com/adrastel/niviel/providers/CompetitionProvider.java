package com.adrastel.niviel.providers;

import com.adrastel.niviel.models.readable.competition.Competition;
import com.adrastel.niviel.models.writeable.BufferCompetition;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class CompetitionProvider {

    public static String IN_PROGRESS = "in-progress-comps";
    public static String UPCOMING_COMPS = "upcoming-comps";

    /**
     * Parse the data
     * @param document jsoup document
     * @param type in_progress or upcoming_comps
     * @return array treated
     */
    public static ArrayList<Competition> getCompetition(Document document, String type) {

        ArrayList<Competition> competitions = new ArrayList<>();

        try {

            Element table = document.getElementById(type);

            Elements lines = table.select("ul li");

            // On saute le titre
            for(int i = 1; i < lines.size(); i++) {

                Element line = lines.get(i);

                if(!line.hasClass("break")) {
                    Competition competition = hydrate(line);

                    competitions.add(competition);
                }
            }


        }
        catch (Exception e) {
            e.printStackTrace();
        }


        return competitions;

    }

    private static BufferCompetition hydrate(Element line) {

        BufferCompetition bufferCompetition = new BufferCompetition();

        String date = line.children().first().text();

        Element competition_infos = line.children().last();

        String competition = competition_infos.child(0).children().last().text();

        String competition_link = competition_infos.child(0).children().last().attr("href");

        String country = competition_infos.child(1).text();

        String place = competition_infos.child(2).text();

        bufferCompetition
                .setDate(date)
                .setCompetition(competition)
                .setCompetition_link(competition_link)
                .setCountry(country)
                .setPlace(place);


        return bufferCompetition;
    }


}
