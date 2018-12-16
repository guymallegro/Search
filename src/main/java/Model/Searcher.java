package Model;

import java.util.ArrayList;
import java.util.HashMap;

public class Searcher {
    private Model model;
    private Ranker ranker;
    private HashMap<String, City> cityDictionary;

    public Searcher(HashMap<String, ArrayList<Object>> termsDictionary, HashMap<Integer, ArrayList<Object>> documentsDictionary, HashMap<String, City> cityDictionary, Model model) {
        this.model = model;
        this.cityDictionary = cityDictionary;
        ranker = new Ranker(termsDictionary, documentsDictionary);

    }

    public ArrayList<String> findRelevantDocs(HashMap<String, Term> terms){
        Object[] sortedTerms = terms.keySet().toArray();
      //  sort (sortedTerms)

        //Arrays.sort(sortedTerms);
        ArrayList<String> termsToFind = new ArrayList<>();
        char currentLetter = (terms.get(sortedTerms[0])).getValue().charAt(0);
        for (int i = 0; i < sortedTerms.length; i++) {
            termsToFind.add((terms.get(sortedTerms[i])).getValue());
            while (currentLetter == (terms.get(sortedTerms[i])).getValue().charAt(0)) {
                termsToFind.add((terms.get(sortedTerms[i])).getValue());
                i++;
            }
            termsToFind.addAll(model.findTermFromPosting(termsToFind));
            currentLetter = (terms.get(sortedTerms[i])).getValue().charAt(0);
        }
        return termsToFind;
    }

//    public void sort (Object [] terms){
//        for (int i = 0; i < terms.length; i++){
//
//        }
//    }

}
