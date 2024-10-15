package service;

import lombok.RequiredArgsConstructor;
import model.Player;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.PlayerRepository;
import scraper.TransfermarktScraper;

import java.io.IOException;

@Service
@RequiredArgsConstructor
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

            playerRepository.save(player);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
