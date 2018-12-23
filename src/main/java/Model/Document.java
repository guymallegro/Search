package Model;

import java.util.*;

import static org.json.XMLTokener.entity;

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

    public void removeTermFromText(Term term) {
        if (textTerms.containsKey(term.getValue()))
            textTerms.remove(term.getValue());
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

    void removeEntity(Term term) {
        bigLetterTerms.remove(term);
    }

    private double calculateDominantEntity(Term term) {
        double position = (1 / (int) term.getPositionInDocument().get(indexId) - 96);
        return (-1) * term.getAmount() * position;
    }

    public ArrayList<String> getEntities() {
        List<Term> mapValues = new ArrayList(bigLetterTerms);
        Collections.sort(mapValues, (Comparator.comparingDouble((o) -> o.getRank())));
        int num = 0;
        for (int entity = mapValues.size() - 1; num < 10 && entity >= 0; entity--) {
            if (Character.isUpperCase(mapValues.get(entity).getValue().charAt(0))) {
                topFiveEntites.add(mapValues.get(entity).getValue());
                num++;
            }
        }
        return topFiveEntites;
    }

    public ArrayList<String> getTopFive() {
        return topFiveEntites;
    }

    public void addEntity(String entity) {
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
    public double getRank() {
        return rank;
    }

    /**
     * @param rank the rank of the document after calculation in ranker
     */
    public void setRank(double rank) {
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