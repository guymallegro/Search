package sample.Controller;

import sample.Model.Model;
import sample.Model.ReadFile;

import java.io.IOException;

import static sample.Model.ReadFile.readFile;

public class Controller {
    Model model;
    private String path="C:\\Users\\ספיר רצון\\Desktop\\corpus\\";

    public Controller() throws IOException {
        model = new Model();
        ReadFile.model=model;
        model.readFiles(path);
        ReadFile.readFile(path);
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
