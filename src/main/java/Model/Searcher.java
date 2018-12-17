package Model;

import java.util.ArrayList;
import java.util.Arrays;
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
        String[] sortedTerms = terms.keySet().toArray(new String[terms.size()]);
        Arrays.sort(sortedTerms, String.CASE_INSENSITIVE_ORDER);
        ArrayList<String> termsToFind = new ArrayList<>();
        ArrayList<String> allLines = new ArrayList<>();
        termsToFind.add((terms.get(sortedTerms[0])).getValue());
        char currentLetter = (terms.get(sortedTerms[0])).getValue().charAt(0);
        for (int i = 1; i < sortedTerms.length; i++) {
            while (currentLetter == Character.toLowerCase((terms.get(sortedTerms[i])).getValue().charAt(0))){
                termsToFind.add((terms.get(sortedTerms[i])).getValue());
                if (i + 1 < sortedTerms.length)
                    i++;
                else
                    break;
            }
            allLines.addAll(model.findTermFromPosting(termsToFind));
            currentLetter = Character.toLowerCase((terms.get(sortedTerms[i])).getValue().charAt(0));
            termsToFind = new ArrayList<>();
            termsToFind.add((terms.get(sortedTerms[i])).getValue());
        }
        return termsToFind;
    }

}
