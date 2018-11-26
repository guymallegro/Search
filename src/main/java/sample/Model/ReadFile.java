package sample.Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class ReadFile {
    private Model model;
    private File currentFile;
    private List<String> lines;
    private String allFiles;
    private ArrayList <Document> documents;
    private Document currentDocument;
    private int numOfDocs = 1000;

    public ReadFile(Model model) {
        this.model = model;
        documents = new ArrayList<>();
    }

    public void readFile(String path) {
        File currentDirectory = new File(path);
        String[] allDirectories = currentDirectory.list();
        for (String directory : allDirectories) {
            currentFile = new File(path + directory);
            System.out.println("Current file " + directory);
            allFiles = currentFile.list()[0];
            currentFile = new File(path + directory + "/" + allFiles);
            try {
                org.jsoup.nodes.Document doc = Jsoup.parse(currentFile, "UTF-8");
                Elements docs = doc.select("DOC");
                for (Element element : docs) {
                    currentDocument = new Document();
                    numOfDocs --;
                    currentDocument.setId(element.select("DOCNO").text());
                    currentDocument.setContent(element.select("TEXT").text());
                    currentDocument.setTitle(element.select("TITLE").text());
                    currentDocument.setDate(element.select("DATE").text());
                    currentDocument.setCity(findCity(element.outerHtml()).toUpperCase());
                    documents.add(currentDocument);
                    if (numOfDocs < 0){
                        model.index();
                        documents.clear();
                        numOfDocs = 1000;
                    }
                }
            } catch (Exception e) {
                System.out.println("big problem with reader");
            }
            model.processFile(documents);
        }
        model.index();
        documents.clear();
    }

    private String findCity(String city) {
        int position;
        for (position = 0; position < city.length(); position++) {
            if (city.charAt(position) != ' ')
                break;
        }
        city = city.substring(position);
        if (city.contains(" "))
            city = city.substring(0, city.indexOf(" "));
        return city;
    }


//    public void readFile(String path) {
//        File currentDirectory = new File(path);
//        String[] allDirectories = currentDirectory.list();
//        for (String directory : allDirectories) {
//            currentFile = new File(path + directory);
//            System.out.println("Current file " + directory);
//            allFiles = currentFile.list()[0];
//            try {
//                lines = Files.readAllLines(Paths.get(path + directory + "/" + allFiles), StandardCharsets.ISO_8859_1);
//            }
//            catch (Exception e){
//                System.out.println("Cannot open file: "+path);
//            }
//            model.processFile(lines);
//        }
//    }

    public HashSet<String> readStopWords(String path) {
        HashSet<String> stopWords = new HashSet<>();
        File file = new File(path);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                stopWords.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Cannot open the file: " + path);
        }
        return stopWords;
    }

}