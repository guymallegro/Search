package Model;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;

/*
This class responsible to
 */
public class ReadFile {
    private Model model;
    private File currentFile;
    private String allFiles;

    public ReadFile(Model model) {
        this.model = model;
    }

    public void readFile(String path, boolean onlyCities) {
        File currentDirectory = new File(path);
        String[] allDirectories = currentDirectory.list();
        for (String directory : allDirectories) {
            if (directory.equals("stop_words.txt"))
                continue;
            currentFile = new File(path + directory);
            System.out.println("Current file " + directory);
            allFiles = currentFile.list()[0];
            currentFile = new File(path + directory + "/" + allFiles);
            try {
                InputStream is = new FileInputStream(currentFile);
                BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                String line = buf.readLine();
                StringBuilder sb = new StringBuilder();
                while (line != null) {
                    sb.append(line + " ");
                    line = buf.readLine();
                }
                String fileAsString = sb.toString();
                if (onlyCities)
                    model.addCitiesToDictionary(fileAsString);
                else
                    model.processFile(fileAsString);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!onlyCities)
            model.finishReading();
    }

    public HashSet<String> readStopWords(String path) {
        HashSet<String> stopWords = new HashSet<>();
        try {
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()) {
                String stopWord = scanner.nextLine();
                stopWords.add(stopWord);
                stopWords.add(Character.toUpperCase(stopWord.charAt(0)) + stopWord.substring(1));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Cannot open the file: " + path);
        }
        return stopWords;
    }
}