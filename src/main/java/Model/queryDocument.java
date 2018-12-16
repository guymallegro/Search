package Model;

import java.util.HashMap;

public class queryDocument extends ADocument {


    void addTermToText(Term term) {
        if (!textTerms.containsKey(term))
            textTerms.put(term, 1);
        else {
            textTerms.put(term, textTerms.get(term) + 1);
        }
    }

}
