package Model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Controller.Controller;
import View.View;

/**
 * The main class
 */
public class Main extends Application {
    static String citiesUrl = "https://restcountries.eu/rest/v2/all?fields=name;capital;population;currencies"; // URL to the cities API

    /**
     * The start function of the JAVAFX
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root;
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
