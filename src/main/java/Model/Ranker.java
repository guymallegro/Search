package Model;

import java.util.*;

public class Ranker {
    public final double K = 1.2;
    public final double B = 0.75;
    private double avgDocLength;
    private QueryDocument queryDocument;
    private HashMap<Integer, ArrayList<String>> documentsDictionary;
    private double docsAmount = 0;


    public Ranker(HashMap<Integer, ArrayList<String>> documentsDictionary) {
        this.documentsDictionary = documentsDictionary;
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
        for (Integer documentIndex : queryDocument.getTermsDocuments()) {
            for (Term queryTerm : queryDocument.getTextTerms().values()) {
                if (queryTerm.getUnsortedInDocuments().containsKey(documentIndex)) {
                    int len = getDocumentLength(documentIndex);
                    firstCalculation = (K + 1) / (1 + K * ((1 - B) + (B * len) / avgDocLength));
                    logCalculation = Math.log((1 + documentsDictionary.size()) / queryTerm.getInDocuments().length);
                    currentRank += firstCalculation * logCalculation;
                }
            }
            Document currentDocument = new Document();
            currentDocument.setId(documentsDictionary.get(documentIndex).get(1));
            if (documentsDictionary.get(documentIndex).size() > 5) {
                currentDocument.setCity(documentsDictionary.get(documentIndex).get(4));
                currentDocument.setIndexId(Integer.parseInt(documentsDictionary.get(documentIndex).get(5)));
            } else {
                currentDocument.setIndexId(Integer.parseInt(documentsDictionary.get(documentIndex).get(4)));
            }
            currentDocument.setRank(currentRank);
            queryDocument.getQueryDocuments().add(currentDocument);
            currentRank = 0;
        }
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
