package service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebCrawlerService {

    private final ScraperService scraperService;


    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        crawl("https://www.transfermarkt.com/manchester-united/startseite/verein/985", 2, 5);
    }

    public void crawl(String startUrl, int maxSteps, int maxTimeInMinutes) {
        long startTime = System.currentTimeMillis();
        crawlRecursive(startUrl, maxSteps, startTime, maxTimeInMinutes);
    }

    public void crawlRecursive(String url, int stepsRemaining, long startTime, int maxTimeInMinutes) {
        if (stepsRemaining == 0 || checkTimesUp(startTime, maxTimeInMinutes)) {
            return;
        }
        scraperService.addPlayerIfNew(url);

        try {
            Document doc = Jsoup.connect(url).get();
            scrapeTeamPlayers(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }

        crawlRecursive(url, stepsRemaining--, startTime, maxTimeInMinutes);
        log.info(String.valueOf(stepsRemaining));
    }

    public void scrapeTeamPlayers(Document doc) {
        Elements playerLinks = doc.select("td.hauptlink a");

        for (Element link : playerLinks) {
            String playerUrl = link.attr("href");
            playerUrl = "https://www.transfermarkt.com" + playerUrl;
            scraperService.scrapeAndSavePlayer(playerUrl);
            log.info(playerUrl);
        }
    }

    private static boolean checkTimesUp(long startTime, int maxTimeInMinutes) {
        long elapsedTimeInMinutes = TimeUnit.MILLISECONDS.toMinutes
                (System.currentTimeMillis() - startTime);
        return elapsedTimeInMinutes > maxTimeInMinutes;
    }
}