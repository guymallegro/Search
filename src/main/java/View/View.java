package View;

import Controller.Controller;
import Model.ADocument;
import Model.City;
import Model.Document;
import Model.QueryDocument;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

/**
 * The view class which represents the main view
 */
public class View {
    public MenuButton citiesSelect;
    public TextField corpusPath;
    public TextField postingPath;
    public Button browseCorpus;
    public Button browsePosting;
    private Controller controller;
    public javafx.scene.control.Button processCorpus;
    public javafx.scene.control.Button run;
    public javafx.scene.control.Button reset;
    public javafx.scene.control.Button loadDictionaries;
    public javafx.scene.control.Button displayTermsDictionary;
    public javafx.scene.control.TextField query;
    public javafx.scene.control.CheckBox stemming;
    public javafx.scene.control.ComboBox languages;
    public javafx.scene.control.ListView<String> allDocuments;
    boolean toInitCities = true;
    private HashSet<String> selectedCities;

    /**
     * The function which moves the user to the browser page
     */
    public void processCorpus(ActionEvent actionEvent) {
        if (corpusPath.getText().equals("Enter Path"))
            showAlert("enter please a corpus directory");
        else if (postingPath.getText().equals("Enter Path"))
            showAlert("enter please a posting destination directory");
        else {
            controller.setCorpusPath(corpusPath.getText() + "/");
            controller.setStopWordsPath(corpusPath.getText() + "/stop_words.txt");
            controller.setPostingPath(postingPath.getText());
            controller.readFiles();
            initializeLanguages();
            reset.setDisable(false);
            showFinishMessage();
        }
        controller.setStemming(stemming.isSelected());
        displayTermsDictionary.setDisable(false);
    }

    /**
     *
     */
    public void loadPostingPath() {
        controller.setPostingPath(postingPath.getText());
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

    public void initCities() {
        if (toInitCities) {
            selectedCities = new HashSet<>();
            final List<CheckMenuItem> items = new LinkedList<>();
            HashMap<String, City> citiesDictionary = controller.getCitiesDictionary();
            Map<Integer, Integer> map = new TreeMap(citiesDictionary);
            Set set2 = map.entrySet();
            Iterator iterator2 = set2.iterator();
            while (iterator2.hasNext()) {
                Map.Entry me2 = (Map.Entry) iterator2.next();
                CheckMenuItem item = new CheckMenuItem(((City) me2.getValue()).getCityName());
                item.setOnAction(a -> {
                    if (selectedCities.contains(item.getText())) {
                        selectedCities.remove(item.getText());
                        System.out.println("Removed :" + item.getText());
                    } else {
                        selectedCities.add(item.getText());
                        System.out.println("Selected :" + item.getText());
                    }
                });
                items.add(item);

            }
            citiesSelect.getItems().addAll(items);
            toInitCities = false;
        }
    }

    /**
     * The function which tells the controller to reset all the posting files and the dictionaries
     */
    public void reset() {
        try {
            File currentDirectory = new File(postingPath.getText());
            String[] allFiles = currentDirectory.list();
            for (String file : allFiles) {
                File currentFile = new File(postingPath + "\\" + file);
                currentFile.delete();
            }
            controller.resetDictionaries(true);
        } catch (Exception e) {
            System.out.println("Some of the information is already clear");
        }
        loadDictionaries.setDisable(false);
    }

    /**
     * Tells the controller to load the dictionaries
     */
    public void loadDictionaries() {
        controller.loadDictionaries();
        loadDictionaries.setDisable(true);
        displayTermsDictionary.setDisable(false);
        reset.setDisable(false);
    }

    /**
     * The function that moves the user to the display of the terms
     */
    public void displayTermsDictionary() {
        Stage stage = new Stage();
        allDocuments = new ListView<>();
        Map<Integer, Integer> map = new TreeMap(controller.getTermsToDisplay());
        Set set2 = map.entrySet();
        Iterator iterator2 = set2.iterator();
        while (iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry) iterator2.next();
            allDocuments.getItems().add(me2.getKey() + " (" + ((ArrayList) me2.getValue()).get(0) + ")");
        }
        Scene scene = new Scene(new Group());
        stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
        final VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 0, 0, 10));
        vBox.getChildren().addAll(allDocuments);
        vBox.setAlignment(Pos.CENTER);
        Group group = ((Group) scene.getRoot());
        group.getChildren().addAll(vBox);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The function that sends the query to the model to be processed
     */
    public void run() {
        controller.findRelevantDocuments(query.getText());
        Stage stage = new Stage();
        allDocuments = new ListView<>();
        PriorityQueue<Document> queryDocuments = controller.getQueryDocuemnts();
        List list = new ArrayList(queryDocuments);
        for (int i = 0; i < list.size(); i++) {
            allDocuments.getItems().add(((ADocument) list.get(i)).getId());
        }
        Scene scene = new Scene(new Group());
        stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
        final VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 0, 0, 10));
        vBox.getChildren().addAll(allDocuments);
        vBox.setAlignment(Pos.CENTER);
        Group group = ((Group) scene.getRoot());
        group.getChildren().addAll(vBox);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Sets the controller to the given controller
     *
     * @param controller - The given controller
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void browseCorpus() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Corpus Directory");
        Stage stage = new Stage();
        File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory != null) {
            corpusPath.setText(selectedDirectory.getPath());
        }
    }

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
        String property = "Number Of Documents : " + controller.getTotalDocuments() + "\n" + "Number Of Terms : " + controller.getTotalTerms() + "\nTotal Time : " + controller.getTotalTime() + " seconds";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(property);
        alert.showAndWait();

    }
}
