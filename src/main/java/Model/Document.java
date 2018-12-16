package Model;

import java.util.HashMap;

/**
 * The document class which holds all of its required information
 */
public class Document extends ADocument {
    private int max_tf;
    private HashMap<String, String> cities;
    private String date;
    private String title;
    private HashMap<Term, Integer> titleTerms;
    private HashMap<Term, Integer> dateTerms;

    /**
     * The default document constructor
     */
    public Document() {
        titleTerms = new HashMap<>();
        dateTerms = new HashMap<>();
        cities = new HashMap<>();
        max_tf = 0;
        indexId = documentsAmount++;
        content = "";
    }

    /**
     * Adds a term which was found on this document text
     *
     * @param term - The found term
     */
    void addTermToText(Term term) {
        if (!textTerms.containsKey(term))
            textTerms.put(term, 1);
        else {
            textTerms.put(term, textTerms.get(term) + 1);
        }
        if (textTerms.get(term) > max_tf)
            max_tf = textTerms.get(term);
    }

    /**
     * Initialize the documents amount to 1
     */
    void initialize() {
        documentsAmount = 1;
        indexId = documentsAmount;
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
