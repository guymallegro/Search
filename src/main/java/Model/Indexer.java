package Model;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * The indexer class, responsible of all the indexing of the search engine
 */
class Indexer {
    private HashMap<String, Term> allTerms;
    private ArrayList<Document> documents;
    private int currentAmountTempPostFiles;
    private BufferedWriter bw;
    private PrintWriter out;
    private String currentPartOfPostFile = "~";
    private FileWriter fw;
    private String postingPath;
    private boolean isStemming;
    private HashMap<String, String> capitalLetters;
    private HashMap<String, Term> termsDictionary;
    private HashMap<Integer, ArrayList<String>> documentsDictionary;

    /**
    /**
     * The constructor of the indexer
     *
     * @param model     - The model
     * @param allTerms  - All the terms which were found
     * @param documents - All the documents which were found
     */
    Indexer(Model model, HashMap<String, Term> allTerms,HashMap<String, Term> termsDictionary, ArrayList<Document> documents, HashMap<Integer, ArrayList<String>> documentsDictionary) {
        currentAmountTempPostFiles = 0;
        this.allTerms = allTerms;
        this.documents = documents;
        capitalLetters = new HashMap<>();
        this.termsDictionary = termsDictionary;
        this.documentsDictionary = documentsDictionary;
    }

    /**
     * Creates temporary posting files for all the found terms, each post file is created sorted.
     *
     * @param path - Path to the required position for the post files to be created
     */
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
            addTermToDictionary((String) sortedTerm);
            line.append((int) documentsOfTerm[0]).append(";");
            line.append((int) documentsOfTerm[0]).append(",");
            for (int i = 1; i < size; i++) {
                line.append((int) documentsOfTerm[i] - (int) documentsOfTerm[i - 1]).append(",");
            }
            line.deleteCharAt(line.toString().length() - 1);
            line.append(";").append((int) documentsOfTerm[size - 1]);
            line.append("!");
            line.append(allTerms.get(sortedTerm).getAmountInDocuments());
            lines.add(line.toString());
            line.setLength(0);
        }
        path += "/post" + currentAmountTempPostFiles + ".txt";
        Path file = Paths.get(path);
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (Exception e) {
            System.out.println("cannot write");
        }
        currentAmountTempPostFiles++;
    }

    /**
     * Merges all the temporary posting files into one sorted posting file
     */
    void mergeAllPostFiles() {
        Scanner[] scanners = new Scanner[currentAmountTempPostFiles];
        String[] currentLine = new String[currentAmountTempPostFiles];
        fw = null;
        StringBuilder toWrite;
        int currentIndex = 0;
        boolean isChanged = true;
        String lastPosting = "/termsPosting.txt";
        if (isStemming)
            lastPosting = "/termsPostingWithStemming.txt";
        try {
            fw = new FileWriter(postingPath + lastPosting);
        } catch (Exception e) {
            System.out.println("Failed to create file writer");
        }
        bw = new BufferedWriter(fw);
        out = new PrintWriter(bw);
        for (int i = 0; i < currentAmountTempPostFiles; i++) {
            try {
                scanners[i] = new Scanner(new File(postingPath + "/post" + i + ".txt"));
            } catch (Exception e) {
                System.out.println("Failed to create a scanner");
            }
            try {
                currentLine[i] = scanners[i].nextLine();
            } catch (Exception e) {
                currentAmountTempPostFiles--;
            }
        }
        while (true) {
            toWrite = new StringBuilder("~");
            String fromCompare = "";
            for (int i = 0; i < currentAmountTempPostFiles; i++) {
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
                if (!Character.isDigit(toWrite.charAt(1)) && toWrite.charAt(1) != currentPartOfPostFile.charAt(0)) {
                    changePostFile("" + toWrite.toString().charAt(1));
                }
                String current = toWrite.toString().substring(1, toWrite.toString().indexOf('^'));
                if (Character.isUpperCase(current.charAt(0))) {
                    if (termsDictionary.containsKey(current.toLowerCase())) {
                        capitalLetters.put(current.toLowerCase(), toWrite.toString().toLowerCase());
                        int toAdd = termsDictionary.get(current).getAmount();
                        int amount = termsDictionary.get(current.toLowerCase()).getAmount();
                        termsDictionary.get(current.toLowerCase()).setAmount(amount + toAdd);
                        termsDictionary.remove(current.toUpperCase());
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
        removeTempPostFiles();
        out.close();
    }

    private void removeTempPostFiles() {
        for (int i = 0; i < currentAmountTempPostFiles; i++) {
            File currentFile = new File(postingPath + "\\" + "post" + i + ".txt");
            currentFile.delete();
        }
    }

    /**
     * Changes the format used at the temp post files to the format used at the final post files.
     *
     * @param line - The line before the new format
     * @return - The line after the new format
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

    /**
     * Accepts two strings, calculates the gaps and merges them into one string.
     *
     * @param toWrite - The first string
     * @param next    - The second string
     * @return - The two strings merged
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

    private void changePostFile(String nextFile) {
        out.close();
        if (nextFile.equals("."))
            nextFile = "numbers";
        currentPartOfPostFile = nextFile;
        try {
            if (Character.isLowerCase(nextFile.charAt(0))) {
                File file = new File(postingPath + "/post" + currentPartOfPostFile + ".txt");
                FileWriter fr = new FileWriter(file, true);
                bw = new BufferedWriter(fr);
            } else {
                fw = new FileWriter(postingPath + "/post" + currentPartOfPostFile + ".txt");
                bw = new BufferedWriter(fw);
            }
        } catch (Exception e) {
            System.out.println("Failed to create file writer");
        }
        out = new PrintWriter(bw);
    }

    /**
     * Adds the term to the hash map dictionary, the key is the terms name, and the value is the total amount the term was found.
     * If the term is already in the dictionary then update it's total amount.
     *
     * @param term - The term to add to the dictionary
     */
    private void addTermToDictionary(String term) {
        if (termsDictionary.containsKey(term)) {
            int amount = termsDictionary.get(term).getAmount();
            termsDictionary.get(term).setAmount(amount + allTerms.get(term).getAmount());
        } else {
            Term newTerm = new Term(term);
            newTerm.setAmount(allTerms.get(term).getAmount());
            termsDictionary.put(term, newTerm);
        }
    }

    /**
     * Adds all the found documents to the hash map dictionary.
     * The key is the documents index id and the values are it's max term frequency, id,amount of unique terms,it's content length.
     */
    void addAllDocumentsToDictionary() {
        int size = documents.size();
        Document document;
        for (int i = 0; i < size; i++) {
            document = documents.get(i);
            ArrayList<String> attributes = new ArrayList<>();
            attributes.add(0, Integer.toString(document.getMax_tf()));
            attributes.add(1, document.getId());
            attributes.add(2, Integer.toString(document.getTextTerms().size()));
            attributes.add(3, Integer.toString(document.getLength()));
            if (document.getCity().length() > 0)
                attributes.add(4, document.getCity());
            else
                attributes.add(4, "");
            HashMap <String, Term> temp = document.getEntities();
            List<Term> mapValues = new ArrayList<>(document.getEntities().values());
            Collections.sort(mapValues, (Comparator.comparingDouble((o) -> o.getRank())));
            int num = 5;
            for (int entity = mapValues.size() - 1; num < 10 && entity >= 0; entity--){
                if (termsDictionary.containsKey(mapValues.get(entity).getValue().toUpperCase())){
                    attributes.add(num, mapValues.get(entity).getValue());
                    num++;
                }
            }
            documentsDictionary.put(documents.get(i).getIndexId(), attributes);
        }
        documents.clear();
    }

    /**
     * Initializes the amount of the temporary post files to 0
     */
    void initCurrentPostFile() {
        currentAmountTempPostFiles = 0;
    }

    /**
     * Tells the indexer if stemming is being done
     *
     * @param stemming - If stemming is being done
     */
    void setStemming(boolean stemming) {
        isStemming = stemming;
    }
}