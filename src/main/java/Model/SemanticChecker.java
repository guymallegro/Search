package Model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SemanticChecker {
    Model model;
    ArrayList <String> semantic;

    /**
     * The SemanticChecker constructor
     *
     * @param terms - The terms of the query
     */
    SemanticChecker(Model model, List<String> terms) {
        this.model = model;
        HTTPRequest request = new HTTPRequest();
        semantic = new ArrayList<>();
        String semanticWord = "";
        for (int term = 0; term < terms.size(); term ++){
            JSONObject jsonDetails = request.post("https://api.datamuse.com/words?ml="+terms.get(term));
            JSONArray result = jsonDetails.getJSONArray("result");
            int i = 0;
            for (Object obj : result) {
                if (i >= 10)
                    break;
                JSONObject data = (JSONObject) obj;
                semanticWord = data.get("word").toString();
                if (model.getTermsDictionary().containsKey(semanticWord)){
                    semantic.add(semanticWord);
                    i++;
                }
            }
        }
    }

    /**
     * Returns the semantic terms of given term
     *
     * @return - The given term info
     */
    public ArrayList<String> getSemantic() {
        return semantic;
    }
}
