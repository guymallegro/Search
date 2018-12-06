package Model;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class Indexer {
    private Model model;
    private HashMap<String, Term> allTerms;
    private ArrayList<Document> documents;
    private int currentPostFile;
    private BufferedWriter bw;
    private PrintWriter out;
    private char currentPartOfPostFile = '~';
    private FileWriter fw;
    private String postingPath;
    private boolean isStemming;
    private HashMap<String, String> capitalLetters;

    Indexer(Model model, HashMap<String, Term> allTerms, ArrayList<Document> documents) {
        currentPostFile = 0;
        this.model = model;
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
        int length = sortedTerms.length;
        for (int t = 0; t < length; t++) {
            line.append("<").append(sortedTerms[t]).append("^");
            Object[] documentsOfTerm = allTerms.get(sortedTerms[t]).getInDocuments();
            int size = documentsOfTerm.length;
            addTermToDictionary((String) sortedTerms[t], currentPartOfPostFile);
            line.append((int) documentsOfTerm[0]).append(";");
            line.append((int) documentsOfTerm[0]).append(",");
            for (int i = 1; i < size; i++) {
                line.append((int) documentsOfTerm[i] - (int) documentsOfTerm[i - 1]).append(",");
            }
            line.deleteCharAt(line.toString().length() - 1);
            line.append(";").append((int) documentsOfTerm[size - 1]);
            line.append("!");
            line.append(allTerms.get(sortedTerms[t]).getAmountInDocuments());
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
                        toWrite = calculateGaps(toWrite.toString(), currentLine[i]);
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
                if (Character.isUpperCase(current.charAt(0))) {
                    if (model.getTermsDictionary().containsKey(current.toLowerCase())) {
                        capitalLetters.put(current.toLowerCase(), toWrite.toString().toLowerCase());
                        int toAdd = (int) model.getTermsDictionary().get(current).get(0);
                        int amount = (int) model.getTermsDictionary().get(current.toLowerCase()).get(0);
                        model.getTermsDictionary().get(current.toLowerCase()).set(0, amount + toAdd);
                        model.getTermsDictionary().remove(current);
                        toWrite.setLength(0);
                    }
                } else if (Character.isLowerCase(current.charAt(0))) {
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


    /*
    This function use the relevant information to
     */
    private StringBuilder lastLineVersion(String line) {
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
        ans.append(" (");
        ans.append(line.substring(line.indexOf("!") + 1)).append(")");
        return ans;
    }

    /*
    This function merge for each term the documents it appeared in, and the frequency in each document.
     */
    private StringBuilder calculateGaps(String toWrite, String next) {
        String[] term = toWrite.split(";");
        term[2] = term[2].substring(0, term[2].indexOf('!'));
        StringBuilder ans = new StringBuilder(term[0]);
        ans.append(";");
        if (!term[1].equals(""))
            ans.append(term[1]).append(",");
        String firstDoc = next.substring(next.indexOf("^") + 1, next.indexOf(";"));
        ans.append(Integer.parseInt(firstDoc) - Integer.valueOf(term[2]));
        int comma = next.indexOf(",", next.indexOf(";"));
        if (comma != -1) {
            int index = next.indexOf("!");
            String last = next.substring(comma, index);
            ans.append(last);
        } else {
            int index = next.indexOf("!");
            String last = next.substring(next.lastIndexOf(";"), index);
            ans.append(last);
        }
        ans.append(toWrite.substring(toWrite.indexOf("!")));
        ans.append(",").append(next.substring(next.indexOf("!") + 1));
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

    /*
    This function sort the terms dictionary and write to file the term, the documents it appeared in,
    and the frequency in each document.
     */

    private void addTermToDictionary(String term, char path) {
        if (model.getTermsDictionary().containsKey(term)) {
            int numOfDocuments = (int) model.getTermsDictionary().get(term).get(0);
            model.getTermsDictionary().get(term).set(0, numOfDocuments + allTerms.get(term).getInDocuments().length);
        } else {
            ArrayList<Object> attributes = new ArrayList<>();
            attributes.add(allTerms.get(term).getAmount());
            attributes.add(allTerms.get(term).getInDocuments().length);
            attributes.add(path);
            model.getTermsDictionary().put(term, attributes);
        }
    }

    /*
    This function sort the documents dictionary and write to file the index of the document,
    the frequency of the most popular term and the origin city of the document.
     */

    void addAllDocuments() {
        int size = documents.size();
        for (int i = 0; i < size; i++) {
            ArrayList<Object> attributes = new ArrayList<>();
            attributes.add(0, documents.get(i).getMax_tf());
            attributes.add(1, documents.get(i).getTextTerms().size());
            attributes.add(2, documents.get(i).getLength());
            if (documents.get(i).getCity() != null)
                attributes.add(3, documents.get(i).getCity());
            else
                attributes.add(3, "");
            model.getDocsDictionary().put(documents.get(i).getIndexId(), attributes);
        }
        documents.clear();
    }

    /*
    This function sort the cities dictionary and write to file the city name, country and population.
     */

    public void addAllCities(String path) {
        postingPath = path;
        if (isStemming)
            path += "/postCitiesWithStemming.txt";
        else
            path += "/postCities.txt";
        Path file = Paths.get(path);
        ArrayList<String> toPrint = new ArrayList<>();
        Object[] sortedCities = model.getCitiesDictionary().keySet().toArray();
        Arrays.sort(sortedCities);
        int size = sortedCities.length;
        for (int i = 0; i < size; i++) {
            String key = (String) sortedCities[i];
            String value = "";
            if (model.getCitiesDictionary().get(key).getCurrency() != null && model.getCitiesDictionary().get(key).getLocationsInDocuments().size() == 0) {
                model.getCitiesDictionary().remove(key);
                continue;
            } else {
                Iterator it = model.getCitiesDictionary().get(key).getLocationsInDocuments().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    value += pair.getKey() + "(" + pair.getValue() + ")";
                    it.remove();
                }
                toPrint.add("<" + key + ": " + value);
            }
        }
        try {
            Files.write(file, toPrint, Charset.forName("UTF-8"));
        } catch (Exception e) {
            System.out.println("cannot write");
        }
    }

    public void initCurrentPostFile() {
        currentPostFile = 0;
    }

    public void setStemming(boolean stemming) {
        isStemming = stemming;
    }
}