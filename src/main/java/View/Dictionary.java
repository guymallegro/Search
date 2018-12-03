package View;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Dictionary {

    public javafx.scene.control.ScrollPane scrollPane;
    public javafx.scene.control.TextArea allTerms;
    private HashMap<String, ArrayList<Object>> dictionary;


    public void displayDictionary() {
        Object[] sortedterms = dictionary.keySet().toArray();
        Arrays.sort(sortedterms);
        StringBuilder lines = new StringBuilder();
        for (int i = 0; i < sortedterms.length; i ++) {
            lines.append(sortedterms[i]);
            lines.append(" (" );
            lines.append(dictionary.get(sortedterms[i]).get(0));
            lines.append(")" );
            lines.append("\n");
        }
        scrollPane.setHmax(sortedterms.length);
        allTerms.setText(lines.toString());
        allTerms.setEditable(false);
    }

    public void setDictionary(HashMap<String, ArrayList<Object>> dictionary) { this.dictionary = dictionary; }
}
