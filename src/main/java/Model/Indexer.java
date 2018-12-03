package Model;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class Indexer {
    private HashMap<String, Term> allTerms;
    private ArrayList<Document> documents;
    private int currentPostFile;
    private BufferedWriter bw;
    private PrintWriter out;
    private char currentPartOfPostFile = '~';
    private FileWriter fw;
    private String postingPath;
    private boolean isStemming;

    Indexer(HashMap<String, Term> allTerms, ArrayList<Document> documents) {
        currentPostFile = 0;
        this.allTerms = allTerms;
        this.documents = documents;
    }

    void addAllTerms(String path) {
        postingPath = path;
        int indexOfPosting = currentPostFile;
        Object[] sortedterms = allTerms.keySet().toArray();
        Arrays.sort(sortedterms);
        StringBuilder line = new StringBuilder();
        List<String> lines = new LinkedList<>();
        for (Object sortedterm : sortedterms) {
            int prev = 0;
            line.append("<").append(sortedterm).append(";");
            Object[] documentsOfTerm = allTerms.get(sortedterm).getInDocuments();
            for (Object documentId : documentsOfTerm) {
                line.append((int) documentId).append(",");
                prev = (int) documentId;
            }
            line.deleteCharAt(line.toString().length() - 1);
            lines.add(line.toString());
            addTermToDictionary((String) sortedterm, currentPartOfPostFile);
            line.setLength(0);
        }
        path += "/post" + currentPostFile + ".txt";
        Path file = Paths.get(path);
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (Exception e) {
            System.out.println("cannot write");
        }
        currentPostFile++;
    }

    void mergeAllPostFiles() {
        Scanner[] scanners = new Scanner[currentPostFile];
        String[] currentLine = new String[currentPostFile];
        fw = null;
        StringBuilder toWrite;
        int currentIndex = 0;
        boolean isChanged = true;
        String lastPosting = "/final.txt";
        if (isStemming)
            lastPosting = "/finalWithStemming.txt";
        try {
            fw = new FileWriter(postingPath + lastPosting);
        } catch (Exception e) {
            System.out.println("Failed to create file writer");
        }
        bw = new BufferedWriter(fw);
        out = new PrintWriter(bw);
        for (int i = 0; i < currentPostFile; i++) {
            try {
                scanners[i] = new Scanner(new File(postingPath + "/post" + i + ".txt"));
            } catch (Exception e) {
                System.out.println("Failed to create a scanner");
            }
            try {
                currentLine[i] = scanners[i].nextLine();
            } catch (Exception e) {
                currentPostFile--;
            }
        }
        while (true) {
            toWrite = new StringBuilder("~");
            String fromCompare = "";
            for (int i = 0; i < currentPostFile; i++) {
                if (!currentLine[i].equals("~")) {
                    if (isChanged) {
                        if (!toWrite.toString().equals("~"))
                            fromCompare = toWrite.substring(1, toWrite.toString().indexOf(';'));
                        else
                            fromCompare = "~";
                        isChanged = false;
                    }
                    String toCompare = currentLine[i].substring(1, currentLine[i].indexOf(';'));
                    double compare = fromCompare.compareTo(toCompare);
                    if (compare > 0) {
                        toWrite = new StringBuilder(currentLine[i]);
                        currentIndex = i;
                        isChanged = true;
                    } else if (compare == 0) {
                        toWrite.append(",").append(currentLine[i].substring(currentLine[i].indexOf(';') + 1));
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
            if (!toWrite.toString().equals("~")) {
                if (!Character.isDigit(toWrite.charAt(1)) && toWrite.charAt(1) != currentPartOfPostFile) {
                    //      changePostFile(toWrite.charAt(1));
                }
                out.println(toWrite.toString());
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
            fw = new FileWriter(postingPath + "/post" + currentPartOfPostFile + ".txt");
        } catch (Exception e) {
            System.out.println("Failed to create file writer");
        }
        bw = new BufferedWriter(fw);
        out = new PrintWriter(bw);
    }

    private void addTermToDictionary(String term, char path) {
        if (Model.termsDictionary.containsKey(term)) {
            int numOfDocuments = (int) Model.termsDictionary.get(term).get(0);
            Model.termsDictionary.get(term).set(0, numOfDocuments + allTerms.get(term).getInDocuments().length);
        } else {
            ArrayList<Object> attributes = new ArrayList<>();
            attributes.add(allTerms.get(term).getInDocuments().length);
            attributes.add(path);
            Model.termsDictionary.put(term, attributes);
        }
    }

    void addAllDocuments() {
        for (Document doc : documents) {
            ArrayList<Object> attributes = new ArrayList<>();
            attributes.add(0,doc.getMax_tf());
            attributes.add(1,doc.getTextTerms().size());
            attributes.add(2,doc.getCity());
            Model.documentsDictionary.put(doc.getIndexId(), attributes);
        }
    }

    public void setStemming(boolean stemming) { isStemming = stemming; }
}
