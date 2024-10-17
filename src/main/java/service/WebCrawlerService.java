package service;

import controller.TransfermarktScraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Player;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebCrawlerService {

    private final ScraperService scraperService;
    private final TransfermarktScraper transfermarktScraper;
    private final Set<String> visitedPages = new HashSet<>();


    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        crawl("https://www.transfermarkt.com/manchester-united/startseite/verein/985", 5, 5);
    }

    public void crawl(String startUrl, int maxSteps, int maxTimeInMinutes) {
        long startTime = System.currentTimeMillis();
        crawlRecursive(startUrl, maxSteps, startTime, maxTimeInMinutes);
    }

    public void crawlRecursive(String url, int stepsRemaining, long startTime, int maxTimeInMinutes) {
        if (stepsRemaining == 0 || hasTimeElapsed(startTime, maxTimeInMinutes)) {
            return;
        }

        if (!visitedPages.contains(url)) {
            visitedPages.add(url);
            log.info("Visiting: {}", url);

            try {
                Player player = transfermarktScraper.scrapePlayer(url);
                if (player.getAge() > 0) {

                    scraperService.savePlayer(player);
                } else {
                    List<String> teamPlayers = transfermarktScraper.scrapeTeamPlayers(url);
                    for (String playerUrl : teamPlayers) {
                        crawlRecursive(playerUrl, stepsRemaining - 1, startTime, maxTimeInMinutes);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Document doc = Jsoup.connect(url).get();
            scrapeTeamPlayers(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }

        crawlRecursive(url, stepsRemaining - 1, startTime, maxTimeInMinutes);
    }
//TODO bovenstaande methode fixen

    public void scrapeTeamPlayers(Document doc) {
        Elements playerLinks = doc.select("td.hauptlink a");

        for (Element link : playerLinks) {
            String playerUrl = link.attr("href");
            playerUrl = "https://www.transfermarkt.com" + playerUrl;
            scraperService.addPlayerIfNew(playerUrl);
        }
    }

    private static boolean hasTimeElapsed(long startTime, int maxTimeInMinutes) {
        long elapsedTimeInMinutes = TimeUnit.MILLISECONDS.toMinutes
                (System.currentTimeMillis() - startTime);
        return elapsedTimeInMinutes > maxTimeInMinutes;
    }
}