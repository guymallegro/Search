package View;

import Controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class View {
    private String postingPath;
    private Controller controller;
    public javafx.scene.control.Button exit;
    public javafx.scene.control.Button start;
    public javafx.scene.control.Button reset;
    public javafx.scene.control.Button loadDictionaries;
    public javafx.scene.control.Button displayTermsDictionary;
    public javafx.scene.control.TextField query;
    public javafx.scene.control.CheckBox stemming;
    public javafx.scene.control.ComboBox languages;

    public void startWindow(ActionEvent actionEvent) {
        Parent root = null;
        FXMLLoader myLoader = new FXMLLoader();
        try {
            myLoader.setLocation(getClass().getResource("/fxml/browser.fxml"));
            root = myLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, 380, 320);
        scene.getStylesheets().add(getClass().getResource("/sample.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Start");
        Browser browser = myLoader.getController();
        browser.setController(controller);
        browser.setView(this);
        controller.setStemming(stemming.isSelected());
        stage.show();
        loadDictionaries.setDisable(true);
        displayTermsDictionary.setDisable(false);
    }

    void initializeLanguages() {
        HashSet<String> languageList = controller.getLanguages();
        Object[] sortedTerms = languageList.toArray();
        Arrays.sort(sortedTerms);
        for (int i = 0; i < sortedTerms.length; i++) {
            languages.getItems().add(sortedTerms[i]);
        }
        languages.setDisable(false);
        query.setDisable(false);
    }

    public void reset(ActionEvent actionEvent) {
        File currentDirectory = new File(postingPath);
        String[] allFiles = currentDirectory.list();
        for (String file : allFiles) {
            File currentFile = new File(postingPath + "\\" + file);
            if (currentFile.delete())
                System.out.println("Current file " + file + " is closed");
            else
                System.out.println("no deletion");
        }
        controller.resetDictionaries(true);
    }

    public void loadDictionaries() {
        controller.loadDictionaries();
        loadDictionaries.setDisable(true);
        displayTermsDictionary.setDisable(false);
    }

    public void displayTermsDictionary(ActionEvent actionEvent) {
        Parent root = null;
        FXMLLoader myLoader = new FXMLLoader();
        try {
            myLoader.setLocation(getClass().getResource("/fxml/dictionary.fxml"));
            root = myLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root, 600, 480);
        scene.getStylesheets().add(getClass().getResource("/sample.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("terms dictionary");
        Dictionary dic = myLoader.getController();
        dic.setView(this);
        dic.displayDictionary(controller.getTermsToDisplay().toString());
        stage.show();
    }

    public void exit (){
        System.exit(0);
    }

    public void setController(Controller controller) { this.controller = controller; }

    void setPostingPath(String posting) {
        postingPath = posting;
    }

}
