package Model;

import java.util.*;

public class Ranker {
    public final double K= 1.2;
    public final double B= 0.75;
    public double avgDocLength;
    public HashMap<String, ArrayList<Object>> termsDictionary;
    public HashMap<Integer, ArrayList<Object>> documentsDictionary;
    public double docsAmount=0;


    public Ranker(HashMap<String, ArrayList<Object>> termsDictionary,HashMap<Integer, ArrayList<Object>> documentsDictionary){
        this.termsDictionary=termsDictionary;
        this.documentsDictionary=documentsDictionary;

    }

    private void corpusAvgDocLength(){
        int totalLength=0;
        Map<Integer, Integer> map = new TreeMap(documentsDictionary);
        Set set2 = map.entrySet();
        Iterator iterator2 = set2.iterator();
        while (iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry) iterator2.next();
            docsAmount++;
            totalLength+=(Integer)((ArrayList)me2.getValue()).get(3);
        }
        avgDocLength=totalLength/docsAmount;

    }

    private int amountOfDocsPerTerm(String term){
        return 0;

    }

}
