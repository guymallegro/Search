package View;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
public class Dictionary {

    public View view;
    public javafx.scene.control.Button back;
    public javafx.scene.control.TextArea allTerms;
    public javafx.scene.control.ScrollPane scrollPane;

    public void displayDictionary(int height, String content) {
        scrollPane.setHmax(height);
        allTerms.setText(content);
        allTerms.setEditable(false);
    }

    public void back (ActionEvent actionEvent){
        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.close();
    }

    public void setView(View view) { this.view = view; }
}
