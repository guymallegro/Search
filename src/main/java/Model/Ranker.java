package Model;

import java.util.*;

public class Ranker {
    public final double K = 1.2;
    public final double B = 0.75;
    private double titleRank;
    private double avgDocLength;
    private QueryDocument queryDocument;
    private HashMap<Integer, ArrayList<String>> documentsDictionary;
    private double docsAmount = 0;


    public Ranker(HashMap<Integer, ArrayList<String>> documentsDictionary) {
        this.documentsDictionary = documentsDictionary;
        titleRank = 1;
        corpusAvgDocLength();
    }

    /**
     * calculate the average number of terms in document of all the corpus
     */
    private void corpusAvgDocLength() {
        int totalLength = 0;
        for (ArrayList details : documentsDictionary.values()) {
            docsAmount++;
            totalLength += Integer.parseInt("" + details.get(3));
        }
        avgDocLength = totalLength / docsAmount;
    }

    /**
     * get the amount of all the terms that appears in the given query
     *
     * @param documentIndex - the index of the document as it appear in the documentsDictionary file
     */
    private int getDocumentLength(int documentIndex) {
        String length = documentsDictionary.get(documentIndex).get(2);
        return Integer.parseInt(length);
    }

    /**
     * calculate the rank of all documents by M25 formula
     */
    public void rank() {
        double currentRank = 0;
        double firstCalculation = 0;
        double logCalculation = 0;
        String title;
        for (Integer documentIndex : queryDocument.getTermsDocuments()) {
            title = documentsDictionary.get(documentIndex).get(5);
            initialTitleRank();
            for (Term queryTerm : queryDocument.getTextTerms().values()) {
                if (queryTerm.getUnsortedInDocuments().containsKey(documentIndex)) {
                    if (title.contains(queryTerm.getValue()))
                        titleRank = 1.2;
                    int len = getDocumentLength(documentIndex);
                    firstCalculation = (K + 1) / (1 + K * ((1 - B) + (B * len) / avgDocLength));
                    logCalculation = Math.log((1 + documentsDictionary.size()) / queryTerm.getInDocuments().length);
                    currentRank += firstCalculation * logCalculation;
                }
            }
            currentRank *= titleRank;
            Document currentDocument = new Document();
            currentDocument.setId(documentsDictionary.get(documentIndex).get(1));
            currentDocument.setCity(documentsDictionary.get(documentIndex).get(4));
            currentDocument.setTitle(documentsDictionary.get(documentIndex).get(5));
            ArrayList <String> details = documentsDictionary.get(documentIndex);
            for (int i = 6; i < details.size(); i++) {
                currentDocument.addEntity(details.get(i));
            }
            currentDocument.setRank(currentRank);
            queryDocument.getQueryDocuments().add(currentDocument);
            currentRank = 0;
        }
    }

    private void initialTitleRank (){
        titleRank = 1;
    }

    /**
     * set the current document query to rank
     *
     * @param queryDocument the current query
     */
    public void setQueryDocument(QueryDocument queryDocument) {
        this.queryDocument = queryDocument;
    }
}
