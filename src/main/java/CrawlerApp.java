import scraper.TransfermarktScraper;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CrawlerApp {

    private static final String CR7 = "https://www.transfermarkt.com/cristiano-ronaldo/profil/spieler/8198";
    private static final String KM9 = "https://www.transfermarkt.com/kylian-mbappe/profil/spieler/342229";
    private static final String VVD = "https://www.transfermarkt.com/virgil-van-dijk/profil/spieler/139208";
    private static final String WW = "https://www.transfermarkt.com/wout-weghorst/profil/spieler/228645";
    private static final String ERLING = "https://www.transfermarkt.com/erling-haaland/leistungsdaten/spieler/418560/plus/0?saison=ges";

    public static void main(String[] args) {

        System.out.printf("Based on transfermarkt.com on %s\n\n", LocalDate.now().format
                (DateTimeFormatter.ofPattern("dd-MM-yyyy")));


        TransfermarktScraper.scrapePlayer(CR7);
        TransfermarktScraper.scrapePlayer(KM9);
        TransfermarktScraper.scrapePlayer(VVD);
        TransfermarktScraper.scrapePlayer(WW);

        try(Connection con = DatabaseConnection.connect()){
            System.out.println("Success");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }











    //        ArrayList<WebCrawler> bots = new ArrayList<>();
//        bots.add(new WebCrawler("https://en.wikipedia.org/wiki/Open-source_intelligence", 1L));
//        bots.add(new WebCrawler("https://www.nu.nl/", 2L));
//
//        for(WebCrawler w : bots) {
//            try{
//                w.getThread().join();
//            }
//            catch (InterruptedException e){
//                e.printStackTrace();
//            }
//        }
}
