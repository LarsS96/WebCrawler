package controller;

import lombok.extern.slf4j.Slf4j;
import model.Player;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
@Slf4j
public class TransfermarktScraper {

    public Player scrapePlayer(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String playerName = scrapeName(doc);

        if (playerName.equals("Club")) {
            return null;
        }

        Player player = new Player();
        player.setName(scrapeName(doc));
        player.setAge(scrapeAge(doc));
        player.setMarketValue(scrapeMarketValue(doc));
        return player;
    }

    public List<String> scrapeTeamPlayers(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements playerLinks = doc.select("td.hauptlink a");
        List<String> playerUrls = new ArrayList<>();

        for (Element link : playerLinks) {
            String playerUrl = "https://www.transfermarkt.com" + link.attr("href");
            playerUrls.add(playerUrl);
        }
        return playerUrls;
    }

    public String scrapeName(Document doc) {
        String nameClass = "h1.data-header__headline-wrapper";

        Element playerNameAndNumberElement = doc.selectFirst(nameClass);
        if (playerNameAndNumberElement != null) {
            String firstName = playerNameAndNumberElement.ownText().trim();

            Element lastNameElement = playerNameAndNumberElement.selectFirst("strong");

            if (lastNameElement != null) {
                String lastName = lastNameElement.text().trim();
                return String.format("%s %s", firstName, lastName);
            } else {
                return firstName;
            } //TODO vermijden van if statements in andere if statements
        }
        return "Club";
    }

    public String scrapeMarketValue(Document doc) {
        String marketValueClass = "a.data-header__market-value-wrapper";

        Element playerMarketValueElement = doc.selectFirst(marketValueClass);

        if (playerMarketValueElement != null) {
            String marketValue = playerMarketValueElement.ownText().trim();

            return String.format("Є%sm\n", marketValue);
        } //TODO market value klopt niet helemaal, verschil tussen miljoenen, miljarden en tonnen
        return "";
    }

    public int scrapeAge(Document doc) {
        String birthSelector = "span[itemprop=birthDate]";
        Element birthElement = doc.selectFirst(birthSelector);

        if (birthElement != null) {
            String birthdayText = birthElement.text().trim();
            birthdayText = birthdayText.split("\\(")[0].trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

            try {
                LocalDate birthdate = LocalDate.parse(birthdayText, formatter);

                return (int) ChronoUnit.YEARS.between(birthdate, LocalDate.now());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}