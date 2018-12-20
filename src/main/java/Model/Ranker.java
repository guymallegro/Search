package Model;

import java.util.*;

public class Ranker {
    public final double K = 1.2;
    public final double B = 0.75;
    private double avgDocLength;
    private QueryDocument queryDocument;
    private HashMap<Integer, Document> documentsDictionary;
    private double docsAmount = 0;


    public Ranker(HashMap<Integer, Document> documentsDictionary) {
        this.documentsDictionary = documentsDictionary;
    }

    /**
     *
     * calculate the average number of terms in document of all the corpus
     */
    private void corpusAvgDocLength() {
        int totalLength = 0;
        for (Document document: documentsDictionary.values()){
            docsAmount++;
            totalLength += document.getLength();
        }
        avgDocLength = totalLength / docsAmount;
    }

    /**
     * get the amount of all the terms that appears in the given query
     * @param documentIndex - the index of the document as it appear in the documentsDictionary file
     */
    private int getDocumentLength(int documentIndex) {
        return documentsDictionary.get(documentIndex).getLength();
    }

    /**
     * calculate the rank of all documents by M25 formula
     */
    public void rank() {
        corpusAvgDocLength();
        double currentRank = 0;
        double firstCalculation = 0;
        double logCalculation = 0;
        for (Integer documentIndex : queryDocument.getTermsDocuments().keySet()) {
            for (Term queryTerm : queryDocument.getTextTerms().values()) {
                if (queryTerm.getUnsortedInDocuments().containsKey(documentIndex)){
                    int len = getDocumentLength(documentIndex);
                    firstCalculation = (K + 1) / (1 + K*((1 - B)+ (B*len)/avgDocLength));
                    logCalculation = Math.log((1 + documentsDictionary.size()) / queryTerm.getInDocuments().length);
                    currentRank += firstCalculation * logCalculation;
                }
            }
            Document currentDocument = new Document();
            currentDocument.setId(documentsDictionary.get(documentIndex).getId());
            currentDocument.setRank(currentRank);
            queryDocument.getQueryDocuments().add(currentDocument);
            currentRank = 0;
        }
    }

    /**
     * set the current document query to rank
     * @param queryDocument the current query
     */
    public void setQueryDocument(QueryDocument queryDocument) { this.queryDocument = queryDocument; }
}
