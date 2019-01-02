package Model;

import java.util.*;

/**
 * The document class which holds all of its required information
 */
public class Document extends ADocument {
    private int max_tf;
    private HashMap<String, String> cities;
    private double rank;
    private String date;
    private String title;
    private HashSet<Term> bigLetterTerms;
    private ArrayList<String> topFiveEntites;
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
        bigLetterTerms = new HashSet<Term>();
        topFiveEntites = new ArrayList<>();
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
     *
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
    static void initialize() {
        documentsAmount = 1;
    }

    /**
     * add term to the hash map of entities
     *
     * @param term - the entity to add
     */
    void addBigLetter(Term term) {
        if (Character.isUpperCase(term.getValue().charAt(0))) {
            if (bigLetterTerms.contains(term))
                bigLetterTerms.remove(term);
            term.setRank(calculateDominantEntity(term));
            bigLetterTerms.add(term);
        }
    }

    /**
     * remove the entity from the hash map
     *
     * @param term the entity
     */
    private void removeEntity(Term term) {
        if (bigLetterTerms.contains(term.getValue().toUpperCase()))
            bigLetterTerms.remove(term.getValue().toUpperCase());
    }

    /**
     * Method for calculating the rank of a term(Used for finding the dominant entities of a document)
     *
     * @param term - The term to rank
     * @return - The term's rank
     */
    private double calculateDominantEntity(Term term) {
        double position = (1 - (0.1 * ((int) term.getPositionInDocument().get(indexId) - 96)));
        return (-1) * term.getAmount() * position;
    }

    /**
     * Finds and returns the dominant entities of the document
     *
     * @return - The dominant entities
     */
    ArrayList<String> getEntities() {
        List<Term> mapValues = new ArrayList(bigLetterTerms);
        Collections.sort(mapValues, (Comparator.comparingDouble((o) -> o.getRank())));
        int num = 0;
        for (int entity = mapValues.size() - 1; num < 5 && entity >= 0; entity--) {
            if (Character.isUpperCase(mapValues.get(entity).getValue().charAt(0))) {
                topFiveEntites.add(mapValues.get(entity).getValue());
                num++;
            }
        }
        return topFiveEntites;
    }

    /**
     * Retruns the title of the document
     *
     * @return
     */
    String getTitle() {
        return title;
    }

    /**
     * Returns the dominant entities of a document
     *
     * @return - The top five entities
     */
    ArrayList<String> getTopFive() {
        return topFiveEntites;
    }

    /**
     * Adds an entity to the top five entities
     *
     * @param entity - The entity to add
     */
    void addEntity(String entity) {
        topFiveEntites.add(entity);
    }

    /**
     * Returns the document's max term frequency
     *
     * @return - The max term frequency
     */
    int getMax_tf() {
        return max_tf;
    }

    /**
     * @return the rank of the document
     */
    double getRank() {
        return rank;
    }

    /**
     * @param rank the rank of the document after calculation in ranker
     */
    void setRank(double rank) {
        this.rank = rank;
    }


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

}