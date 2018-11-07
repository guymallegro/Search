package sample.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class ReadFile {

    private Model model;

    public ReadFile(Model model){
        this.model=model;
    }

    public void readFile(String path) throws IOException {
        File currentDirectory = new File(path);
        String[] allDirectories = currentDirectory.list();
        for (String directory : allDirectories) {
            File currentFile = new File(path + directory);
            String[] allFiles = currentFile.list();
            List<String> lines = Files.readAllLines(Paths.get(path + directory +"/"+ allFiles[0]), StandardCharsets.UTF_8);
            model.processFile(lines);
            break;
        }
    }

    public HashSet<String> readStopWords() {
        HashSet<String> stopWords = new HashSet<>();
        File file = new File("stop_words.txt");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                stopWords.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
        }
        return stopWords;
    }

}