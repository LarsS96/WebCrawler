package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import service.ScraperService;

import java.util.List;

@SpringBootApplication(scanBasePackages = {"app", "service", "scraper"})
@EnableJpaRepositories(basePackages = "repository")
@EntityScan(basePackages = "model")
public class CrawlerApp {

    private static final List<String> players = List.of("https://www.transfermarkt.com/cristiano-ronaldo/profil/spieler/8198",
            "https://www.transfermarkt.com/kylian-mbappe/profil/spieler/342229",
            "https://www.transfermarkt.com/virgil-van-dijk/profil/spieler/139208",
            "https://www.transfermarkt.com/wout-weghorst/profil/spieler/228645",
            "https://www.transfermarkt.com/erling-haaland/leistungsdaten/spieler/418560/plus/0?saison=ges");

    @Autowired
    private ScraperService scraperService;


    public static void main(String[] args) {
        SpringApplication.run(CrawlerApp.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartUp() {
        for (String url : players) {
            scraperService.scrapeAndSavePlayer(url);
        }
    }
}
//
//        scraperService.scrapeAndSavePlayer(CR7);
//        System.out.printf("Based on transfermarkt.com on %s\n\n", LocalDate.now().format
//                (DateTimeFormatter.ofPattern("dd-MM-yyyy")));
//
////        TransfermarktScraper.scrapePlayer(CR7);
////        TransfermarktScraper.scrapePlayer(KM9);
////        TransfermarktScraper.scrapePlayer(VVD);
////        TransfermarktScraper.scrapePlayer(WW);
//
//        try(Connection con = connection.connect()){
//            System.out.println("Success");
//        } catch (SQLException e){
//            e.printStackTrace();
//        }
//    }
//    //        ArrayList<WebCrawler> bots = new ArrayList<>();
////        bots.add(new WebCrawler("https://en.wikipedia.org/wiki/Open-source_intelligence", 1L));
////        bots.add(new WebCrawler("https://www.nu.nl/", 2L));
////
////        for(WebCrawler w : bots) {
////            try{
////                w.getThread().join();
////            }
////            catch (InterruptedException e){
////                e.printStackTrace();
////            }
////        }
//}
