import controllers.ControllerOfSimulation;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxmls/sample.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 1383, 934);
        scene.getStylesheets().add(getClass().getResource("/css/stylesheet.css").toExternalForm());
        primaryStage.setTitle("LoadBalancer in DATA CENTER Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
        ControllerOfSimulation controller = fxmlLoader.getController();
        primaryStage.setOnCloseRequest(e -> {
            controller.stopSimulation();
            Platform.exit();
            System.exit(0);
        });
    }


    public static void main(String[] args) {
        launch(args);

    }
}
