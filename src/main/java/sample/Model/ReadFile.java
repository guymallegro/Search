package sample.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class ReadFile {
    private Model model;
    private File currentFile;
    private List<String> lines;
    private String allFiles;

    public ReadFile(Model model) {
        this.model = model;
    }

    public void readFile(String path) {
        File currentDirectory = new File(path);
        String[] allDirectories = currentDirectory.list();
        for (String directory : allDirectories) {
            currentFile = new File(path + directory);
            System.out.println("Current file " + directory);
            allFiles = currentFile.list()[0];
            try {
                lines = Files.readAllLines(Paths.get(path + directory + "/" + allFiles), StandardCharsets.ISO_8859_1);
            }
            catch (Exception e){
                System.out.println("Cannot open file: "+path);
            }
            model.processFile(lines);
        }
    }

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