package Model;

import java.util.*;

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

    public String getAmountInDocuments() {
        StringBuilder sb = new StringBuilder();
        Map<Integer, Integer> map = new TreeMap(inDocuments);
        Set set2 = map.entrySet();
        Iterator iterator2 = set2.iterator();
        while (iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry) iterator2.next();
            sb.append(me2.getValue()).append(",");
        }
        return sb.toString();
    }


    public void clear() {
        inDocuments.clear();
    }
}