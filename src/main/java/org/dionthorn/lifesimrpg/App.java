package org.dionthorn.lifesimrpg;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The program entry point extends the JavaFX Application class
 * Generates our initial Stage object and initiates the Engine
 */
public final class App extends Application {

    // public constant for the program version
    public static final String PROGRAM_VERSION = "v0.0.1";

    /**
     * Main Java VM entry point
     * @param args command line arguments, aren't used currently
     */
    public static void main(String[] args) {
        // This will take you to start() method below and generates our Stage object
        launch();
    }

    /**
     * Override so we can initialize the Engine and show the Stage
     * @param stage Stage object representing the 'window' of the application
     * @throws Exception will throw any and all exceptions during the initialization process
     */
    @Override
    public void start(Stage stage) throws Exception {
        Engine.initialize(stage); // This will set up the Engine
        stage.sizeToScene();
        stage.setResizable(false);
        stage.setTitle("LifeSimRPG " + PROGRAM_VERSION);
        stage.show(); // we show the stage after all Engine.initialize() are done
    }

}