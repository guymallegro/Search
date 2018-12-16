package View;

import Controller.Controller;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;


/**
 * The browser view class which represents the window where the paths are selected
 */
public class Browser {
    private View myView;
    private Controller myController;
    public javafx.scene.control.Button OK;
    public javafx.scene.control.Button back;
    public javafx.scene.control.Button browseCorpus;
    public javafx.scene.control.Button browsePosting;
    public javafx.scene.control.TextField corpusPath;
    public javafx.scene.control.TextField postingPath;


    /**
     * Function which lets the user choose where the corpus directory is
     */
    public void browseCorpus() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Corpus Directory");
        Stage stage = new Stage();
        File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory != null) {
            corpusPath.setText(selectedDirectory.getPath());
        }
    }

    /**
     * A function which lets the user choose where to save the posting files
     */
    public void browsePosting() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Posting Directory");
        Stage stage = new Stage();
        File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory != null) {
            postingPath.setText(selectedDirectory.getPath());
        }
    }

    /**
     * The function which tells the controller to sart reading the files
     *
     * @param actionEvent - Click of the ok button
     */
    public void ok(ActionEvent actionEvent) {
        if (corpusPath.getText().equals("Enter Path"))
            showAlert("enter please a corpus directory");
        else if (postingPath.getText().equals("Enter Path"))
            showAlert("enter please a posting destination directory");
        else {
            myController.setCorpusPath(corpusPath.getText() + "/");
            myController.setStopWordsPath(corpusPath.getText() + "/stop_words.txt");
            myController.setPostingPath(postingPath.getText());
            myView.setPostingPath(postingPath.getText());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.close();
            myController.readFiles();
            myView.initializeLanguages();
            myView.loadDictionaries.setDisable(false);
            myView.reset.setDisable(false);
            showFinishMessage();
        }
    }

    /**
     * A function which returns the user to the previous window
     *
     * @param actionEvent - The back button click
     */
    public void back(ActionEvent actionEvent) {
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.close();
    }

    /**
     * A function which shows an alret to the user showing the given string
     *
     * @param property - The given string
     */
    private void showAlert(String property) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(property);
        alert.showAndWait();
    }

    /**
     * A function which shows the user the finish message with its required information
     */
    private void showFinishMessage() {
        String property = "Number Of Documents : " + myController.getTotalDocuments() + "\n" + "Number Of Terms : " + myController.getTotalTerms() + "\nTotal Time : " + myController.getTotalTime() + " seconds";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(property);
        alert.showAndWait();

    }

    /**
     * Sets the view to the given view
     *
     * @param view - The given view
     */
    void setView(View view) {
        myView = view;
    }

    /**
     * Sets the controller to the given controller
     *
     * @param controller - The given controller
     */
    void setController(Controller controller) {
        myController = controller;
    }

}

