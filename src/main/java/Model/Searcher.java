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

    public ArrayList<String> findRelevantDocs(String query){
        model.findTermFromPosting();

    }


}
