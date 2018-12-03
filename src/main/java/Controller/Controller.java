package Controller;

import Model.Model;
import View.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Controller {
    private Model model;
    private View view;
    private String corpusPath; // @TODO Needs to be set by UI
    //private String filesPath="./src/main/resources/test/"; // @TODO Needs to be set by UI
    private String stopWordsPath; // @TODO Needs to be set by UI
    private String postingPath; // @TODO Needs to be set by UI


    public Controller() {
        model = new Model();
    }

    public void setView (View view) {this.view = view; }

    public void setCorpusPath(String corpusPath) { this.corpusPath = corpusPath; }

    public void setStopWordsPath(String stopWordsPath) { this.stopWordsPath = stopWordsPath; }

    public void setPostingPath(String postingPath) { this.postingPath = postingPath; }

    public void readFiles() { model.readFiles(corpusPath, stopWordsPath, postingPath); }

    public void setStemming(boolean selected) { model.setStemming(selected); }

    public static HashMap <String, ArrayList<Object>> getDictionary () {return Model.getTermsDictionary();}

    public HashSet<String> getLanguages () { return model.getLanguages();}
}