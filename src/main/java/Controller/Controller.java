package Controller;

import Model.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import Model.Document;

import java.util.PriorityQueue;

import Model.City;

/**
 * The controller class of the search engine, controls the application.
 */
public class Controller {
    private Model model;
    private String corpusPath;
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
    public void readFiles() {
        model.readFiles(corpusPath, stopWordsPath, postingPath);
    }

    /**
     * Tells the model to load the dictionaries
     */
    public void loadDictionaries() {
        model.loadTermsDictionary();
        model.loadDocsDictionary();
        model.loadCitiesDictionary();
    }

    /**
     * Tells the model to return the terms list
     *
     * @return - List of terms
     */
    public HashMap<String, ArrayList<Object>> getTermsToDisplay() {
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
     * Tells the model which corpus path to use
     *
     * @param corpusPath - The corpus path
     */
    public void setCorpusPath(String corpusPath) {
        this.corpusPath = corpusPath;
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
     * Tells the model where to create the posting files
     *
     * @param postingPath - Path to the required posting files location
     */
    public void setPostingPath(String postingPath) {
        this.postingPath = postingPath;
        model.setPostingPathDestination(postingPath);
    }

    public void findRelevantDocuments(String query) {
        model.findRelevantDocuments(query);
    }

    public HashMap<String, City> getCitiesDictionary() {
        return model.getCitiesDictionary();
    }

    public ArrayList<Document> getQueryDocuments() {
        return model.getQueryDocuments();
    }

    public HashMap<Integer, Document> getDocumentsDictionary() {
        return model.getDocumentsDictionary();
    }
}