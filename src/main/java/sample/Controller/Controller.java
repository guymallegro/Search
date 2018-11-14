package sample.Controller;

import sample.Model.Model;

public class Controller {
    private Model model;
    private String filesPath="C:\\Users\\ספיר רצון\\Desktop\\test\\"; // @TODO Needs to be set by UI
    private String stopWordsPath=".\\src\\main\\resources\\stop_words"; // @TODO Needs to be set by UI

    public Controller(){
        model = new Model();
    }

    public void readFiles() {
        model.readFiles(filesPath,stopWordsPath);
    }
}
