package View;

import Controller.Controller;
import Model.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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
    public TextField queryPath;
    public Button runQueryPath;
    public Button browseQueries;
    public TextField savePath;
    public Button browseSave;
    public Button save;
    private Controller controller;
    public javafx.scene.control.Button processCorpus;
    public javafx.scene.control.Button run;
    public javafx.scene.control.Button reset;
    public javafx.scene.control.Button loadDictionaries;
    public javafx.scene.control.Button displayTermsDictionary;
    public javafx.scene.control.TextField query;
    public javafx.scene.control.CheckBox stemming;
    public javafx.scene.control.CheckBox semantic;
    public javafx.scene.control.ComboBox languages;
    private javafx.scene.control.ListView<String> allDocuments;
    private boolean toInitCities = true;
    private HashSet<String> selectedCities;
    private HashMap<String, ListView<String>> bigLetterTerms;

    public View() {
        allDocuments = new ListView<>();
        bigLetterTerms = new HashMap<>();
        selectedCities = new HashSet<>();
        allDocuments.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<String>() {
                    public void changed(
                            ObservableValue<? extends String> observable,
                            String oldValue, String newValue) {
                        showBigLetterTerms(newValue);
                    }
                });
    }

    /**
     * The function which moves the user to the browser page
     */
    public void processCorpus(ActionEvent actionEvent) {
        if (corpusPath.getText().equals("Enter Path"))
            showAlert("Enter please a corpus directory");
        else if (postingPath.getText().equals("Enter Path"))
            showAlert("Enter please a posting destination directory");
        else {
            showAlert("start in processing");
            controller.setStemming(stemming.isSelected());
            controller.setStopWordsPath(corpusPath.getText() + "/stop_words.txt");
            controller.setPostingPath(postingPath.getText());
            controller.readFiles(corpusPath.getText() + "/");
            initializeLanguages();
            reset.setDisable(false);
            showFinishMessage();
            displayTermsDictionary.setDisable(false);
        }
    }

    /**
     * Initializes the languages
     */
    private void initializeLanguages() {
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
                    if (selectedCities.contains(item.getText().toUpperCase())) {
                        selectedCities.remove(item.getText().toUpperCase());
                    } else {
                        selectedCities.add(item.getText().toUpperCase());
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
        showAlert("reset done successfully");
    }

    /**
     * Tells the controller to load the dictionaries
     */
    public void loadDictionaries() {
        showAlert("start load dictionaries");
        controller.setStemming(stemming.isSelected());
        controller.setPostingPath(postingPath.getText());
        controller.setStemming(stemming.isSelected());
        if (postingPath.getText().equals("Enter Path")) {
            showAlert("Please enter a posting path");
            return;
        }
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
        allDocuments.getItems().clear();
        Map<Integer, Integer> map = new TreeMap(controller.getTermsToDisplay());
        Set set2 = map.entrySet();
        Iterator iterator2 = set2.iterator();
        while (iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry) iterator2.next();
            allDocuments.getItems().add(me2.getKey() + " (" + ((Term) me2.getValue()).getAmount() + ")");
        }
        Scene scene = new Scene(new Group());
        stage.initModality(Modality.APPLICATION_MODAL);
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

    public void runQuery(ActionEvent actionEvent) {
        showAlert("start find the 50 most relevant documents");
        controller.loadStopWords("./src/main/resources/stop_words.txt");
        controller.setSelectedCities(selectedCities);
        controller.setStemming(stemming.isSelected());
        controller.setSemantic(semantic.isSelected());
        if (((Button) actionEvent.getSource()).getId().equals("run")) {
            controller.addQueryDocument(query.getText());
        } else {
            controller.readQueriesFile(queryPath.getText());
        }
        Stage stage = new Stage();
        allDocuments.getItems().clear();
        bigLetterTerms.clear();
        controller.setQueriesResult(allDocuments, bigLetterTerms);
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

    public void save() {
        controller.writeSave(savePath.getText());
        showAlert("the results saved successfully");
    }

    private void showBigLetterTerms(String docId) {
        if (!bigLetterTerms.containsKey(docId))
            return;
        Stage stage = new Stage();
        stage.setTitle("top Five");
        Scene scene = new Scene(new Group());
        stage.initModality(Modality.APPLICATION_MODAL);
        final VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 0, 0, 10));
        vBox.getChildren().addAll(bigLetterTerms.get(docId));
        vBox.setAlignment(Pos.CENTER);
        Group group = ((Group) scene.getRoot());
        group.getChildren().addAll(vBox);
        stage.setScene(scene);
        stage.show();

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

    /**
     * browse path for the queries file
     * @param actionEvent
     */
    public void browseQueries(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Queries File");
        Stage stage = new Stage();
        File selectedFile = chooser.showOpenDialog(stage);
        if (selectedFile != null)
            queryPath.setText(selectedFile.getPath());
    }

    /**
     * browse path for the saving of results
     * @param actionEvent
     */
    public void browseSave(ActionEvent actionEvent) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Save results directory");
        Stage stage = new Stage();
        File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory != null) {
            savePath.setText(selectedDirectory.getPath());
        }
    }

    /**
     * browse path for corpus
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
     * browse path for posting files
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
     * Sets the controller to the given controller
     *
     * @param controller - The given controller
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }
}