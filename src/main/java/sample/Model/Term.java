package sample.Model;

import java.util.Arrays;
import java.util.HashMap;

public class Term {

    private String value;
    private int amount;
    private HashMap<Integer, Integer> inDocuments;

    public Term(String value) {
        this.value = value;
        amount = 1;
        inDocuments = new HashMap<>();
    }

    public void increaseAmount() {
        amount++;
    }

    public String getValue() {
        return value;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int newAmount) {
        amount = newAmount;
    }

    public void addInDocument(int documentId) {
        if (inDocuments.containsKey(documentId)) {
            //inDocuments.put(documentId, inDocuments.get(documentId)+1);
            inDocuments.replace(documentId, inDocuments.get(documentId) + 1);
        } else {
            inDocuments.put(documentId, 1);
        }
    }

    public Object[] getInDocuments() {
        Object[] sortedterms = inDocuments.keySet().toArray();
        Arrays.sort(sortedterms);
        return sortedterms;
    }

    public void clear(){
        inDocuments.clear();
    }
}