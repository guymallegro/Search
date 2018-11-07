package sample.Controller;

import sample.Model.Model;
import sample.Model.ReadFile;

import java.io.IOException;

public class Controller {
    Model model;
    private String path="/home/guy/Desktop/corpus/";

    public Controller() throws IOException {
        model = new Model();
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public void readFiles() {
        model.readFiles(path);

    }
}
