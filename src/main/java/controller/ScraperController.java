package controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.ScraperService;

@RestController
@RequiredArgsConstructor
public class ScraperController {

    private ScraperService scraperService;

    public ScraperController(ScraperService scraperService) {
        this.scraperService = scraperService;
    }

    @PostMapping("/player")
    public ResponseEntity<String> scrapePlayer(@RequestParam String url) {
        scraperService.scrapeAndSavePlayer(url);
        return ResponseEntity.ok("Player has been saved to the database");
    }
}
