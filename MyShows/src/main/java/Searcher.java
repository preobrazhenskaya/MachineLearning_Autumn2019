import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;

import java.io.*;
import java.util.ArrayList;

public class Searcher {
    public ArrayList<Series> search(String constant, String text, String indexDirectoryPath) throws IOException, ParseException {
        Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath).toPath());
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        QueryParser queryParser = new QueryParser(constant, new StandardAnalyzer());
        Query query = queryParser.parse(text);
        System.out.println("Search '" + query + "'");

        TopDocs docs = indexSearcher.search(query, 10000);

        long num = docs.totalHits;
        System.out.println("Number of results: " + num);

        ArrayList<Series> searchResult = new ArrayList<Series>();

        for(ScoreDoc scoreDoc : docs.scoreDocs){
            Document doc = indexSearcher.doc(scoreDoc.doc);
            Series series = new Series(doc.get(Constants.NAME), doc.get(Constants.HREF));
            searchResult.add(series);
        }
        return searchResult;
    }
}