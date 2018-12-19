package Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Searcher {
    private Model model;
    private Ranker ranker;

    private HashMap<String, Term> queryTerms;
    private HashMap<String, City> cityDictionary;
    private HashMap<Integer, Document> documentsDictionary;
    private HashMap<Integer, Document> allDocuments;

    public Searcher(HashMap<String, ArrayList<Object>> termsDictionary, HashMap<Integer, Document> documentsDictionary, HashMap<String, City> cityDictionary, Model model, HashMap<String, Term> queryTerms) {
        this.model = model;
        this.queryTerms = queryTerms;
        allDocuments = new HashMap<>();
        this.cityDictionary = cityDictionary;
        this.documentsDictionary = documentsDictionary;
        ranker = new Ranker(termsDictionary, documentsDictionary, queryTerms, allDocuments);
    }

    /**
     * find the 50 most relevant documents using the ranker
     */
    public void findRelevantDocs() {
        retrieveData(findLinesOfTerms(queryTerms));
        ranker.rank();
    }

    /**
     * @param terms - HashMap of the terms from the query after the parsing process
     * @return ArrayList of the lines of each term from the posting file
     */
    private ArrayList<String> findLinesOfTerms(HashMap<String, Term> terms) {
        String[] sortedTerms = terms.keySet().toArray(new String[terms.size()]);
        Arrays.sort(sortedTerms, String.CASE_INSENSITIVE_ORDER);
        ArrayList<String> termsToFind = new ArrayList<>();
        ArrayList<String> allLines = new ArrayList<>();
        char currentLetter = (terms.get(sortedTerms[0])).getValue().charAt(0);
        for (int i = 0; i < sortedTerms.length; i++) {
            while (currentLetter == Character.toLowerCase((terms.get(sortedTerms[i])).getValue().charAt(0)) ||
                    currentLetter == Character.toUpperCase((terms.get(sortedTerms[i])).getValue().charAt(0))) {
                termsToFind.add((terms.get(sortedTerms[i])).getValue());
                if (i + 1 < sortedTerms.length)
                    i++;
                else
                    break;
            }
            allLines.addAll(model.findTermFromPosting(termsToFind));
            currentLetter = Character.toLowerCase((terms.get(sortedTerms[i])).getValue().charAt(0));
            termsToFind = new ArrayList<>();
            termsToFind.add((terms.get(sortedTerms[i])).getValue());
        }
        return allLines;
    }

    /**
     * update the relevant details of terms and documents
     *
     * @param allLines - ArrayList of the lines of each term from the posting file
     */
    private void retrieveData(ArrayList<String> allLines) {
        String[] documents;
        String termLine = "";
        String term = "";
        int documentIndex = 0;
        for (int line = 0; line < allLines.size(); line++) {
            termLine = allLines.get(line);
            term = termLine.substring(1, termLine.indexOf(";"));
            documents = termLine.substring(termLine.indexOf(";") + 1, termLine.indexOf(" ")).split(",");
            for (int i = 0; i < documents.length; i++) {
                documentIndex += Integer.parseInt(documents[i]);
                queryTerms.get(term).addInDocument(documentIndex, 1);
                if (documentsDictionary.containsKey(documentIndex))
                    allDocuments.put(documentIndex, documentsDictionary.get(documentIndex));
            }
        }
    }

    /**
     * initial the terms' HashMap
     *
     * @param queryTerms - the HashMap of terms after parsing
     */
    public void setQueryTerms(HashMap<String, Term> queryTerms) {
        this.queryTerms = queryTerms;
    }

    public PriorityQueue<Document> getQueryDocuments() {
        return ranker.getQueryDocuments();
    }
}
