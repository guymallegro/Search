package Model;


import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class QueryDocument extends ADocument {
    private HashSet<Integer> termsDocuments;
    private PriorityQueue<Document> rankDocuments;

    public QueryDocument(){
        termsDocuments = new HashSet<>();
        rankDocuments = new PriorityQueue<Document>((Comparator.comparingDouble(o -> o.getRank())));
    }

    public QueryDocument(String content){
        this.content = content;
        termsDocuments = new HashSet<>();
        rankDocuments = new PriorityQueue<Document>(Comparator.comparingDouble(o -> o.getRank()));
    }

    void addTermToText(Term term) {
        if (!textTerms.containsKey(term)){
            textTerms.put(term.getValue(), term);
        }
        else {
            term.setAmount(term.getAmount() + 1);
            textTerms.put(term.getValue(), term);
        }
    }

    public void removeTermFromText(Term term) {
        if (textTerms.containsKey(term))
            textTerms.remove(term);
    }

    public void addDocument (int index) {
        termsDocuments.add(index);
    }

    public HashSet<Integer> getTermsDocuments() { return termsDocuments; }

    public PriorityQueue<Document> getQueryDocuments() { return rankDocuments; }
}
