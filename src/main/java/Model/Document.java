package Model;

import java.util.HashMap;

/**
 * The document class which holds all of its required information
 */
public class Document {
    private String id;
    private int indexId;
    private static int documentsAmount = 1;
    private int max_tf;
    private int length;
    private HashMap<Term, Integer> textTerms;
    private String content;
    private String city;
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
        textTerms = new HashMap<>();
        cities = new HashMap<>();
        max_tf = 0;
        indexId = documentsAmount++;
        content = "";
    }

    /**
     * Adds a term which was found on this document text
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

    /** // @TODO Ask why its 1 and not 0
     * Initialize the documents amount to 1
     */
    void initialize() {
        documentsAmount = 1;
        indexId = documentsAmount;
    }

    /**
     * Returns the document's max term frequency
     * @return - The max term frequency
     */
    int getMax_tf() {
        return max_tf;
    }

    /**
     * Returns the document's city
     * @return - The city
     */
    String getCity() {
        return city;
    }

    /**
     * Returns the document content
     * @return - The document's content
     */
    String getContent() {
        return content;
    }

    /**
     * Returns the document's index id
     * @return - The index id
     */
    int getIndexId() {
        return indexId;
    }

    /**
     * Returns the document content length
     * @return - The document's  content length
     */
    int getLength() {
        return length;
    }

    /**
     * Returns the document's text terms
     * @return - The text terms
     */
    HashMap<Term, Integer> getTextTerms() {
        return textTerms;
    }

    /**
     * Returns the document's id
     * @return - The id
     */
    String getId() {
        return id;
    }

    /**
     * Sets the document's id to the given id
     * @param id - The given id
     */
    void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the document's date to the given date
     * @param date - The given date
     */
    void setDate(String date) {
        this.date = date;
    }

    /**
     * Sets the document's title to the given title
     * @param title - The given title
     */
    void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the document's content length to the given length
     * @param length - The given length
     */
    void setLength(int length) {
        this.length = length;
    }

    /**
     * Sets the document's content to the given content
     * @param content - The given content
     */
    void setContent(String content) {
        this.content = content;
    }

    /**
     * Sets the document's city to the given city
     * @param city - The given city
     */
    void setCity(String city) {
        this.city = city;
    }
}
