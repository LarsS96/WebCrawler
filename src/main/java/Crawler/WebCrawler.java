package Crawler;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import static org.jsoup.Jsoup.connect;

public class WebCrawler implements Runnable {
    private static final int MAX_STEPS = 5;
    private final Thread thread;
    private final String firstLink;
    private final ArrayList<String> visitedLinks = new ArrayList<>();
    private final Long id;


    public WebCrawler(String link, Long number) {
        System.out.println("Crawler.WebCrawler created successfully");
        firstLink = link;
        id = number;

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        crawl(1, firstLink);

    }

    private void crawl(int level, String url) {
        if (level <= MAX_STEPS) {
            Document doc = request(url);

            if (doc != null) {
                for (Element link : doc.select("a[href]")){
                    String nextLink = link.absUrl("href");
                    if (!visitedLinks.contains(nextLink)) {
                        crawl(level++, nextLink);
                    }
                }
            }
        }
    }

    private Document request(String url){
        try {
            Connection connection = connect(url);
            Document document = connection.get();

            if (connection.response().statusCode() == 200){
                System.out.printf("\n**Bot ID: %d received message %s", id, url);

                String title = document.title();
                System.out.println(title);
                visitedLinks.add(url);

                System.out.println(visitedLinks.size());

                return document;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Thread getThread() {
        return thread;
    }
}