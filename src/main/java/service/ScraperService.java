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

@Service
@Slf4j
@AllArgsConstructor
public class ScraperService {

    private PlayerRepository playerRepository;

    private TransfermarktScraper transfermarktScraper;

    public void scrapeAndSavePlayer(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            String playerName = transfermarktScraper.scrapeName(doc);

            if (playerRepository.existsByName(playerName)) {
                return;
            }
            Player player = new Player();
            player.setName(transfermarktScraper.scrapeName(doc));
            player.setMarketValue(transfermarktScraper.scrapeMarketValue(doc));
            player.setAge(transfermarktScraper.scrapeAge(doc));

            if (player.getAge() > 0) {
                playerRepository.save(player);
                log.info(String.format("%s has succesfully been added to the database", player.getName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
