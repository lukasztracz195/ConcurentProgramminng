import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Rectangle2D rectangleScreen = Screen.getPrimary().getVisualBounds();
        Parent root = FXMLLoader.load(getClass().getResource("/fxmls/sample.fxml"));
        Scene scene = new Scene(root, rectangleScreen.getWidth()/2,rectangleScreen.getHeight()/2);
        System.out.println(rectangleScreen.getWidth()/2+" "+rectangleScreen.getHeight()/2);
        scene.getStylesheets().add(getClass().getResource("/css/stylesheet.css").toExternalForm());
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
