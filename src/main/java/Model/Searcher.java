package Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Searcher {
    private Model model;
    private Ranker ranker;
    private SemanticChecker semanticChecker;
    private boolean isSemantic;
    private QueryDocument currentQuery;
    private HashMap<String, City> cityDictionary;
    private HashMap<Integer, ArrayList<String>> documentsDictionary;

    public Searcher(Model model, HashMap<Integer, ArrayList<String>> documentsDictionary, HashMap<String, City> cityDictionary) {
        this.model = model;
        this.cityDictionary = cityDictionary;
        this.documentsDictionary = documentsDictionary;
        ranker = new Ranker(documentsDictionary);
        if (isSemantic) {
            List<String> terms = new ArrayList<>(currentQuery.getTextTerms().keySet());
            semanticChecker = new SemanticChecker(model, terms);
        }
    }

    /**
     * find the 50 most relevant documents using the ranker
     */
    public void findRelevantDocs(QueryDocument queryDocument) {
        currentQuery = queryDocument;
        ranker.setQueryDocument(currentQuery);
        if (isSemantic) {

        }
        if (isSemantic)
            addSemantic();
        retrieveData(findLinesOfTerms());
        ranker.rank();
    }

    private void addSemantic() {
        ArrayList<String> semantic = semanticChecker.getSemantic();
        for (int i = 0; i < semantic.size(); i++) {
            double rank = model.getTermsDictionary().get(semantic.get(i)).getRank();
            model.getTermsDictionary().get(semantic.get(i)).setRank(rank * 0.8);
            currentQuery.addTermToText(model.getTermsDictionary().get(semantic.get(i)));
        }
    }

    /**
     * find the posting lines of terms of the current query
     *
     * @return ArrayList of the lines of each term in the query from the posting file
     */
    private ArrayList<String> findLinesOfTerms() {
        HashMap<String, Term> terms = currentQuery.getTextTerms();
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
                currentQuery.getTextTerms().get(term).addInDocument(documentIndex, 1);
                if (documentsDictionary.containsKey(documentIndex))
                    currentQuery.addDocument(documentIndex);
            }
        }
    }

    public void setSemantic(boolean selected) {
        isSemantic = selected;
    }
}
