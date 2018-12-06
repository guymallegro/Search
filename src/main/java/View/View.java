package View;

import Model.CityInfo;
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
    StringBuilder lines;
    HashMap<String, ArrayList<Object>> termsDictionary;
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
        loadDictionaries.setDisable(false);
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
        termsDictionary = new HashMap<>();
        String path = postingPath + "/termsDictionary.txt";
        if (stemming.isSelected())
            path = postingPath + "/termsDictionaryWithStemming.txt";
        File file = new File(path);
        try {
            lines = new StringBuilder();
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String term = line.substring(1, line.indexOf(":"));
                String[] info = line.substring(line.indexOf(":") + 1).split(",");
                ArrayList<Object> attributes = new ArrayList<>();
                attributes.add(0, info[0]);
                attributes.add(1, info[1]);
                termsDictionary.put(term, attributes);
                lines.append("<");
                lines.append(term);
                lines.append(": (");
                lines.append(info[0]);
                lines.append(")");
                lines.append("\n");
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
                String[] info = line.substring(line.indexOf(":") + 1).split(",");
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
        String path = postingPath + "/citiesDictionary.txt";
        if (stemming.isSelected())
            path = postingPath + "/citiesDictionaryWithStemming.txt";
        File file = new File(path);
        try {
            Scanner scanner = new Scanner(file);
            HashMap<String, CityInfo> citiesDictionary = new HashMap<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] info = line.substring(line.indexOf(":") + 1).split(",");
                String city = line.substring(1, line.indexOf(":"));
                CityInfo cityInfo = new CityInfo(city);
                cityInfo.setCountryName(info[0]);
                cityInfo.setCurrency(info[1]);
                cityInfo.setPopulation(info[2]);
                citiesDictionary.put(city, cityInfo);
            }
            controller.setCitiesDictionary(citiesDictionary);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot open the city dictionary");
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
        dic.displayDictionary(termsDictionary.size(), lines.toString());
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
        languages.setDisable(false);
        query.setDisable(false);
    }

}
