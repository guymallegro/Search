package sample.Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Indexer {
    private HashMap<String, Term> allTerms;
    private int currentPostFile;
    private BufferedWriter bw;
    private PrintWriter out;
    private char currentPartOfPostFile = '~';
    private FileWriter fw;

    public Indexer(HashMap<String, Term> allTerms) {
        currentPostFile = 0;
        this.allTerms = allTerms;
    }

    public void addAllTerms(String path) {
        path = "/home/guy/Desktop/post/";
        Object[] sortedterms = allTerms.keySet().toArray();
        Arrays.sort(sortedterms);
        StringBuilder line = new StringBuilder();
        List<String> lines = new LinkedList<>();
        for (int i = 0; i < sortedterms.length; i++) {
            int prev = 0;
            line.append("<" + sortedterms[i] + ":" + allTerms.get(sortedterms[i]).getInDocuments().length + ";");
            Object[] documentsOfTerm = allTerms.get(sortedterms[i]).getInDocuments();
            for (Object documentId : documentsOfTerm) {
                line.append((int) documentId - prev + ",");
                prev = (int) documentId;
            }
            line.deleteCharAt(line.toString().length() - 1);
            lines.add(line.toString());
            addToDictionary((String) sortedterms[i], currentPartOfPostFile);

            line.setLength(0);
        }
        path += "post" + currentPostFile + ".txt";
        Path file = Paths.get(path);
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (Exception e) {
            System.out.println("cannot write");
        }
        currentPostFile++;
    }

    public void mergeAllPostFiles() {
        Scanner[] scanners = new Scanner[currentPostFile];
        String[] currentLine = new String[currentPostFile];
        fw = null;
        String toWrite = "";
        int currentIndex = 0;
        boolean isChanged = true;
        try {
            fw = new FileWriter("/home/guy/Desktop/final.txt");
        } catch (Exception e) {
            System.out.println("Failed to create file writer");
        }
        bw = new BufferedWriter(fw);
        out = new PrintWriter(bw);
        for (int i = 0; i < currentPostFile; i++) {
            try {
                scanners[i] = new Scanner(new File("/home/guy/Desktop/post/post" + i + ".txt"));
            } catch (Exception e) {
                System.out.println("Failed to create a scanner");
            }
            currentLine[i] = scanners[i].nextLine();
        }

        while (true) {
            toWrite = "~";
            String fromCompare = "";
            for (int i = 0; i < currentPostFile; i++) {
                if (!currentLine[i].equals("~")) {
                    if (isChanged) {
                        if (!toWrite.equals("~"))
                            fromCompare = toWrite.substring(1, toWrite.indexOf(':'));
                        else
                            fromCompare = "~";
                        isChanged = false;
                    }
                    String toCompare = currentLine[i].substring(1, currentLine[i].indexOf(':'));
                    double compare = fromCompare.compareTo(toCompare);
                    if (compare > 0) {
                        toWrite = currentLine[i];
                        currentIndex = i;
                        isChanged = true;
                    } else if (compare == 0) {
                        toWrite += "," + currentLine[i].substring(currentLine[i].indexOf(';') + 1);
                        if (scanners[i].hasNext())
                            currentLine[i] = scanners[i].nextLine();
                        else {
                            currentLine[i] = "~";
                            scanners[i].close();
                        }
                    }
                }
            }
            if (scanners[currentIndex].hasNext())
                currentLine[currentIndex] = scanners[currentIndex].nextLine();
            else {
                currentLine[currentIndex] = "~";
            }
            if (!toWrite.equals("~")) {
                if (!Character.isDigit(toWrite.charAt(1)) && toWrite.charAt(1) != currentPartOfPostFile) {
                    changePostFile(toWrite.charAt(1));
                }
                out.println(toWrite);
                isChanged = true;
            } else
                break;
        }
        out.close();
    }

    private void changePostFile(char nextFile) {
        out.close();
        currentPartOfPostFile = nextFile;
        try {
            fw = new FileWriter("/home/guy/Desktop/finalPostFiles/final" + currentPartOfPostFile + ".txt");
        } catch (Exception e) {
            System.out.println("Failed to create file writer");
        }
        bw = new BufferedWriter(fw);
        out = new PrintWriter(bw);
    }

    private void addToDictionary(String term, char path) {
        if (Model.dictionary.containsKey(term)) {
            int numOfDocuments = (int) Model.dictionary.get(term).get(0);
            Model.dictionary.get(term).set(0, numOfDocuments + allTerms.get(term).getInDocuments().length);
        } else {
            ArrayList<Object> attributes = new ArrayList<>();
            attributes.add(allTerms.get(term).getInDocuments().length);
            attributes.add(path);
            Model.dictionary.put(term, attributes);
        }
    }
}
