package service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Player;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebCrawlerService {

    private final ScraperService scraperService;
    private final TransfermarktScraperService transfermarktScraperService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        crawl("https://www.transfermarkt.com/manchester-united/startseite/verein/985", 4, 3);
    }

    public void crawl(String startUrl, int maxSteps, int maxTimeInMinutes) {
        long startTime = System.currentTimeMillis();
        crawlRecursive(startUrl, maxSteps, startTime, maxTimeInMinutes);
    }

    private void crawlRecursive(String url, int stepsRemaining, long startTime, int maxTimeInMinutes) {
        if (stepsRemaining <= 0 || checkLimits(stepsRemaining, startTime, maxTimeInMinutes)) {
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
    }

    private void processUrl(String url, int stepsRemaining, long startTime, int maxTimeInMinutes) {
        try {
            Player player = transfermarktScraperService.scrapePlayer(url);
            if (player != null && player.getAge() > 0) {
                scraperService.savePlayer(player);
            } else {
                List<String> teamPlayers = transfermarktScraperService.scrapeTeamPlayers(url);
                crawlTeamPlayers(teamPlayers, stepsRemaining, startTime, maxTimeInMinutes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void crawlTeamPlayers(List<String> teamPlayers, int stepsRemaining, long startTime, int maxInMinutes) {
        for (String playerUrl : teamPlayers) {
            if (checkLimits(stepsRemaining, startTime, maxInMinutes)) {
                return;
            }
            crawlRecursive(playerUrl, stepsRemaining - 1, startTime, maxInMinutes);

            if (stepsRemaining <= 0) {
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
//TODO end of steps fixen. Time limit optimaliseren