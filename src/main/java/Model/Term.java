package Model;

import java.util.*;

/**
 * The term class which holds of it required information
 */
public class Term {
    private int amount;
    private String value;
    private double rank;
    private HashMap<Integer, Integer> inDocuments;

    private HashMap<Integer, Character> positionInDocument;

    /**
     * The terms construction which create a term with a given string
     * @param value - The given string
     */
    Term(String value) {
        this.value = value;
        amount = 1;
        inDocuments = new HashMap<>();
        positionInDocument = new HashMap<>();
    }

    /**
     * Increases the total amount the term was found
     */
    void increaseAmount() {
        amount++;
    }

    /**
     * Adds the term to the given document at the given position
     * @param documentId - The given document if
     * @param position - The given position
     */
    void addInDocument(int documentId, double position) {
        if (inDocuments.containsKey(documentId)) {
            inDocuments.replace(documentId, inDocuments.get(documentId) + 1);
        } else {
            inDocuments.put(documentId, 1);
            if (position < 0.33) {
                positionInDocument.put(documentId, 'a');
            } else if (position < 0.66) {
                positionInDocument.put(documentId, 'b');
            } else
                positionInDocument.put(documentId, 'c');
        }
    }

    void setInDocument (int documentId, int amount, char position) {
        inDocuments.put(documentId, amount);
        positionInDocument.put(documentId, position);
    }

    /**
     * Custom toString function which returns the term total amount and the amount of documents it was found at.
     * @return - The total amount and the amount of documents it was found at
     */
    public String toString() {
        return amount + "," + inDocuments.size();
    }

    public HashMap<Integer, Character> getPositionInDocument() {
        return positionInDocument;
    }

    public double getRank() {
        return rank;
    }

    /**
     * Returns the total amount of times the term was found
     * @return - THe total amount of times
     */
    public int getAmount() { return amount; }

    /**
     * Returns the value of the term
     * @return - The value of the term
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns all the documents the term was found at sorted
     * @return - The documents the term was found at sorted
     */
    public Object[] getInDocuments() {
        Object[] sortedterms = inDocuments.keySet().toArray();
        Arrays.sort(sortedterms);
        return sortedterms;
    }

    /**
     * Returns all the documents the term was found at sorted
     * @return - The documents the term was found without sorting
     */
    public HashMap<Integer, Integer> getUnsortedInDocuments () { return inDocuments;}

    /**
     * Returns a string with all the documents the term was found at with the amount of times it was found
     * @return string of all the documents o terms
     */
    public String getAmountInDocuments() {
        StringBuilder sb = new StringBuilder();
        Map<Integer, Integer> map = new TreeMap(inDocuments);
        Set set2 = map.entrySet();
        Iterator iterator2 = set2.iterator();
        while (iterator2.hasNext()){
            Map.Entry me2 = (Map.Entry) iterator2.next();
            sb.append(positionInDocument.get(me2.getKey()));
            sb.append(me2.getValue()).append(",");
        }
        sb.deleteCharAt(sb.toString().length() - 1);
        return sb.toString();
    }

    /**
     * Sets the term value into a given value
     * @param newValue - The given value
     */
    void setValue(String newValue) {value = newValue;}

    /**
     * Sets the term amount in corpus
     * @param amount - the frequency of the term in corpus
     */
    void setAmount(int amount) {
        this.amount = amount;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }
}