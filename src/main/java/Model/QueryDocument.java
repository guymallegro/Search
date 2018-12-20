package Model;


import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class QueryDocument extends ADocument {
    private HashMap<Integer, Document> termsDocuments;
    private PriorityQueue<Document> rankDocuments;

    public QueryDocument(){
        termsDocuments = new HashMap<Integer, Document>();
        rankDocuments = new PriorityQueue<Document>((Comparator.comparingDouble(o -> o.getRank())));
    }

    public QueryDocument(String content){
        this.content = content;
        termsDocuments = new HashMap<Integer, Document>();
        rankDocuments = new PriorityQueue<Document>((Comparator.comparingDouble(o -> o.getRank())));
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

    public void addDocument (int index, Document document) {
        termsDocuments.put(index, document);}

    public HashMap<Integer, Document> getTermsDocuments() { return termsDocuments; }

    public PriorityQueue<Document> getQueryDocuments() { return rankDocuments; }
}
