package Controller;

import Model.Model;
import Model.Term;

import java.util.HashMap;
import java.util.HashSet;

import Model.City;
import javafx.scene.control.ListView;

/**
 * The controller class of the search engine, controls the application.
 */
public class Controller {
    private Model model;
    private String stopWordsPath;
    private String postingPath;

    /**
     * The controller default constructor
     */
    public Controller() {
        model = new Model();
    }

    /**
     * Tells the model to reset the dictionaries
     *
     * @param resetCities - If the cities should be reset too
     */
    public void resetDictionaries(boolean resetCities) {
        model.resetDictionaries(resetCities);
    }

    /**
     * Tells the model to read the files with the given parameters
     */
    public void readFiles(String corpusPath) {
        model.readFiles(corpusPath, stopWordsPath, postingPath);
    }

    /**
     * Tells the model to load the dictionaries
     */
    public void loadDictionaries() {
        model.loadTermsDictionary();
        model.loadDocsDictionary();
        model.loadCitiesDictionary();
        model.loadLanguages();
    }

    /**
     * Tells the model to return the terms list
     *
     * @return - List of terms
     */
    public HashMap<String, Term> getTermsToDisplay() {
        return model.getTermsDictionary();
    }

    /**
     * Tells the model to return the total amount of documents
     *
     * @return - The total amount of documents
     */
    public Integer getTotalDocuments() {
        return model.getTotalDocuments();
    }

    /**
     * Tells the model to return the total amount of terms
     *
     * @return - Total amount of terms
     */
    public Integer getTotalTerms() {
        return model.getTotalTerms();
    }

    /**
     * Tells the model to return the total time it took to create the dictionaries and post files
     *
     * @return - The total time
     */
    public Double getTotalTime() {
        return model.getTotalTime();
    }

    /**
     * Tells the model to return all the languages
     *
     * @return - All the languages
     */
    public HashSet<String> getLanguages() {
        return model.getLanguages();
    }

    /**
     * Tells the model if stemming is used
     *
     * @param selected - If to use stemming
     */
    public void setStemming(boolean selected) {
        model.setStemming(selected);
    }

    /**
     * Sets the path to the stop words file
     *
     * @param stopWordsPath - The stop words path
     */
    public void setStopWordsPath(String stopWordsPath) {
        this.stopWordsPath = stopWordsPath;
    }

    /**
     * Tells the model to load the stop words
     *
     * @param stopWordsPath - The path to the stop words file
     */
    public void loadStopWords(String stopWordsPath) {
        model.loadStopWords(stopWordsPath);
    }

    /**
     * Tells the model where to create the posting files
     *
     * @param postingPath - Path to the required posting files location
     */
    public void setPostingPath(String postingPath) {
        this.postingPath = postingPath;
        model.setPostingPathDestination(postingPath);
    }

    /*
     *  call to function in the model to create a queryDocument from the user query
     *
     * @param query - the query from the user or file
     */
    public void addQueryDocument(String query) {
        model.addQueryDocument(query);
    }


    /*
     *  call to function in the model fet all the cities in corpus
     *
     */
    public HashMap<String, City> getCitiesDictionary() {
        return model.getCitiesDictionary();
    }

    /**
     * @param allDocuments   - the documents dictionary
     * @param bigLetterTerms - all the
     */
    public void setQueriesResult(ListView<String> allDocuments, HashMap<String, ListView<String>> bigLetterTerms) {
        model.setQueriesResult(allDocuments, bigLetterTerms);
    }

    /**
     * Tells the model to read the queries file
     *
     * @param path - The path to the quries
     */
    public void readQueriesFile(String path) {
        model.readQueriesFile(path);
    }

    /*
     *  call to function in the model to save the results of the query
     *
     * @param savePath - the path that the user choose to save the results
     */
    public void writeSave(String savePath) {
        model.writeSave(savePath);
    }

    /**
     * Tells the model if to use semantic reference when processing the queries
     *
     * @param selected
     */
    public void setSemantic(boolean selected) {
        model.setSemantic(selected);
    }

    public void setSelectedCities(HashSet<String> selectedCities) {
        model.setSelectedCities(selectedCities);
    }

}