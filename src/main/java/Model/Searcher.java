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
    private HashSet<String> selectedCities;
    String[] documents;
    String[] positionAndAmount;

    /**
     * The searcher constructor
     *
     * @param model               - The model
     * @param documentsDictionary - The documents dictionary
     * @param cityDictionary      - The cities dictionary
     */
    public Searcher(Model model, HashMap<Integer, ArrayList<String>> documentsDictionary, HashMap<String, City> cityDictionary, HashSet<String> selectedCities) {
        this.model = model;
        this.selectedCities = selectedCities;
        this.cityDictionary = cityDictionary;
        this.documentsDictionary = documentsDictionary;
        ranker = new Ranker(documentsDictionary);

    }

    /**
     * find the relevant documents using the ranker
     */
    public void findRelevantDocs(QueryDocument queryDocument) {
        currentQuery = queryDocument;
        ranker.setQueryDocument(currentQuery);
        if (isSemantic) {
            List<String> terms = new ArrayList<>(currentQuery.getTextTerms().keySet());
            semanticChecker = new SemanticChecker(model, terms);
            addSemantic();
        }
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
            model.getTermsDictionary().get(semantic.get(i)).setSemantic(true);
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
        if (selectedCities.size() > 0){
            Iterator iterator = selectedCities.iterator();
            while (iterator.hasNext()) {
                String me = (String) iterator.next();
                currentQuery.addTermToText(new Term(me));
            }
        }
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
        String termLine = "";
        String term = "";
        int documentIndex = 0;
        for (int line = 0; line < allLines.size(); line++) {
            termLine = allLines.get(line);
            term = termLine.substring(1, termLine.indexOf(";"));
            documents = termLine.substring(termLine.indexOf(";") + 1, termLine.indexOf(" ")).split(",");
            positionAndAmount = termLine.substring(termLine.indexOf("(") + 1, termLine.indexOf(")")).split(",");
            for (int i = 0; i < documents.length; i++) {
                documentIndex = Integer.parseInt(documents[i]);
                if (selectedCities.size() > 0) {
                    if (selectedCities.contains(term.toUpperCase()) || selectedCities.contains(model.getDocsDictionary().get(documentIndex).get(4))) {
                        currentQuery.getTextTerms().get(term).setInDocument(documentIndex, Integer.parseInt(positionAndAmount[i].substring(1)), positionAndAmount[i].charAt(0));
                        currentQuery.addDocument(documentIndex);
                    }
                } else {
                    currentQuery.getTextTerms().get(term).setInDocument(documentIndex, Integer.parseInt(positionAndAmount[i].substring(1)), positionAndAmount[i].charAt(0));
                    currentQuery.addDocument(documentIndex);
                }
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