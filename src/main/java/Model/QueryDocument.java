package Model;


import java.util.*;

public class QueryDocument extends ADocument {
    private HashSet<Integer> termsDocuments;
    private PriorityQueue<Document> rankDocuments;

    public QueryDocument() {
        termsDocuments = new HashSet<>();
        rankDocuments = new PriorityQueue<Document>((Comparator.comparingDouble(o -> o.getRank())));
    }

    public QueryDocument(String content) {
        this.content = content;
        termsDocuments = new HashSet<>();
        rankDocuments = new PriorityQueue<Document>(Comparator.comparingDouble(o -> o.getRank()));
    }

    void addTermToText(Term term) {
        if (!textTerms.containsKey(term)) {
            textTerms.put(term.getValue(), term);
        } else {
            term.setAmount(term.getAmount() + 1);
            textTerms.put(term.getValue(), term);
        }
    }

    public void removeTermFromText(Term term) {
        if (textTerms.containsKey(term))
            textTerms.remove(term);
    }

    public void addDocument(int index) {
        termsDocuments.add(index);
    }

    public void addRankedDocument(Document newDocument){
        if (rankDocuments.size() < 50)
            rankDocuments.add(newDocument);
        else {
            Document minimum = rankDocuments.poll();
            if (minimum.getRank() > newDocument.getRank())
                rankDocuments.add(minimum);
            else
                rankDocuments.add(newDocument);
        }
    }

    public HashSet<Integer> getTermsDocuments() {
        return termsDocuments;
    }

    public ArrayList<Document> getRankedQueryDocuments() {
        ArrayList<Document> rankedDocuments = new ArrayList<Document>();
        while (!rankDocuments.isEmpty())
            rankedDocuments.add(0, rankDocuments.poll());
        return rankedDocuments;
    }
}