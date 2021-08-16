package org.dionthorn.lifesimrpg;

import javafx.application.Application;
import javafx.stage.Stage;
import org.dionthorn.lifesimrpg.controllers.Engine;

public class App extends Application {

    public static void main(String[] args) {
        // This will take you to start() method below
        launch(App.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Engine.setStage(stage); // This will begin the Controller
        stage.sizeToScene();
        stage.setResizable(false);
        stage.setTitle("LifeSimRPG");
        stage.show();
    }

}