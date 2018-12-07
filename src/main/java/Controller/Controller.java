package Controller;

import Model.Model;
import java.util.HashSet;

public class Controller {
    private Model model;
    private String corpusPath;
    private String stopWordsPath;
    private String postingPath;

    public Controller() {
        model = new Model();
    }

    public void resetDictionaries(boolean resetCities) {
        model.resetDictionaries(resetCities);
    }

    public void readFiles() {
        model.readFiles(corpusPath, stopWordsPath, postingPath);
    }

    public void loadDictionaries() {
        model.loadTermsDictionary();
        model.loadDocsDictionary();
        model.loadCitiesDictionary();
    }

    public StringBuilder getLines (){
        return model.getLines();
    }

    public Integer getTotalDocuments() {
        return model.getTotalDocuments();
    }

    public Integer getTotalTerms() {
        return model.getTotalTerms();
    }

    public Double getTotalTime() {
        return model.getTotalTime();
    }

    public HashSet<String> getLanguages() {
        return model.getLanguages();
    }

    public void setStemming(boolean selected) {
        model.setStemming(selected);
    }

    public void setCorpusPath(String corpusPath) {
        this.corpusPath = corpusPath;
    }

    public void setStopWordsPath(String stopWordsPath) {
        this.stopWordsPath = stopWordsPath;
    }

    public void setPostingPath(String postingPath) {
        this.postingPath = postingPath;
    }
}