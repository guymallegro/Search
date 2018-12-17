package Model;

import java.util.*;

public class Ranker {
    public final double K = 1.2;
    public final double B = 0.75;
    private double avgDocLength;
    private HashMap<String, Term> queryTerms;
    HashMap<Integer, ArrayList<Object>> allDocuments;
    private HashMap<String, ArrayList<Object>> termsDictionary;
    private HashMap<Integer, ArrayList<Object>> documentsDictionary;
    private PriorityQueue <Document> queryDocuments;
    private double docsAmount = 0;


    public Ranker(HashMap<String, ArrayList<Object>> termsDictionary, HashMap<Integer, ArrayList<Object>> documentsDictionary, HashMap<String, Term> queryTerms, HashMap<Integer, ArrayList<Object>> allDocuments) {
        this.queryTerms = queryTerms;
        this.allDocuments = allDocuments;
        this.termsDictionary = termsDictionary;
        this.documentsDictionary = documentsDictionary;
    }

    /**
     * calculate the average number of terms in document of all the corpus
     */
    private void corpusAvgDocLength() {
        int totalLength = 0;
        Map<Integer, Integer> map = new TreeMap(documentsDictionary);
        Set set2 = map.entrySet();
        Iterator iterator2 = set2.iterator();
        while (iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry) iterator2.next();
            docsAmount++;
            totalLength += (Integer) ((ArrayList) me2.getValue()).get(3);
        }
        avgDocLength = totalLength / docsAmount;

    }

    /**
     *
     * @param term - the current term from the query
     * @return the amount of document that the term appeared in
     */
    private int getDocumentAmount(String term) {
        return queryTerms.get(term).getInDocuments().length;
    }

    /**
     * get the amount of all the terms that appears in the given query
     * @param documentIndex - the index of the document as it appear in the documentsDictionary file
     */
    private int getDocumentLength( int documentIndex) {
        String length = (String) documentsDictionary.get(documentIndex).get(2);
        return Integer.parseInt(length);
    }

//    /**
//     * initial the terms' HashMap
//     * @param queryTerms - the HashMap of terms after parsing
//     */
//    public void setQueryTerms(HashMap<String, Term> queryTerms) {
//        this.queryTerms = queryTerms;
//    }

    public void rank() {
        double currentRank = 0;
        double firstCalculation = 0;
        double logCalculation = 0;
        for (Integer documentIndex : allDocuments.keySet()) {
            for (Term queryTerm : queryTerms.values()) {
                if (queryTerm.getUnsortedInDocuments().containsKey(documentIndex)){
                    firstCalculation = (K + 1) / (1 + K*(1 - B)+ (B*getDocumentLength(documentIndex))/avgDocLength);
                    logCalculation = Math.log((documentsDictionary.size() + 1) / queryTerm.getInDocuments().length);
                    currentRank += firstCalculation * logCalculation;
                }
            }
            Document currentDocument = new Document();
            currentDocument.setId((String) documentsDictionary.get(documentIndex).get(1));
            currentDocument.setRank(currentRank);
            queryDocuments.add(currentDocument);
            currentRank = 0;
        }

    }
}
