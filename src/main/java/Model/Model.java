package Model;

import Controller.Controller;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Model {

    private Parse parse;
    private ReadFile fileReader;
    private Indexer indexer;
    private CityChecker cityChecker;
    private String postingPathDestination;
    private int nomOfDocs = 11800; //@TODO Need to find the best amount
    private int totalAmountOfDocs = 0;
    private HashSet<String> languages;
    private ArrayList<Document> documents;
    private HashSet<String> stopWords;
    static HashMap<String, ArrayList<Object>> termsDictionary;
    static HashMap<Integer, ArrayList<Object>> documentsDictionary;
    private boolean isStemming = false;
    static HashMap<String, CityInfo> citiesDictionary;

    public Model() {
        parse = new Parse();
        citiesDictionary = new HashMap<>();
        cityChecker = new CityChecker(Main.citiesUrl, citiesDictionary);
        fileReader = new ReadFile(this);
        documents = new ArrayList<>();
        languages = new HashSet<>();
        termsDictionary = new HashMap<>();
        documentsDictionary = new HashMap<>();
        indexer = new Indexer(parse.getAllTerms(), documents);
    }

    public void readFiles(String filesPath, String stopWordsPath, String postingpath) {
        postingPathDestination = postingpath;
        long tStart = System.currentTimeMillis();
        stopWords = fileReader.readStopWords(stopWordsPath);
        parse.setStopWords(stopWords);
        fileReader.readFile(filesPath, true);
        Object[] cities = citiesDictionary.keySet().toArray();
        printCities(cities);
        fileReader.readFile(filesPath, false);
        System.out.println("--------------------------------------");
        System.out.println("--------------indexing-----------------");
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        double elapsedSeconds = tDelta / 1000.0;
        tStart = System.currentTimeMillis();
        System.out.println("Time it took: " + elapsedSeconds + " seconds");
        indexer.mergeAllPostFiles();
        parse.getAllTerms().clear();
        writeDictionary ();
        System.out.println("--------------------------------------");
        System.out.println("-----------------Complete-------------");
        tEnd = System.currentTimeMillis();
        tDelta = tEnd - tStart;
        elapsedSeconds = tDelta / 1000.0;
        System.out.println("Time it took: " + elapsedSeconds + " seconds");
        System.out.println("dictionarySize: " + termsDictionary.size());
        System.out.println("Doc 10 info :" + documentsDictionary.get(10).get(0) + "," + documentsDictionary.get(10).get(1) + "," + documentsDictionary.get(10).get(2));
    }

    private void writeDictionary() {
        Object[] sortedTerms = termsDictionary.keySet().toArray();
        Arrays.sort(sortedTerms);
        StringBuilder line = new StringBuilder();
        List<String> lines = new LinkedList<>();
        int size = sortedTerms.length;
        for (int i = 1; i < size; i++) {
            line.append(sortedTerms[i]);
            line.append(" (");
            line.append(termsDictionary.get(sortedTerms[i]).get(0));
            line.append(")");
            lines.add(line.toString());
            line.setLength(0);
        }
        String path = postingPathDestination + "/termsDictionary.txt";
        if (isStemming)
            path = postingPathDestination + "/termsDictionaryWithStemming.txt";
        Path file = Paths.get(path);
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (Exception e) {
            System.out.println("cannot write to dictionary");
        }
        termsDictionary.clear();
    }

    private void printCities(Object[] cities) {
        for (int i = 0; i < cities.length; i++) {
            System.out.println(cities[i] + " : " + citiesDictionary.get(cities[i]));
        }
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
            startTagIndex = document.indexOf("<F P=105>");
            endTagIndex = document.indexOf("</F>", startTagIndex);
            if (startTagIndex != -1 && endTagIndex != -1) {
                String ans = cleanString(document.substring(startTagIndex + 9, endTagIndex));
                if (ans.length() > 0)
                    languages.add(ans);
            }
            documents.add(currentDocument);
            if (nomOfDocs < 0) {
                for (Document doc : documents) {
                    parse.parseDocument(doc);
                }
                index();
                documents.clear();
                nomOfDocs = 11800;
            }
        }
    }

    public void addCitiesToDictionary(String fileAsString) {
        String[] allDocuments = fileAsString.split("<DOC>");
        for (String document : allDocuments) {
            if (document.length() == 0 || document.equals(" ")) continue;
            int startTagIndex = document.indexOf("<F P=104>");
            int endTagIndex = document.indexOf("</F>", startTagIndex);
            if (startTagIndex != -1 && endTagIndex != -1)
                addCityToDictionary(document.substring(startTagIndex + 9, endTagIndex));
        }
    }

    private void addCityToDictionary(String city) {
        if (city.contains(" ")) {
            int counter;
            for (counter = 0; counter < city.length(); counter++) {
                if (city.charAt(counter) != ' ')
                    break;
            }
            city = city.substring(counter);
            city = city.substring(0, city.indexOf(' '));
        }
        if (city.length() > 1 && !stopWords.contains(city)) {
            if (!citiesDictionary.containsKey(city) && !stopWords.contains(city.toLowerCase())) {
                citiesDictionary.put(city, cityChecker.getCityInfo(city));
            }
        }
    }

    private void index() {
        indexer.addAllTerms(postingPathDestination);
        indexer.addAllDocuments();
        parse.getAllTerms().clear();
    }

    void finishReading() {
        int size = documents.size();
        for (int i = 0; i < size; i++)
            parse.parseDocument(documents.get(i));
        index();
        indexer.addAllCities(postingPathDestination);
        documents.clear();
        System.out.println(totalAmountOfDocs);
    }

    public void setStemming(boolean selected) {
        isStemming = selected;
        indexer.setStemming(selected);
        parse.setStemming(selected);
    }

    private String cleanString(String str) {
        if (str.length() == 0)
            return "";
        char current = str.charAt(0);
        while (!(Character.isLetter(current))) {
            if (str.length() == 1) {
                return "";
            } else {
                str = str.substring(1);
                current = str.charAt(0);
            }
        }
        current = str.charAt(str.length() - 1);
        while (!(Character.isLetter(current))) {
            if (str.length() == 1) {
                return "";
            } else {
                str = str.substring(0, str.length() - 1);
                current = str.charAt(str.length() - 1);
            }
        }
        return str;
    }

    public HashSet<String> getLanguages() { return languages; }

    public static HashMap<String, ArrayList<Object>> getTermsDictionary() { return termsDictionary; }

    public void resetDictionaries() {
        termsDictionary.clear();
        documentsDictionary.clear();
        citiesDictionary.clear();
    }
}