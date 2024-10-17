package service;

import controller.TransfermarktScraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Player;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebCrawlerService {

    private final ScraperService scraperService;
    private final TransfermarktScraper transfermarktScraper;
    private final Set<String> visitedPages;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        crawl("https://www.transfermarkt.com/manchester-united/startseite/verein/985", 2, 1);
    }

    public void crawl(String startUrl, int maxSteps, int maxTimeInMinutes) {
        long startTime = System.currentTimeMillis();
        crawlRecursive(startUrl, maxSteps, startTime, maxTimeInMinutes);
    }

    public void crawlRecursive(String url, int stepsRemaining, long startTime, int maxTimeInMinutes) {
        if (checkLimits(stepsRemaining, startTime, maxTimeInMinutes)) {
            return;
        }

        processUrl(url, stepsRemaining, startTime, maxTimeInMinutes);
    }

    private boolean checkLimits(int stepsRemaining, long startTime, int maxTimeInMinutes) {
        if (stepsRemaining == 0) {
            log.info("Step limit reached");
            return true;
        }
        if (hasTimeElapsed(startTime, maxTimeInMinutes)) {
            log.info("Time limit reached");
            return true;
        }
        return false;
    } //TODO end of steps fixen. Time limit optimaliseren

    private void processUrl(String url, int stepsRemaining, long startTime, int maxTimeInMinutes) {
        if (visitedPages.contains(url)) {
            log.info("Already visited: {}", url);
            return;
        }

        log.info("Visiting: {}", url);

        try {
            Player player = transfermarktScraper.scrapePlayer(url);
            if (player != null && player.getAge() > 0) {
                scraperService.savePlayer(player);
            } else {
                List<String> teamPlayers = transfermarktScraper.scrapeTeamPlayers(url);
                crawlTeamPlayers(teamPlayers, stepsRemaining, startTime, maxTimeInMinutes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void crawlTeamPlayers(List<String> teamPlayers, int stepsRemaining, long startTime, int maxInMinutes) {
        stepsRemaining--;
        for (String playerUrl : teamPlayers) {
            if (checkLimits(stepsRemaining, startTime, maxInMinutes)) {
                break;
            }
            crawlRecursive(playerUrl, stepsRemaining, startTime, maxInMinutes);

            if (stepsRemaining == 0) {
                return;
            }
        }
    }

    public boolean hasTimeElapsed(long startTime, int maxTimeInMinutes) {
        long elapsedTimeInMinutes = TimeUnit.MILLISECONDS.toMinutes
                (System.currentTimeMillis() - startTime);
        return elapsedTimeInMinutes >= maxTimeInMinutes;
    }
}