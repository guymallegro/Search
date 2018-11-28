package sample.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Model {

    private Parse parse;
    private ReadFile fileReader;
    private Indexer indexer;

    public Model() {
        parse = new Parse();
        indexer = new Indexer();
        fileReader = new ReadFile(this);
    }

    public void readFiles(String filesPath, String stopWordsPath) {
        long tStart = System.currentTimeMillis();
        parse.setStopWords(fileReader.readStopWords(stopWordsPath));
        fileReader.readFile(filesPath);
        System.out.println("--------------------------------------");
        System.out.println("-----------------Complete-------------");
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        System.out.println("Time it took: " + elapsedSeconds + " seconds");
    }

    void processFile(ArrayList<Document> documents) {
        for (Document document : documents) {
            parse.parseDocument(document);
        }
    }

    public void index (){
        indexer.addAllTerms(parse.getAllTerms(), "");
        parse.getAllTerms().clear();
    }
}