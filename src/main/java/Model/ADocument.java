package Model;

import java.util.HashMap;
import java.util.PriorityQueue;

public abstract class ADocument {
    private String id;
    Model model;
    HashMap<String, Term> textTerms;
    String content;
    private String city;

    public void setIndexId(int indexId) {
        this.indexId = indexId;
    }

    int indexId;

    static int documentsAmount = 1;
    int length;

    public ADocument (){
        city = "";
        textTerms = new HashMap<String, Term>();
    }

    abstract void addTermToText(Term term);

    abstract void removeTermFromText(Term term);

    /**
     * Returns the document's city
     *
     * @return - The city
     */
    public String getCity() {
        return city;
    }

    /**
     * Returns the document content
     *
     * @return - The document's content
     */
    String getContent() {
        return content;
    }

    /**
     * Returns the document's text terms
     *
     * @return - The text terms
     */
    HashMap<String, Term> getTextTerms() {
        return textTerms;
    }

    /**
     * Returns the document's id
     *
     * @return - The id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the document's id to the given id
     *
     * @param id - The given id
     */
    void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the document's content to the given content
     *
     * @param content - The given content
     */
    void setContent(String content) {
        this.content = content;
    }

    /**
     * Sets the document's city to the given city
     *
     * @param city - The given city
     */
    void setCity(String city) {
        this.city = city;
    }

    /**
     * Returns the document's index id
     *
     * @return - The index id
     */
    public int getIndexId() {
        return indexId;
    }

    /**
     * Sets the document's content length to the given length
     *
     * @param length - The given length
     */
    void setLength(int length) {
        this.length = length;
    }

    /**
     * Returns the document content length
     *
     * @return - The document's  content length
     */
    int getLength() {
        return length;
    }


    public void setTextTerms(HashMap<String, Term> tempHash) {textTerms = tempHash;}
}
