package scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public class TransfermarktScraper {

    public static void scrapePlayer(String url){
        try {
            Document doc = Jsoup.connect(url).get();
            System.out.println(scrapeName(doc));
            System.out.println(scrapeMarketValue(doc));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String scrapeName(Document doc){
        String marketValueClass = "h1.data-header__headline-wrapper";

        Element playerNameAndNumberElement = doc.selectFirst(marketValueClass);
        String firstName = playerNameAndNumberElement.ownText().trim();

        Element lastNameElement = playerNameAndNumberElement.selectFirst("strong");
        String lastName = lastNameElement.text().trim();

        return String.format("%s %s", firstName, lastName);
    }

    private static String scrapeMarketValue(Document doc){
        String marketValueClass = "a.data-header__market-value-wrapper";

        Element playerMarketValueElement = doc.selectFirst(marketValueClass);
        String marketValue = playerMarketValueElement.ownText().trim();

        return String.format("Є%sm\n",marketValue);
    }

    public static String scrapeGoals(Document doc, String url){

        Element table = doc.select("table.items").first();
        Elements zentriertElements = table.select("td.zentriert");

        return zentriertElements.get(1).text();
    }

    public static String mutateUrl(String url){
        List<String> parts = List.of(url.split("/"));

        String name = parts.get(3);
        String id = parts.get(6);

        return String.format
                ("https://www.transfermarkt.com/%s/leistungsdaten/spieler/%s/plus/0?saison=ges".trim(), name, id);
    }
}