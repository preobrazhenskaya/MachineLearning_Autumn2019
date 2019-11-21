import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;

import java.io.*;
import java.util.ArrayList;

public class Searcher {
    public ArrayList<Series> search(String constant, String text, String indexDirectoryPath, Query inputQuery) throws IOException, ParseException {
        Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath).toPath());
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Query query = null;
        if (inputQuery == null) {
            QueryParser queryParser = new QueryParser(constant, new StandardAnalyzer());
            query = queryParser.parse(text);
        } else {
            query = inputQuery;
        }
        System.out.println("\nSearch '" + query + "'");

        TopDocs docs = indexSearcher.search(query, 10000);

        long num = docs.totalHits;
        System.out.println("Number of results: " + num);

        ArrayList<Series> searchResult = new ArrayList<Series>();

        for(ScoreDoc scoreDoc : docs.scoreDocs){
            Document doc = indexSearcher.doc(scoreDoc.doc);
            Integer rating = 0;
            if ( doc.get(Constants.RATING) != null) {
                rating = Integer.valueOf(doc.get(Constants.RATING));
            }
            Series series = new Series(doc.get(Constants.NAME), doc.get(Constants.HREF), rating, doc.get(Constants.DESCRIPTION));
            searchResult.add(series);
        }
        return searchResult;
    }

    public ArrayList<Series> search(String constant, String lowerValue, String upperValue) throws IOException, ParseException {
        Integer lowerRating =  Integer.parseInt(lowerValue);
        Integer upperRating = Integer.parseInt(upperValue);
        Query query = IntPoint.newRangeQuery(constant, lowerRating, upperRating);
        ArrayList<Series> series = search(null, null, "index", query);
        return series;
    }

    public ArrayList<Series> searchBySynonym(String text) throws ParseException, IOException {
        QueryParser queryParser = new QueryParser(Constants.NAME, new SynonymAnalyzer());
        Query query = queryParser.parse(text);
        ArrayList<Series> series = search(null,null,"index", query);
        return series;
    }
}