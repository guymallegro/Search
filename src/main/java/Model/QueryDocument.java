package Model;

import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * The query document file
 */
public class QueryDocument extends ADocument {
    private HashSet<Integer> termsDocuments;
    private PriorityQueue<Document> rankDocuments;

    /**
     * The default query document constructor
     */
    QueryDocument() {
        termsDocuments = new HashSet<>();
        rankDocuments = new PriorityQueue<Document>((Comparator.comparingDouble(o -> o.getRank())));
    }

    /**
     * Query document constructor which created a query from a string
     *
     * @param content - The query's content
     */
    QueryDocument(String content) {
        this.content = content;
        termsDocuments = new HashSet<>();
        rankDocuments = new PriorityQueue<Document>(Comparator.comparingDouble(o -> o.getRank()));
    }

    /**
     * Adds a term to the query
     *
     * @param term - The term to be added
     */
    void addTermToText(Term term) {
        if (!textTerms.containsKey(term)) {
            textTerms.put(term.getValue(), term);
        } else {
            term.setAmount(term.getAmount() + 1);
            textTerms.put(term.getValue(), term);
        }
    }

    /**
     * Removes a term from the query
     *
     * @param term - The term to be removed
     */
    public void removeTermFromText(Term term) {
        if (textTerms.containsKey(term))
            textTerms.remove(term);
    }

    /**
     * Adds a document to the query
     *
     * @param index
     */
    void addDocument(int index) {
        termsDocuments.add(index);
    }

    /**
     * Returns the terms document of the query
     *
     * @return
     */
    HashSet<Integer> getTermsDocuments() {
        return termsDocuments;
    }

    /**
     * Returns the found documents ranked
     *
     * @return
     */
    PriorityQueue<Document> getRankedQueryDocuments() {
        return rankDocuments;
    }
}
