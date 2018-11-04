package sample.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class ReadFile {
    public static Model model;

    public static void readFile(String path) throws IOException {
        File currentDirectory = new File(path);
        String[] allDirectories = currentDirectory.list();
        for (String directory : allDirectories) {
            File currentFile = new File(path + directory);
            String[] allFiles = currentFile.list();
            List<String> lines = Files.readAllLines(Paths.get(path + directory + "//" + allFiles[0]), StandardCharsets.UTF_8);
            model.processFile(lines);
            break;
        }
    }
}
