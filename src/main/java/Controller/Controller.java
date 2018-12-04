package Controller;

import Model.Model;
import View.View;

import java.util.HashSet;

public class Controller {
    private Model model;
    private View view;
    private String corpusPath;
    private String stopWordsPath;
    private String postingPath;


    public Controller() {
        model = new Model();
    }

    public void resetDictionaries() { model.resetDictionaries(); }

    public void readFiles() { model.readFiles(corpusPath, stopWordsPath, postingPath); }

    public HashSet<String> getLanguages () { return model.getLanguages();}

    public void setView (View view) {this.view = view; }

    public void setStemming(boolean selected) { model.setStemming(selected); }

    public void setCorpusPath(String corpusPath) { this.corpusPath = corpusPath; }

    public void setStopWordsPath(String stopWordsPath) { this.stopWordsPath = stopWordsPath; }

    public void setPostingPath(String postingPath) { this.postingPath = postingPath; }

    public Integer getTotalDocuments() {
        return model.getTotalDocuments();
    }
    public Integer getTotalTerms() {
        return model.getTotalTerms();
    }
    public Double getTotalTime() {
        return model.getTotalTime();
    }
}