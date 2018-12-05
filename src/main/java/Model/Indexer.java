package Model;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class Indexer {
    private HashMap<String, Term> allTerms;
    private HashMap<String, String> capitalLetters;
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
        capitalLetters = new HashMap<>();
    }

    void addAllTerms(String path) {
        postingPath = path;
        Object[] sortedTerms = allTerms.keySet().toArray();
        Arrays.sort(sortedTerms);
        StringBuilder line = new StringBuilder();
        List<String> lines = new LinkedList<>();
        for (Object sortedTerm : sortedTerms) {
            line.append("<").append(sortedTerm).append("^");
            Object[] documentsOfTerm = allTerms.get(sortedTerm).getInDocuments();
            int size = documentsOfTerm.length;
            addTermToDictionary((String) sortedTerm, currentPartOfPostFile);
            line.append((int)documentsOfTerm[0]).append(";");
            line.append((int)documentsOfTerm[0]).append(",");
            for (int i = 1; i < size; i++) {
                line.append((int)documentsOfTerm[i] - (int)documentsOfTerm[i - 1]).append(",");
            }
            line.deleteCharAt(line.toString().length() - 1);
            line.append(";").append((int)documentsOfTerm[size - 1]);
            lines.add(line.toString());
            line.setLength(0);
        }
        if (isStemming)
            path += "/post" + currentPostFile + "WithStemming.txt";
        else
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
                if (isStemming)
                    scanners[i] = new Scanner(new File(postingPath + "/post" + i + "WithStemming.txt"));
                else
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
                            fromCompare = toWrite.substring(1, toWrite.toString().indexOf('^'));
                        else
                            fromCompare = "~";
                        isChanged = false;
                    }
                    String toCompare = currentLine[i].substring(1, currentLine[i].indexOf('^'));
                    double compare = fromCompare.compareTo(toCompare);
                    if (compare > 0) {
                        toWrite = new StringBuilder(currentLine[i]);
                        currentIndex = i;
                        isChanged = true;
                    } else if (compare == 0) {
                        toWrite = calculateGaps (toWrite.toString(), currentLine[i]);
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
                String current = toWrite.toString().substring(1, toWrite.toString().indexOf('^'));
                if (Character.isUpperCase(current.charAt(0))){
                    if (Model.termsDictionary.containsKey(current.toLowerCase())) {
                        capitalLetters.put(current.toLowerCase(), toWrite.toString().toLowerCase());
                        int toAdd = (int)Model.termsDictionary.get(current).get(0);
                        int amount = (int)Model.termsDictionary.get(current.toLowerCase()).get(0);
                        Model.termsDictionary.get(current.toLowerCase()).set(0, amount + toAdd);
                        Model.termsDictionary.remove(current);
                        toWrite.setLength(0);
                    }
                }
                else if (Character.isLowerCase(current.charAt(0))){
                    if (capitalLetters.containsKey(current))
                        toWrite = calculateGaps(capitalLetters.get(current), toWrite.toString());
                }
                toWrite = lastLineVersion(toWrite.toString());
                if (toWrite.length() != 0)
                    out.println(toWrite.toString());
                isChanged = true;
            } else
                break;
        }
        for (int i = 0; i < scanners.length; i++) {
            scanners[i].close();
        }
        out.close();
    }

    private StringBuilder lastLineVersion (String line){
        StringBuilder ans = new StringBuilder();
        if (line.length() == 0)
            return ans;
        String term = line.substring(0, line.indexOf("^"));
        ans.append(term);
        String temp = line.substring(line.indexOf(";"), line.lastIndexOf(";"));
        if (temp.equals(""))
            ans.append(line.substring(line.lastIndexOf(";") + 1));
        else
            ans.append(temp);
        return ans;
    }

    private StringBuilder calculateGaps(String toWrite, String next) {
        String [] term = toWrite.split(";");
        StringBuilder ans = new StringBuilder(term[0]);
        ans.append(";");
        if (!term[1].equals(""))
            ans.append(term[1]).append(",");
        String firstDoc = next.substring(next.indexOf("^") + 1, next.indexOf(";"));
        ans.append(Integer.parseInt(firstDoc) - Integer.valueOf(term[2]));
        int comma = next.indexOf(",", next.indexOf(";"));
        if (comma != -1) {
            ans.append(next.substring(comma));
        }
        else {
            ans.append(next.substring(next.lastIndexOf(";")));
        }
        return ans;
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
        int size = documents.size();
        for (int i = 0; i < size; i ++) {
            ArrayList<Object> attributes = new ArrayList<>();
            attributes.add(0, documents.get(i).getMax_tf());
            attributes.add(1, documents.get(i).getTextTerms().size());
            attributes.add(2, documents.get(i).getCity());
            Model.documentsDictionary.put(documents.get(i).getIndexId(), attributes);
        }
    }

    public void addAllCities(String path) {
        postingPath = path;
        if (isStemming)
            path += "/postCitiesWithStemming.txt";
        else
            path += "/postCities.txt";
        Path file = Paths.get(path);
        ArrayList<String> toPrint = new ArrayList<>();
        Object[] sortedTerms = Model.citiesDictionary.keySet().toArray();
        Arrays.sort(sortedTerms);
        int size = sortedTerms.length;
        for (int i = 0; i < size; i++) {
            String key = (String) sortedTerms[i];
            String value = "";
            if (Model.citiesDictionary.get(key).getCurrency() != null && Model.citiesDictionary.get(key).getLocationsInDocuments().size() == 0) {
                Model.citiesDictionary.put(key, null);
                continue;
            } else {
                Iterator it = Model.citiesDictionary.get(key).getLocationsInDocuments().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    value += pair.getKey() + " [ " + pair.getValue() + "]";
                    it.remove();
                }
                toPrint.add(key + " " + value);
            }
        }
        try {
            Files.write(file, toPrint, Charset.forName("UTF-8"));
        } catch (Exception e) {
            System.out.println("cannot write");
        }
    }

    public void initCurrentPostFile() { currentPostFile = 0; }

    public void setStemming(boolean stemming) { isStemming = stemming; }
}
