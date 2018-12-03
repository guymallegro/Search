package View;

import Controller.Controller;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

public class browser {
    private View myView;
    private Controller myController;
    public javafx.scene.control.Button OK;
    public javafx.scene.control.TextField corpusPath;
    public javafx.scene.control.TextField postingPath;
    public javafx.scene.control.Button browseCorpus;
    public javafx.scene.control.Button browsePosting;


    public void browseCorpus(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Corpus Directory");
        //chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Directory", "*"));
        Stage stage = new Stage();
        File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory != null) {
            corpusPath.setText(selectedDirectory.getPath());
        }
    }

    public void browsePosting(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Posting Directory");
        //chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Directory", "*"));
        Stage stage = new Stage();
        File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory != null) {
            postingPath.setText(selectedDirectory.getPath());
        }
    }

    public void ok(ActionEvent actionEvent) throws IOException {
        if (corpusPath.getText().equals("Enter Path"))
            showAlert("enter please a corpus directory");
        else if (postingPath.getText().equals("Enter Path"))
            showAlert("enter please a posting destination directory");
        else {
            myController.setCorpusPath(corpusPath.getText() + "\\");
            myController.setStopWordsPath(corpusPath.getText() + "\\stop_words.txt");
            myController.setPostingPath(postingPath.getText());
            myView.setPostingPath(postingPath.getText());

            Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            window.close();
            myController.readFiles();
            myView.initializeLanguages();
        }
    }

    public void showAlert(String property) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(property);
        alert.showAndWait();
    }

    public void setView(View view) { myView = view; }

    public void setController(Controller controller) { myController = controller; }

}

