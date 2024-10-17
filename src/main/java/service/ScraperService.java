package service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Player;
import org.springframework.stereotype.Service;
import repository.PlayerRepository;

@Service
@Slf4j
@AllArgsConstructor
public class ScraperService {

    private PlayerRepository playerRepository;

    public void savePlayer(Player player) {
        if (player != null && player.getAge() > 0 && !playerRepository.existsByName(player.getName())) {
            playerRepository.save(player);
            log.info("{} has successfully been added to the database", player.getName());
        }
    }
}