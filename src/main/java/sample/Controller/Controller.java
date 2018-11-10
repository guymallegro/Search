package sample.Controller;

import sample.Model.Model;
import sample.Model.ReadFile;

import java.io.IOException;

public class Controller {
    private Model model;
    private String filesPath="C:\\Users\\Guy Shuster\\Desktop\\temp\\corpus\\"; // @TODO Needs to be set by UI
    private String stopWordsPath=".\\src\\main\\resources\\stop_words"; // @TODO Needs to be set by UI

    public Controller(){
        model = new Model();
    }

    public void readFiles() {
        model.readFiles(filesPath,stopWordsPath);

    }
}
