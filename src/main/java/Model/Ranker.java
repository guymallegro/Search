package Model;

import java.util.*;

/**
 * The ranker class
 */
class Ranker {
    private final double K = 1.05;
    private final double B = 0.5;
    private double titleRank;
    private double positionRank;
    private double avgDocLength;
    private QueryDocument queryDocument;
    private HashMap<Integer, ArrayList<String>> documentsDictionary;
    private double docsAmount = 0;


    /**
     * The ranker's constructor
     *
     * @param documentsDictionary - The documents dictionary
     */
    Ranker(HashMap<Integer, ArrayList<String>> documentsDictionary) {
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
        return Integer.parseInt(documentsDictionary.get(documentIndex).get(3));
    }

    /**
     * calculate the rank of all documents by M25 formula
     */
    public void rank() {
        System.out.println("start ranking");
        double currentRank = 0;
        double firstCalculation;
        double logCalculation;
        String title;
        int N = documentsDictionary.size();
        for (Integer documentIndex : queryDocument.getTermsDocuments()) {
            title = "";
            if (documentsDictionary.get(documentIndex).size() > 5) {
                title = documentsDictionary.get(documentIndex).get(5);
            }
            titleRank = 1;
            for (Term queryTerm : queryDocument.getTextTerms().values()) {
                if (queryTerm.getUnsortedInDocuments().containsKey(documentIndex)) {
                    if (title.contains(queryTerm.getValue()))
                        titleRank = 1.1;
                    positionRank = (0.01 - (0.001 * ((int) queryTerm.getPositionInDocument().get(documentIndex) - 96)));
                    int len = getDocumentLength(documentIndex);
                    int tf = queryTerm.getUnsortedInDocuments().get(documentIndex);
                    int nqi = queryTerm.getUnsortedInDocuments().size() - 1;
                    double top = (K + 1) * tf;
                    double bottom = (tf + (K * ((1 - B) + B * (len / avgDocLength))));
                    firstCalculation = top / bottom;
                    logCalculation = Math.log((N - nqi + 0.5) / (nqi + 0.5));
                    if (queryTerm.isSemantic())
                        currentRank += (queryTerm.getRank() * firstCalculation * logCalculation);
                    else
                        currentRank += (firstCalculation * logCalculation + positionRank);
                        //currentRank += (firstCalculation * logCalculation * titleRank + positionRank);
                }
            }
            currentRank *= titleRank;
            Document currentDocument = new Document();
            currentDocument.setId(documentsDictionary.get(documentIndex).get(1));
            if (documentsDictionary.get(documentIndex).size() > 4) {
                currentDocument.setCity(documentsDictionary.get(documentIndex).get(4));
            }
            if (documentsDictionary.get(documentIndex).size() > 6) {
                ArrayList<String> details = documentsDictionary.get(documentIndex);

                for (int i = 6; i < details.size(); i++) {
                    currentDocument.addEntity(details.get(i));
                }
            }
            currentDocument.setRank(currentRank);
            queryDocument.addRankedDocument(currentDocument);
            currentRank = 0;
        }
        System.out.println("finish ranking");
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