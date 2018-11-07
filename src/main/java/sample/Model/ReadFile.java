package sample.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ReadFile {

    private Model model;

    public ReadFile(Model model) {
        this.model = model;
    }

    public void readFile(String path){
        try {
            File currentDirectory = new File(path);
            String[] allDirectories = currentDirectory.list();
            for (String directory : allDirectories) {
                File currentFile = new File(path + directory);
                System.out.println("CUrrent " + directory);
                String allFiles = Objects.requireNonNull(currentFile.list())[0];
                List<String> lines = Files.readAllLines(Paths.get(path + directory + "/" + allFiles), StandardCharsets.UTF_8);
                model.processFile(lines);
            }
        }catch (Exception e){
            System.out.println("Failed at file ");
        }
    }

    public HashSet<String> readStopWords() {
        HashSet<String> stopWords = new HashSet<>();
        File file = new File("/home/guy/Desktop/stop_words");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                stopWords.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
        return stopWords;
    }

}