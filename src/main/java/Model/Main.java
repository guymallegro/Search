package Model;

import Model.Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Controller.Controller;
import View.View;

public class Main extends Application {
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
//        StringBuilder a = new StringBuilder("abc.def");
//        int index = a.indexOf("e");
        //        String ans = "1st";
//        String ans2 = "1,665";
//        String ans3 = "$1000-0000";
//        ans2 = ans2.toLowerCase();
//        ans2 = ans2.toUpperCase();
//        ans3 = ans3.toLowerCase();
//        ans3 = ans3.toUpperCase();

//        boolean a = ans.contains("[a-z]");
//        boolean ab = ans2.matches("[0-9]+"+"[.]?"+"[0-9]+");
//        boolean abb = ans3.matches("[$][0-9]+"+"[,]?"+"[0-9]+");

        Parent root = null;
        FXMLLoader myLoader = new FXMLLoader();
        myLoader.setLocation(getClass().getResource("/fxml/sample.fxml"));
        root = myLoader.load();
        Scene scene = new Scene(root, 600, 480);
        scene.getStylesheets().add(getClass().getResource("/sample.css").toExternalForm());
        primaryStage.setTitle("SearchMe");
        primaryStage.setScene(scene);
        primaryStage.show();
        Controller controller = new Controller();
        View view = myLoader.getController();
        view.setController(controller);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
