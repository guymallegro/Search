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
                    line = buf.readLine();
                }
                String fileAsString = sb.toString();
                model.processFile(fileAsString);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace(); }
        }
        model.finishReading();
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