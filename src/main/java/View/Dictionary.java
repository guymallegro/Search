package View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
public class Dictionary {

    public View view;
    public javafx.scene.control.ScrollPane scrollPane;
    public javafx.scene.control.TextArea allTerms;
    private ArrayList<String> dictionary;

    public void displayDictionary() {
        StringBuilder lines = new StringBuilder();
        int size = dictionary.size();
        for (int i = 0; i < size; i ++) {
            lines.append(dictionary.get(i));
            lines.append("\n");
        }
        scrollPane.setHmax(dictionary.size());
        allTerms.setText(lines.toString());
        allTerms.setEditable(false);
    }

    public void setView(View view) { this.view = view; }

    public void setDictionary(ArrayList<String> dictionary) { this.dictionary = dictionary; }
}
