package org.dionthorn.lifesimrpg;

import org.dionthorn.lifesimrpg.entities.*;
import org.dionthorn.lifesimrpg.entities.AbstractCharacter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.net.URI;
import java.net.URL;

/**
 * The Engine class will manage the GameState, FXMLLoader
 * And which screen we are on, it is essentially the interface between the
 * controllers and the entities packages. All variables and methods are public static
 * Engine.loadFXML(String fileName) can be used to load a different screen from any controller object
 */
public class Engine {

    /**
     * SCREEN enum is used to flag which screen we should be currently on
     * for example when you press next day, if you are on mapInfo it will just update mapInfo
     * instead of reloading it.
     */
    public enum SCREEN {
        BOOT,
        CHARACTER_CREATION,
        PLAYER_INFO,
        JOB_INFO,
        MAP_INFO,
        COURSES_INFO,
        DEAD
    }

    // Constants
    public static final int SCREEN_WIDTH = 1024;
    public static final int SCREEN_HEIGHT = 768;

    // Access these from any controller or entity
    public static SCREEN CURRENT_SCREEN = SCREEN.BOOT;
    public static Stage rootStage;
    public static FXMLLoader rootLoader;
    public static GameState gameState;

    /**
     * Essentially our constructor we take it the Stage object from App.start() call
     * Then we add the global event handlers for ESC key and F7 key
     * Then we loadFXML for the StartScreen.fxml
     * @param stage Stage representing the root Stage object for the program to be statically referenced
     */
    public static void setStage(Stage stage) {
        // setup rootStage and Key Handlers
        rootStage = stage;
        rootStage.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode() == KeyCode.ESCAPE) {
                rootStage.close();
            } else if(key.getCode() == KeyCode.F7) {
                dumpEntityData();
            }
        });

        // Load the StartScreen
        try{
            loadFXML("StartScreen.fxml");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // utility methods

    /**
     * Will find the FXML file if in JRT or not and use FXMLLoader.load(URL) to load the new Scene
     * We use the static rootStage object and setScene() on the result of the .load()
     * @param fileName String the target filename ex: "StartScreen.fxml"
     * @throws Exception Exception representing either an IO or File error, shouldn't happen,
     * but we print stack trace in a try catch anytime we call this method just in case.
     */
    public static void loadFXML(String fileName) throws Exception {
        FileOpUtil.checkJRT();
        URL test;
        if(FileOpUtil.JRT) {
            test = URI.create(FileOpUtil.jrtBaseURI + "FXML/" + fileName).toURL();
        } else {
            test = Objects.requireNonNull(App.class.getResource("/FXML/" + fileName)).toURI().toURL();
        }
        Parent root = FXMLLoader.load(test);
        Scene rootScene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        rootStage.setScene(rootScene);
    }

    /**
     * This will dump AbstractEntity related data into System.out
     */
    public static void dumpEntityData() {
        ArrayList<AbstractEntity> entities = AbstractEntity.entities; // get quick dump of entity data
        for(AbstractEntity e : entities) {
            if (e instanceof Job) {
                Job temp = ((Job) e);
                System.out.print("Job UID:" + temp.getUID());
                System.out.print(" " + temp.getName());
                System.out.println(" " + temp.getDailyPayRate());
            } else if (e instanceof Map) {
                Map temp = ((Map) e);
                System.out.print("Map UID:" + temp.getUID());
                System.out.print(" " + temp.getName());
                System.out.println(" total places: " + temp.getPlaces().size());
            } else if (e instanceof Place) {
                Place temp = ((Place) e);
                System.out.print("Place UID:" + temp.getUID());
                System.out.print(" " + temp.getName());
                System.out.println(" total connections: " + temp.getConnections().size());
            } else if (e instanceof AbstractCharacter) {
                AbstractCharacter temp = ((AbstractCharacter) e);
                System.out.print("AbstractCharacter UID:" + temp.getUID());
                System.out.print(" " + temp.getFirstName() + " " + temp.getLastName());
                System.out.println(" is at: " + temp.getCurrentLocation().getName());
            }
        }
    }

}
