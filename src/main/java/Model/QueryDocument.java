package Model;
import java.util.*;

/**
 * The QueryDocument class which holds all of its required information for query
 */
public class QueryDocument extends ADocument {
    private Model model;
    private HashSet<Integer> termsDocuments;
    private String narrative;
    private PriorityQueue<Document> rankDocuments;

    /**
     * constructor
     */
    public QueryDocument(Model model) {
        this.model = model;
        termsDocuments = new HashSet<>();
        narrative = "";
        rankDocuments = new PriorityQueue<Document>((Comparator.comparingDouble(o -> o.getRank())));
    }

    /**
     * constructor
     * @param content - the content of the query
     */
    public QueryDocument(Model model, String content) {
        this.model = model;
        this.content = content;
        termsDocuments = new HashSet<>();
        rankDocuments = new PriorityQueue<Document>(Comparator.comparingDouble(o -> o.getRank()));
    }

    /**
     * add term of query to db
     * @param term - The term to be added
     */
    void addTermToText(Term term) {
        if (model.getStopWords().contains(term.getValue().toLowerCase()) ||
                model.getStopWords().contains(term.getValue().toUpperCase()))
            return;
        if (!textTerms.containsKey(term)) {
            if (model.getTermsDictionary().containsKey(term.getValue().toUpperCase()))
                textTerms.put(term.getValue().toUpperCase(), term);
            else if (model.getTermsDictionary().containsKey(term.getValue().toLowerCase()))
                textTerms.put(term.getValue().toLowerCase(), term);
        } else {
            term.setAmount(term.getAmount() + 1);
        }
    }

    /**
     * remove term from the terms db
     * @param term - The term to be removed
     */
    public void removeTermFromText(Term term) {
        if (textTerms.containsKey(term))
            textTerms.remove(term);
    }

    /**
     *  add to the db index of document of term from the query
     * @param index - the index of the document
     */
    public void addDocument(int index) {
        termsDocuments.add(index);
    }

    /**
     * this function add ranked documents to the db
     * @param newDocument - the ranked document
     */
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

    /**
     * this function return HashSet of the terms of the documents
     * @return HashSet of the terms of the documents
     */
    public HashSet<Integer> getTermsDocuments() {
        return termsDocuments;
    }

    /**
     * this function return ArrayList of the ranked documents
     * @return ArrayList of the ranked documents
     */
    public ArrayList<Document> getRankedQueryDocuments() {
        ArrayList<Document> rankedDocuments = new ArrayList<Document>();
        while (!rankDocuments.isEmpty())
            rankedDocuments.add(0, rankDocuments.poll());
        return rankedDocuments;
    }

    public String getNarrative () {return narrative;}

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }
}