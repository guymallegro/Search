package Model;
import java.util.HashMap;

/**
 * The document class which holds all of its required information
 */
public class Document extends ADocument {
    private int max_tf;
    private HashMap<String, String> cities;
    private double rank;
    private String date;
    private String title;
    private HashMap <String, Term> bigLetterTerms;
    private HashMap<Term, Integer> titleTerms;
    private HashMap<Term, Integer> dateTerms;

    /**
     * The default document constructor
     */
    public Document() {
        rank = 0;
        titleTerms = new HashMap<>();
        dateTerms = new HashMap<>();
        cities = new HashMap<>();
        max_tf = 0;
        indexId = documentsAmount++;
        content = "";
        bigLetterTerms = new HashMap<>();
    }

    /**
     * Adds a term which was found on this document text
     *
     * @param term - The found term
     */
    void addTermToText(Term term) {
        if (!textTerms.containsKey(term))
            textTerms.put(term.getValue(), term);
        else {
            term.increaseAmount();
            textTerms.put(term.getValue(), term);
        }
        addBigLetter(term);
        if (textTerms.get(term.getValue()).getAmount() > max_tf)
            max_tf = textTerms.get(term.getValue()).getAmount();
    }

    /**
     * remove term from the terms' hash map of this document
     * @param term the term to remove
     */
    public void removeTermFromText(Term term) {
        if (textTerms.containsKey(term.getValue().toUpperCase()))
            textTerms.remove(term.getValue().toUpperCase());
        removeEntity(term);
    }

    /**
     * Initialize the documents amount to 1
     */
    void initialize() {
        documentsAmount = 1;
        indexId = documentsAmount;
    }

    /**
     * add term to the hash map of entities
     * @param term - the entity to add
     */
    void addBigLetter (Term term){
        if (Character.isUpperCase(term.getValue().charAt(0))){
            term.setRank(calculateDominantEntity(term));
            bigLetterTerms.put(term.getValue(), term);
        }
    }

    /**
     * remove the entity from the hash map
     * @param term the entity
     */
    void removeEntity (Term term) {
        if (bigLetterTerms.containsKey(term.getValue().toUpperCase()))
            bigLetterTerms.remove(term.getValue().toUpperCase());
    }

    /**
     *
     * @param term the entity
     * @return the domination rank of the entity
     */
    private double calculateDominantEntity (Term term){
        double position = (1 / (int)term.getPositionInDocument().get(indexId) - 96);
        return (-1) * term.getAmount() * position;
    }

    /**
     * getter
     * @return the hash map of the dominant entities
     */
    public HashMap<String, Term> getEntities() {return bigLetterTerms;}

    /**
     * Returns the document's max term frequency
     *
     * @return - The max term frequency
     */
    int getMax_tf() {
        return max_tf;
    }

    /**
     *
     * @return the rank of the document
     */
    public double getRank () {return rank;}

    /**
     *
     * @param rank the rank of the document after calculation in ranker
     */
    public void setRank (double rank){this.rank = rank;}

    /**
     * Sets the document's date to the given date
     *
     * @param date - The given date
     */
    void setDate(String date) {
        this.date = date;
    }

    /**
     * Sets the document's title to the given title
     *
     * @param title - The given title
     */
    void setTitle(String title) {
        this.title = title;
    }

    public void setMax_tf(int max_tf) { this.max_tf = max_tf; }


}
