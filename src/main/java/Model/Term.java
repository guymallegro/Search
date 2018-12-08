package Model;

import java.util.*;

public class Term {

    private int amount;
    private String value;
    private HashMap<Integer, Integer> inDocuments;
    private HashMap<Integer, Character> positionInDocument;

    Term(String value) {
        this.value = value;
        amount = 1;
        inDocuments = new HashMap<>();
        positionInDocument = new HashMap<>();
    }

    void increaseAmount() {
        amount++;
    }

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

    public String toString() {
        return amount + "," + inDocuments.size();
    }

    int getAmount() {
        return amount;
    }

    Object[] getInDocuments() {
        Object[] sortedterms = inDocuments.keySet().toArray();
        Arrays.sort(sortedterms);
        return sortedterms;
    }

    String getAmountInDocuments() {
        StringBuilder sb = new StringBuilder();
        Map<Integer, Integer> map = new TreeMap(inDocuments);
        Set set2 = map.entrySet();
        Iterator iterator2 = set2.iterator();
        while (iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry) iterator2.next();
            sb.append(positionInDocument.get(me2.getKey()));
            sb.append(me2.getValue()).append(",");
        }
        sb.deleteCharAt(sb.toString().length() - 1);
        return sb.toString();
    }

    void setValue(String newValue) {value = newValue;}
}