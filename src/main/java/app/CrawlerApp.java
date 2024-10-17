package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"app", "service", "controller"})
@EnableJpaRepositories(basePackages = "repository")
@EntityScan(basePackages = "model")
public class CrawlerApp {

    public static void main(String[] args) {
        SpringApplication.run(CrawlerApp.class, args);
    }

}
