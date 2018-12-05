package View;

import Controller.Controller;
import Model.Model;
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
    ArrayList<String> termsDictionary;
    ArrayList<String> docsDictionary;
    ArrayList<String> citiesDictionary;
    public javafx.scene.control.Button start;
    public javafx.scene.control.Button reset;
    public javafx.scene.control.Button loadTermsDictionary;
    public javafx.scene.control.Button loadDocsDictionary;
    public javafx.scene.control.Button loadCitiesDictionary;
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
        Scene scene = new Scene(root, 600, 480);
        scene.getStylesheets().add(getClass().getResource("/sample.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Start");
        browser browser = myLoader.getController();
        browser.setController(controller);
        browser.setView(this);
        controller.setStemming(stemming.isSelected());
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
        controller.resetDictionaries(true);
    }

    public void setPostingPath(String posting) {
        postingPath = posting;
    }

    public void setController(Controller controller) {
        this.controller = controller;
        this.controller.setView(this);
    }

    public void loadTermsDictionary() {
        termsDictionary = new ArrayList<>();
        String path = postingPath + "/termsDictionary.txt";
        if (stemming.isSelected())
            path = postingPath + "/termsDictionaryWithStemming.txt";
        File file = new File(path);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String term = scanner.nextLine();
                termsDictionary.add(term);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Cannot open the terms dictionary");
        }
        loadTermsDictionary.setDisable(true);
        displayTermsDictionary.setDisable(false);
    }

    public void loadDocsDictionary() {
        String path = postingPath + "/documentsDictionary.txt";
        if (stemming.isSelected())
            path = postingPath + "/documentsDictionaryWithStemming.txt";
        File file = new File(path);
        try {
            Scanner scanner = new Scanner(file);
            HashMap<Integer, ArrayList<Object>> docsDictionary = new HashMap<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                int docIndex = Integer.parseInt(line.substring(1, line.indexOf(":")));
                String[] info = line.substring(line.indexOf(":")).split(",");
                ArrayList<Object> attributes = new ArrayList<>();
                attributes.add(0, info[0]);
                attributes.add(1, info[1]);
                if (info.length == 3)
                    attributes.add(2, info[2]);
                else
                    attributes.add(2, "");
                docsDictionary.put(docIndex, attributes);
            }
            controller.setDocsDictionary(docsDictionary);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot open the documents dictionary");
        }
        loadDocsDictionary.setDisable(true);
    }

    public void loadCitiesDictionary() {
        citiesDictionary = new ArrayList<>();
        String path = postingPath + "/postCities.txt";
        if (stemming.isSelected())
            path = postingPath + "/postCitiesWithStemming.txt";
        File file = new File(path);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String term = scanner.nextLine();
                citiesDictionary.add(term);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Cannot open the cities dictionary");
        }
        loadCitiesDictionary.setDisable(true);
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
        stage.setTitle("Start");
        Dictionary dic = myLoader.getController();
        dic.setView(this);
        StringBuilder lines = new StringBuilder();
        int size = termsDictionary.size();
        for (int i = 0; i < size; i++) {
            lines.append(termsDictionary.get(i));
            lines.append("\n");
        }
        dic.displayDictionary(size, lines.toString());
        stage.show();
    }

    public void initializeLanguages() {
        HashSet<String> languageList = controller.getLanguages();
        Object[] sortedterms = languageList.toArray();
        Arrays.sort(sortedterms);
        for (int i = 0; i < sortedterms.length; i++) {
            languages.getItems().add(sortedterms[i]);
        }
        loadTermsDictionary.setDisable(false);
        loadDocsDictionary.setDisable(false);
        loadCitiesDictionary.setDisable(false);
        languages.setDisable(false);
        query.setDisable(false);
    }

}
