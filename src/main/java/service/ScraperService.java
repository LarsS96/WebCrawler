package service;

import controller.TransfermarktScraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Player;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.PlayerRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScraperService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TransfermarktScraper transfermarktScraper;

    public void scrapeAndSavePlayer(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Player player = new Player();
            player.setName(transfermarktScraper.scrapeName(doc));
            player.setMarketValue(transfermarktScraper.scrapeMarketValue(doc));
            player.setAge(transfermarktScraper.scrapeAge(doc));

            playerRepository.save(player);
            log.info(String.format("%s has succesfully been added to the database", player.getName()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
