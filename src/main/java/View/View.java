package View;

import Controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.ParallelCamera;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * The view class which represents the main view
 */
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
    public javafx.scene.control.ListView<String> allTerms;
    private Parent root;

    /**
     * The function which moves the user to the browser page
     */
    public void startWindow() {
        root = null;
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

    /**
     * Initializes the languages
     */
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

    /**
     * The function which tells the controller to reset all the posting files and the dictionaries
     */
    public void reset() {
        File currentDirectory = new File(postingPath);
        String[] allFiles = currentDirectory.list();
        for (String file : allFiles) {
            File currentFile = new File(postingPath + "\\" + file);
            currentFile.delete();
        }
        controller.resetDictionaries(true);
    }

    /**
     * Tells the controller to load the dictionaries
     */
    public void loadDictionaries() {
        controller.loadDictionaries();
        loadDictionaries.setDisable(true);
        displayTermsDictionary.setDisable(false);
    }

    /**
     * The function that moves the user to the display of the terms
     */
    public void displayTermsDictionary() {
        Stage stage = new Stage();
        allTerms = new ListView<>();
        Map<Integer, Integer> map = new TreeMap(controller.getTermsToDisplay());
        Set set2 = map.entrySet();
        Iterator iterator2 = set2.iterator();
        while (iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry) iterator2.next();
            allTerms.getItems().add(me2.getKey() + " (" + ((ArrayList) me2.getValue()).get(0)+")");
        }
        Scene scene = new Scene(new Group());
        stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
        final VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 0, 0, 10));
        vBox.getChildren().addAll(allTerms);
        vBox.setAlignment(Pos.CENTER);
        Group group = ((Group) scene.getRoot());
        group.getChildren().addAll(vBox);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The function that closes the application
     */
    public void exit() {
        System.exit(0);
    }

    /**
     * Sets the controller to the given controller
     *
     * @param controller - The given controller
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Sets the posting path into the given path
     *
     * @param posting - The given path
     */
    void setPostingPath(String posting) {
        postingPath = posting;
    }

    public Parent getRoot() {
        return root;
    }

}
