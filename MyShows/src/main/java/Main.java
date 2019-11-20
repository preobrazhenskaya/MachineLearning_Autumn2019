import org.apache.lucene.document.IntPoint;
import org.apache.lucene.queryparser.classic.ParseException;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    private static String indexPath = "index";
    private static FileWriter dataFile;

    public static String findValue(String str, String start, char end) {
        String rez = "";
        int pos = str.indexOf(start);
        pos += start.length();
        while (str.charAt(pos) != end) {
            rez += str.charAt(pos);
            pos++;
        }
        return rez;
    }

    public static void loadData(ArrayList<Series> seriesArray) throws InterruptedException, IOException {
        Document doc = null;
        ArrayList<Series> tempArray = new ArrayList<Series>();
        System.out.println("=== Parsing start ===");
        for (int i = 0; i <= 334; i++) {
            tempArray.clear();
            doc = Jsoup.connect("https://myshows.me/search/all/?page=" + i).userAgent("Yandex").get();
            Elements currentpagedata = doc.select("body > div.wrapper > div.container.content > div > main > table > tbody > tr");
            currentpagedata.remove(0);
            for (Element el : currentpagedata) {
                Document seriesDoc = null;
                String href = findValue(el.toString(),
                        "href=\"https://myshows.me/view",
                        '\"');
                String name = findValue(el.toString(),
                        "href=\"https://myshows.me/view" + href + "\">",
                        '<');
                Integer rating = Integer.parseInt(findValue(el.toString(),
                        "span class=\"stars _",
                        '\"'));
                seriesDoc = Jsoup.connect("https://myshows.me/view" + href).userAgent("Yandex").get();
                Element mainSeriesInformation = seriesDoc.selectFirst("body > div.wrapper > div > div > main");
                String description = findValue(mainSeriesInformation.toString(),
                        "<div class=\"col5\"> \n" +
                                "   <h3>Описание</h3> \n" +
                                "   <p>",
                        '<');
                Series series = new Series(name, "https://myshows.me/view" + href, rating, description);
                tempArray.add(series);
                System.out.println("\"" + name + "\" add in list");
            }
            Indexer indexer = new Indexer(indexPath);
            indexer.createIndex(tempArray);
            for (Series s : tempArray) {
                seriesArray.add(s);
            }
            System.out.println("--- " + i + " page ready ---");
        }
        Reader.writeToFile(seriesArray, "data/series.txt");
        System.out.println("=== Parsing finish ===\n");
    }

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        ArrayList<Series> seriesArray = new ArrayList<Series>();
        loadData(seriesArray);    // Uncommit for load data
        System.out.println("=== Searching start ===");
        Searcher searcher = new Searcher();
        ArrayList<Series> byName = searcher.search(Constants.NAME, "Шерлок", indexPath);
        System.out.println("Results:");
        for (Series series : byName) {
            System.out.println(" " + series.name);
        }
        System.out.println("=== Searching finish ===\n");
    }
}



