package service;

import controller.TransfermarktScraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Player;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import repository.PlayerRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class ScraperService {

    private PlayerRepository playerRepository;
    private TransfermarktScraper transfermarktScraper;
    private final Set<String> visitedPages = new HashSet<>();

    public void scrapeAndSavePlayer(String url) {
        try {
            Player player = scrapePlayer(url);
            savePlayer(player);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Player scrapePlayer(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        String playername = transfermarktScraper.scrapeName(doc);

        if (playerRepository.existsByName(playername)) {
            return null;
        }
        return transfermarktScraper.scrapePlayer(url);
    }

    public void savePlayer(Player player) {
        if (player != null && player.getAge() > 0 && !playerRepository.existsByName(player.getName())) {
            playerRepository.save(player);
            log.info("{} has successfully been added to the database", player.getName());
        }
    }

    public void addPlayerIfNew(String url) {
        if (!visitedPages.contains(url)) {
            visitedPages.add(url);
            scrapeAndSavePlayer(url);
        }
    }
}