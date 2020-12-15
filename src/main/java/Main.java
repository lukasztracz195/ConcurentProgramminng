import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Simulation;
import model.Simulator;
import model.algorithms.AlgorithmWage;
import model.algorithms.SetWageAlgorithm;
import model.config.ConfigGenerator;
import model.observer.Observer;
import model.threads.disc.Disc;
import model.threads.generator.ClientsGenerator;
import model.threads.generator.ReceiverInformationAboutActivation;
import model.threads.loadbalancer.LoadBalancer;
import model.threads.warehouse.ClientsReceiver;
import model.threads.warehouse.Warehouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Rectangle2D rectangleScreen = Screen.getPrimary().getVisualBounds();
        Parent root = FXMLLoader.load(getClass().getResource("/fxmls/sample.fxml"));
        Scene scene = new Scene(root, rectangleScreen.getWidth() / 2, rectangleScreen.getHeight() / 2);
        System.out.println(rectangleScreen.getWidth() / 2 + " " + rectangleScreen.getHeight() / 2);
        scene.getStylesheets().add(getClass().getResource("/css/stylesheet.css").toExternalForm());
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) throws InterruptedException {

        ConfigGenerator configGenerator =  ConfigGenerator.builder()
        .probabilityRandomHeavyFile(0.1)
                .probabilityRandomMediumFile(0.5)
                .probabilityRandomLightFile(0.4)
                .thresholdBetweenLightAndMediumFile(25)
                .thresholdBetweenMediumAndHeavyFile(75)
                .maxSizeOfFile(100)
                .howManyFilesCanBeHaveClient(10)
                .howManyClientsCanGenerate(10)
                .timeDuration(1)
                .timeUnitForDuration(TimeUnit.MINUTES)
                .timePauseDuration(5)
                .timeUnitForPause(TimeUnit.SECONDS)
                .build();
        SetWageAlgorithm algorithm = new AlgorithmWage();
        Simulation simulation = new Simulator(configGenerator,5,algorithm);
        simulation.run();

        //launch(args);

    }
}
