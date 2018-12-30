package Model;

import java.util.*;

public class Ranker {
    public final double K = 1.2;
    public final double B = 0.75;
    private double titleRank;
    private double positionRank;
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
            title = documentsDictionary.get(documentIndex).get(5);
            titleRank = 1;
            for (Term queryTerm : queryDocument.getTextTerms().values()) {
                if (queryTerm.getUnsortedInDocuments().containsKey(documentIndex)) {
                    if (title.contains(queryTerm.getValue()))
                        titleRank = 1.2;
                    positionRank = (1 - (0.1 * ((int) queryTerm.getPositionInDocument().get(documentIndex) - 96)));
                    int len = getDocumentLength(documentIndex);
                    int tf = queryTerm.getUnsortedInDocuments().get(documentIndex);
                    int nqi = queryTerm.getInDocuments().length - 1;
                    double top=(K + 1) * tf;
                    double bottom = (tf + (K * ((1 - B) + B * (len / avgDocLength))));
                    firstCalculation = top / bottom;
                    logCalculation = Math.log((N - nqi + 0.5) / (nqi + 0.5));
                    currentRank += (firstCalculation * logCalculation);
                    currentRank *= titleRank;
                     currentRank *= positionRank;
                }
            }
            Document currentDocument = new Document();
            currentDocument.setId(documentsDictionary.get(documentIndex).get(1));
            currentDocument.setCity(documentsDictionary.get(documentIndex).get(4));
            currentDocument.setTitle(documentsDictionary.get(documentIndex).get(5));
            ArrayList<String> details = documentsDictionary.get(documentIndex);
            for (int i = 6; i < details.size(); i++) {
                currentDocument.addEntity(details.get(i));
            }
            currentDocument.setRank((-1) * currentRank);
            queryDocument.getRankedQueryDocuments().add(currentDocument);
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
