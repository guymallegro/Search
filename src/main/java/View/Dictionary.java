package View;

import Controller.Controller;
import com.sun.javafx.css.Rule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Dictionary {

    public javafx.scene.control.ScrollPane scrollPane;
    public javafx.scene.control.TextArea allTerms;
    //public javafx.scene.control.ListView allTerms;
    //public javafx.scene.control.Label allTerms;
    //public javafx.scene.control.ScrollBar scroll;
    private HashMap<String, ArrayList<Object>> dictionary;


    public void displayDictionary() {
        Object[] sortedterms = dictionary.keySet().toArray();
        Arrays.sort(sortedterms);
        StringBuilder lines = new StringBuilder();
        ObservableList<String> items = FXCollections.observableArrayList ();
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
