package sample.Model;

import com.sun.deploy.util.StringUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.Controller.Controller;

import java.text.DecimalFormat;

public class Main extends Application {
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
//        String ans = "1st";
//        String ans2 = "1,665";
//        String ans3 = "$10000000";
//        boolean a = ans.contains("[a-z]");
//        boolean ab = ans2.matches("[0-9]+"+"[.]?"+"[0-9]+");
//        boolean abb = ans3.matches("[$][0-9]+"+"[,]?"+"[0-9]+");

        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        controller = new Controller();
        controller.readFiles();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
