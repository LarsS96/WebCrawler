import java.util.ArrayList;

public class CrawlerApp {


    public static void main(String[] args) {
        ArrayList<WebCrawler> bots = new ArrayList<>();
        bots.add(new WebCrawler("https://en.wikipedia.org/wiki/Open-source_intelligence", 1L));
        bots.add(new WebCrawler("https://www.nu.nl/", 2L));

        for(WebCrawler w : bots) {
            try{
                w.getThread().join();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
