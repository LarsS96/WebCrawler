package controller;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Component
public class TransfermarktScraper {

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