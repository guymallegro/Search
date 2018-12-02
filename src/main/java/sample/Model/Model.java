package sample.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class Model {

    private Parse parse;
    private ReadFile fileReader;
    private Indexer indexer;
    private CityChecker cityChecker;
    private int nomOfDocs = 2; //@TODO Need to find the best amount
    private int totalAmountOfDocs = 0;
    private ArrayList<Document> documents;
    static HashMap<String, ArrayList<Object>> termsDictionary;
    static HashMap<String, ArrayList<Object>> documentsDictionary;
    static HashMap<String, ArrayList<Object>> citiesDictionary;


    public Model() {
        parse = new Parse();
        cityChecker = new CityChecker();
        fileReader = new ReadFile(this);
        documents = new ArrayList<>();
        termsDictionary = new HashMap<>();
        documentsDictionary = new HashMap<>();
        citiesDictionary = new HashMap<>();
        indexer = new Indexer(parse.getAllTerms());
    }

    public void readFiles(String filesPath, String stopWordsPath) {
        long tStart = System.currentTimeMillis();
        //HashMap<String, String> cityInfo = cityChecker.findCityInformation("jerusalem");
        // System.out.println(cityInfo.get("country") + "," + cityInfo.get("currency") + "," + cityInfo.get("population"));
        parse.setStopWords(fileReader.readStopWords(stopWordsPath));
        fileReader.readFile(filesPath);
        System.out.println("--------------------------------------");
        System.out.println("-----------indexing--------------------");
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        tStart = System.currentTimeMillis();
        System.out.println("Time it took: " + elapsedSeconds + " seconds");
        indexer.mergeAllPostFiles();
        parse.getAllTerms().clear();
        System.out.println("--------------------------------------");
        System.out.println("-----------------Complete-------------");
        tEnd = System.currentTimeMillis();
        tDelta = tEnd - tStart;
        elapsedSeconds = tDelta / 1000.0;
        System.out.println("Time it took: " + elapsedSeconds + " seconds");
        System.out.println("dictionarySize: " + termsDictionary.size());
    }

    void processFile(String fileAsString) {
        String[] allDocuments = fileAsString.split("<DOC>");
        for (String document : allDocuments) {
            if (document.length() == 0 || document.equals(" ")) continue;
            Document currentDocument = new Document();
            totalAmountOfDocs++;
            nomOfDocs--;
            int startTagIndex = document.indexOf("<DOCNO>");
            int endTagIndex = document.indexOf("</DOCNO>");
            if (startTagIndex != -1 && endTagIndex != -1)
                currentDocument.setId(document.substring(startTagIndex + 7, endTagIndex));
            startTagIndex = document.indexOf("<TEXT>");
            endTagIndex = document.indexOf("</TEXT>");
            if (startTagIndex != -1 && endTagIndex != -1)
                currentDocument.setContent(document.substring(startTagIndex + 6, endTagIndex));
            startTagIndex = document.indexOf("<TI>");
            endTagIndex = document.indexOf("</TI>");
            if (startTagIndex != -1 && endTagIndex != -1)
                currentDocument.setTitle(document.substring(startTagIndex + 4, endTagIndex));
            startTagIndex = document.indexOf("<DATE>");
            endTagIndex = document.indexOf("</DATE>");
            if (startTagIndex != -1 && endTagIndex != -1)
                currentDocument.setDate(document.substring(startTagIndex + 7, endTagIndex));
            startTagIndex = document.indexOf("<F P=104>");
            endTagIndex = document.indexOf("</F>", startTagIndex);
            if (startTagIndex != -1 && endTagIndex != -1)
                currentDocument.setCity(document.substring(startTagIndex + 9, endTagIndex));
            documents.add(currentDocument);
            if (nomOfDocs < 0) {
                for (Document doc : documents) {
                    parse.parseDocument(doc);
                }
                index();
                documents.clear();
                nomOfDocs = 2;
            }
        }
    }

    private void index() {
        indexer.addAllTerms("");
        parse.getAllTerms().clear();
    }

    void finishReading() {
        for (Document doc : documents) {
            parse.parseDocument(doc);
        }
        index();
        documents.clear();
        System.out.println(totalAmountOfDocs);
    }
}