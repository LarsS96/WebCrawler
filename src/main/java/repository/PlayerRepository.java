package repository;

import model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@Component
public interface PlayerRepository extends JpaRepository<Player, Long> {
    boolean existsByName(String name);

    //TODO querys om te zoeken op ID bvb
}