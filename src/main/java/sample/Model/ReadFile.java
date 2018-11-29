package sample.Model;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class ReadFile {
    private Model model;
    private File currentFile;
    private String allFiles;
    private ArrayList <Document> documents;
    private Document currentDocument;
    private int numOfDocs = 1000;
    private int total;

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
                InputStream is = new FileInputStream(currentFile);
                BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                String line = buf.readLine();
                StringBuilder sb = new StringBuilder();
                while(line != null){
                    sb.append(line + " ");
                    //sb.append(line).append("\n");
                    line = buf.readLine();
                }
                String fileAsString = sb.toString();
                String [] allDocuments = fileAsString.split("<DOC>");
                for (String document: allDocuments) {
                    if (document.length() == 0 || document.equals(" ") ) continue;
                    currentDocument = new Document();
                    total++;
                    numOfDocs--;
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
                }
                    if (numOfDocs < 0){
                        model.processFile(documents);
                        model.index();
                        documents.clear();
                        numOfDocs = 1000;
                    }
                } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace(); }
        }
        if (numOfDocs != 1000) {
            model.processFile(documents);
            model.index();
        }
        documents.clear();
        System.out.println(total);
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

    public HashSet<String> readStopWords(String path) {
        HashSet<String> stopWords = new HashSet<>();
        File file = new File(path);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String stopWord = scanner.nextLine();
                stopWords.add(stopWord);
                stopWords.add(Character.toUpperCase(stopWord.charAt(0)) + stopWord.substring(1));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Cannot open the file: " + path);
        }
        return stopWords;
    }

}