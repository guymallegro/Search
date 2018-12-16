package Model;


public class QueryDocument extends ADocument {

    public QueryDocument(String content){
        this.content = content;
    }


    void addTermToText(Term term) {
        if (!textTerms.containsKey(term))
            textTerms.put(term, 1);
        else {
            textTerms.put(term, textTerms.get(term) + 1);
        }
    }

}
