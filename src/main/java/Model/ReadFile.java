package Model;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;

/*
This class responsible for the reading of the files in the corpus contains documents and the stop words file.
*/
public class ReadFile {
    private Model model;
    private File currentFile;
    private String allFiles;

    /**
     * The default constructor
     *
     * @param model - The model
     */
    ReadFile(Model model) {
        this.model = model;
    }

    /**
     * Reads the corpus and sends each file as a string to the model. If onlyCities is true when the model's add cities
     * to dictionary function is called instead.
     *
     * @param path       - The path to the corpus
     * @param onlyCities - If the focus is only on the cities
     */
    void readCorpus(String path, boolean onlyCities) {
        File currentDirectory = new File(path);
        String[] allDirectories = currentDirectory.list();
        int size = allDirectories.length;
        for (int i = 0; i < size; i ++) {
            if (allDirectories[i].equals("stop_words.txt"))
                continue;
            currentFile = new File(path + allDirectories[i]);
            System.out.println("Current file " + allDirectories[i]);
            allFiles = currentFile.list()[0];
            currentFile = new File(path + allDirectories[i] + "/" + allFiles);
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