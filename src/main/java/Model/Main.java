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
    public static String citiesUrl = "https://restcountries.eu/rest/v2/all?fields=name;capital;population;currencies";

    @Override
    public void start(Stage primaryStage) throws Exception{
//        String ans = "1st";
//        String ans2 = "1,665";
//        String ans3 = "$10000000";
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
//        controller.setView(view);

//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(getClass().getResource("/fxml/sample.fxml"));
//        Scene otherScene = new Scene(root, 600, 480);
//        otherScene.getStylesheets().add(getClass().getResource("/sample.css").toExternalForm());
//        controller.readFiles();







//        Parent root = null;
//        try {
//            FXMLLoader myLoader = new FXMLLoader();
//            myLoader.setLocation(getClass().getResource("/fxml/sample.fxml"));
//            root = myLoader.load();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Scene scene = new Scene(root, 600, 480);
//        scene.getStylesheets().add(getClass().getResource("/sample.css").toExternalForm());
//        Stage stage = new Stage();
//        stage.setScene(scene);
//        stage.initModality(Modality.APPLICATION_MODAL);
//        stage.setTitle("Search");
//        stage.show();
//        controller = new Controller();
//        controller.readFiles();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
