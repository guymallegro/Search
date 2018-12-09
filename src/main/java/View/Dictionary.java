package View;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Dictionary {

    /**
     * The view of the terms dictionary view class
     */
    private View view;
    public javafx.scene.control.Button back;
    public javafx.scene.control.TextArea allTerms;
    public javafx.scene.control.ScrollPane scrollPane;

    /**
     * Displays the given string to the user
     * @param content - The given string
     */
    void displayDictionary(String content) {
        allTerms.setText(content);
        allTerms.setEditable(false);
    }

    /**
     * Returns the user to the previous window
     * @param actionEvent - The back button click
     */
    public void back (ActionEvent actionEvent){
        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.close();
    }

    /**
     * Sets the view to a given view
     * @param view - The given view
     */
    void setView(View view) { this.view = view; }
}
