package View;

import Controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class View {
    private Controller controller;
    private String postingPath;
    ArrayList <String> dictionary;
    public javafx.scene.control.Button start;
    public javafx.scene.control.Button reset;
    public javafx.scene.control.Button displayDictionary;
    public javafx.scene.control.Button loadDictionary;
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
        Scene scene = new Scene(root, 600, 480);
        scene.getStylesheets().add(getClass().getResource("/sample.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Start");
        browser browser = myLoader.getController();
        browser.setController(controller);
        browser.setView(this);
        controller.setStemming (stemming.isSelected());
        stage.show();
    }

    public void reset(ActionEvent actionEvent) {
        File currentDirectory = new File(postingPath);
        String[] allFiles = currentDirectory.list();
        for (String file : allFiles) {
            File currentFile = new File(postingPath + "\\" + file);
            if (currentFile.delete())
                System.out.println("Current file " + file + " is closed");
            else // @TODO dont forget to close the posting temporary files
                System.out.println("no deletion");
        }
        controller.resetDictionaries();
    }

    public void setPostingPath (String posting){ postingPath = posting; }

    public void setController(Controller controller) {
        this.controller = controller;
        this.controller.setView(this);
    }

    public void loadDictionary () {
        dictionary = new ArrayList<>();
        String path = postingPath + "/termsDictionary.txt";
        if (stemming.isSelected())
            path = postingPath + "/termsDictionaryWithStemming.txt";
        File file = new File(path);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String term = scanner.nextLine();
                dictionary.add(term);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Cannot open the terms dictionary");
        }
        loadDictionary.setDisable(true);
        displayDictionary.setDisable(false);
    }

    public void displayDictionary(ActionEvent actionEvent) {
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
        stage.setTitle("Start");
        Dictionary dic = myLoader.getController();
        dic.setDictionary(dictionary);
        dic.setView(this);
        stage.show();
        dic.displayDictionary();
    }

    public void initializeLanguages (){
        HashSet <String> languageList = controller.getLanguages();
        Object[] sortedterms = languageList.toArray();
        Arrays.sort(sortedterms);
        for (int i = 0; i < sortedterms.length; i ++) {
            languages.getItems().add(sortedterms[i]);
        }
        loadDictionary.setDisable(false);
        languages.setDisable(false);
        query.setDisable(false);
    }

}
