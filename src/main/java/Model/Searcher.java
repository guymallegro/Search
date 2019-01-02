package Model;

import java.util.*;

/**
 * The searcher class
 */
public class Searcher {
    private Model model;
    private Ranker ranker;
    private SemanticChecker semanticChecker;
    private boolean isSemantic;
    private QueryDocument currentQuery;
    private HashMap<String, City> cityDictionary;
    private HashMap<Integer, ArrayList<String>> documentsDictionary;

    /**
     * The searcher constructor
     *
     * @param model               - The model
     * @param documentsDictionary - The documents dictionary
     * @param cityDictionary      - The cities dictionary
     */
    Searcher(Model model, HashMap<Integer, ArrayList<String>> documentsDictionary, HashMap<String, City> cityDictionary) {
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
     * find the relevant documents using the ranker
     */
    public void findRelevantDocs(QueryDocument queryDocument) {
        currentQuery = queryDocument;
        ranker.setQueryDocument(currentQuery);
        if (isSemantic)
            addSemantic();
        retrieveData(findLinesOfTerms());
        ranker.rank();
    }

    /**
     * Adds the semantic terms to the query
     */
    private void addSemantic() {
        ArrayList<String> semantic = semanticChecker.getSemantic();
        for (int i = 0; i < semantic.size(); i++) {
            double rank = model.getTermsDictionary().get(semantic.get(i)).getRank();
            model.getTermsDictionary().get(semantic.get(i)).setRank(rank * 0.8);
            currentQuery.addTermToText(model.getTermsDictionary().get(semantic.get(i)));
        }
    }

    /**
     * Tells the model to find the posting lines of the given terms
     *
     * @return - The posting lines of the terms
     */
    private ArrayList<String> findLinesOfTerms() {
        System.out.println("start to find lines of terms - searcher");
        HashMap<String, Term> terms = currentQuery.getTextTerms();
        ArrayList<String> termsToFind = new ArrayList<>();
        ArrayList<String> allLines = new ArrayList<>();
        Map<Integer, Integer> map = new TreeMap(terms);
        Set set2 = map.entrySet();
        Iterator iterator2 = set2.iterator();
        while (iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry) iterator2.next();
            termsToFind.add((String) me2.getKey());
        }
        allLines.addAll(model.findTermFromPosting(termsToFind));
        return allLines;
    }

    /**
     * update the relevant details of terms and documents
     *
     * @param allLines - ArrayList of the lines of each term from the posting file
     */
    private void retrieveData(ArrayList<String> allLines) {
        String[] documents;
        String[] positionAndAmount;
        String termLine = "";
        String term = "";
        int documentIndex = 0;
        for (int line = 0; line < allLines.size(); line++) {
            termLine = allLines.get(line);
            term = termLine.substring(1, termLine.indexOf(";"));
            documents = termLine.substring(termLine.indexOf(";") + 1, termLine.indexOf(" ")).split(",");
            positionAndAmount = termLine.substring(termLine.indexOf("(") + 1, termLine.indexOf(")")).split(",");
            for (int i = 0; i < documents.length; i++) {
                documentIndex += Integer.parseInt(documents[i]);
                currentQuery.getTextTerms().get(term).setInDocument(documentIndex, Integer.parseInt(positionAndAmount[i].substring(1)), positionAndAmount[i].charAt(0));
                if (documentsDictionary.containsKey(documentIndex))
                    currentQuery.addDocument(documentIndex);
            }
        }
    }

    /**
     * Tells to use semantic when processing queries
     *
     * @param selected
     */
    void setSemantic(boolean selected) {
        isSemantic = selected;
    }
}
