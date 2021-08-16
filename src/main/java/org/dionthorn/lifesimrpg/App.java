package org.dionthorn.lifesimrpg;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    public static String PROGRAM_VERSION = "v0.0.1";

    public static void main(String[] args) {
        // This will take you to start() method below and generates our Stage object
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Engine.setStage(stage); // This will begin the Engine
        stage.sizeToScene();
        stage.setResizable(false);
        stage.setTitle("LifeSimRPG " + PROGRAM_VERSION);
        stage.show();
    }

}