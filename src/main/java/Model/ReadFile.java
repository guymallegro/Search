package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;

/*
This class responsible for the reading of the files in the corpus contains documents and the stop words file.
*/
public class ReadFile {
    private Model model;
    private File currentFile;
    private String allFiles;
    private String postPath;

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
        for (int i = 0; i < size; i++) {
            if (allDirectories[i].equals("stop_words.txt"))
                continue;
            currentFile = new File(path + allDirectories[i]);
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

    /**
     * @param path - the path of the folder of the stop-words file
     * @return HashSet of all the stop-words appears in the file
     */
    HashSet<String> readStopWords(String path) {
        HashSet<String> stopWords = new HashSet<>();
        try {
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()) {
                String stopWord = scanner.nextLine();
                stopWords.add(stopWord.toLowerCase());
                stopWords.add(stopWord.toUpperCase());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Cannot open the file: " + path);
        }
        return stopWords;
    }


    /**
     * Find the given terms from the post files
     *
     * @param terms - The given terms
     * @return - The given terms line from posting
     */
    public ArrayList<String> findTerms(ArrayList<String> terms) {
        ArrayList<String> termsLines = new ArrayList<>();
        for (int i = 0; i < terms.size(); i++) {
            char firstChar = terms.get(i).charAt(0);
            if (!Character.isDigit(firstChar))
                currentFile = new File(postPath + "/post" + Character.toUpperCase(firstChar) + ".txt");
            else
                currentFile = new File(postPath + "/post" + ",.txt");
            try {
                InputStream is = new FileInputStream(currentFile);
                BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                String line = buf.readLine();
                while (line != null) {
                    if (line.substring(1, line.indexOf(";")).equals(terms.get(i))){
                        termsLines.add(line);
                        break;
                    }
                    line = buf.readLine();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return termsLines;
    }

    /**
     * Sets the post path
     *
     * @param postPath - The post path
     */
    void setPostPath(String postPath) {
        this.postPath = postPath;
    }

    /**
     * Reads the queries file
     *
     * @param path - Path to the queries file
     */
    void readQueriesFile(String path) {
        StringBuilder sb = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Cannot open the file: " + path);
        }
        model.processQuery(sb.toString());
    }
}