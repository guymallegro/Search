package sample.Model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DocumentTerms {
    private String id;
    private static int indexId = 1;
    private int max_tf;
    private String city;
    private HashMap<Term, Integer> titleTerms;
    private HashMap<Term, Integer> dateTerms;
    private HashMap<Term, Integer> textTerms;

    DocumentTerms(String id) {
        this.id = id;
        titleTerms = new HashMap<>();
        dateTerms = new HashMap<>();
        textTerms = new HashMap<>();
        max_tf = 0;
        city="";
        indexId=indexId++;
    }

    public void addTermToTitle(Term term) {
        titleTerms.put(term, 1);
    }


    public void addTermToDate(Term term) {
        dateTerms.put(term, 1);
    }

    void addTermToText(Term term) {
        if (!textTerms.containsKey(term))
            textTerms.put(term, 1);
        else {
            textTerms.put(term, textTerms.get(term) + 1);
        }
        if (textTerms.get(term) > max_tf)
            max_tf = textTerms.get(term);
    }

    public String getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    void print() {
        System.out.println("Document id : " + id);
        Iterator it = textTerms.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Term term = (Term) pair.getKey();
            System.out.println(term.getValue() + " = " + pair.getValue());
            it.remove();
        }
    }
}
