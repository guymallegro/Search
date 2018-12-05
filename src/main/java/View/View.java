package View;

import Controller.Controller;
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
    ArrayList<String> citiesDictionary;
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
            else
                System.out.println("no deletion");
        }
        controller.resetDictionaries(true);
    }

    public void loadDictionaries() {
        loadTermsDictionary();
        loadDocsDictionary();
        loadCitiesDictionary();
    }

    public void loadTermsDictionary() {
        termsDictionary = new ArrayList<>();
        String path = postingPath + "/termsDictionary.txt";
        if (stemming.isSelected())
            path = postingPath + "/termsDictionaryWithStemming.txt";
        File file = new File(path);
        try {
            Scanner scanner = new Scanner(file);
            HashMap<String, ArrayList<Object>> termsDictionary = new HashMap<>();
            while (scanner.hasNextLine()) {
                String term = scanner.nextLine();
                String temp = term.substring(term.indexOf(':') + 1);
                term = term.substring(1, term.indexOf(':')) + " (";
                term += temp.substring(0, temp.indexOf(',')) + ")";

                //termsDictionary.put(term, );
            }
            controller.setTermsDictionary(termsDictionary);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot open the terms dictionary");
        }
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

    public void setController(Controller controller) {
        this.controller = controller;
        this.controller.setView(this);
    }

    public void setPostingPath(String posting) {
        postingPath = posting;
    }

        public void initializeLanguages() {
            HashSet<String> languageList = controller.getLanguages();
            Object[] sortedterms = languageList.toArray();
            Arrays.sort(sortedterms);
            for (int i = 0; i < sortedterms.length; i++) {
                languages.getItems().add(sortedterms[i]);
            }
            loadDictionaries();
            languages.setDisable(false);
            query.setDisable(false);
        }

}
