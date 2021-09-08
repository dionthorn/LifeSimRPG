package org.dionthorn.lifesimrpg;

import org.dionthorn.lifesimrpg.entities.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import java.net.URI;
import java.time.LocalDate;
import java.net.URL;

/**
 * The Engine class will manage the GameState, FXMLLoader and which screen we are on,
 * it is essentially the interface between the controllers and the entities packages.
 * All variables and methods are public static except the rootStage.
 * Engine.loadFXML(String fileName) can be used to load a different screen from any controller object.
 */
public final class Engine {

    /**
     * SCREEN enum is used to flag which screen we should be currently on.
     * for example when you press the next day button,
     * and you are on MAP_INFO it will just update the screen
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

    private Engine() {
        // This is a static utility class that is not intended to be instantiated,
        // therefore we create a private constructor,
        // and declare the class as final, so it cannot be extended.
        // all calls should be of the form Engine.functionName()
    }

    // Constants
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    // Access these directly for convenience
    public static SCREEN currentScreen = SCREEN.BOOT;
    public static GameState gameState; // manage game object data

    // Private root Stage reference
    private static Stage rootStage;

    /**
     * Essentially our constructor we take it the Stage object from App.start() call
     * Then we add the global event handlers for ESC key and F7 key
     * Then we loadFXML for the StartScreen.fxml
     * @param stage Stage representing the root Stage object for the program to be statically referenced
     */
    public static void initialize(Stage stage) {
        // Set JRT flag and then set initial paths
        FileOpUtil.checkJRT();
        FileOpUtil.initializePaths();

        // setup rootStage initialize Settings.ini and Key Handlers
        rootStage = stage;
        initializeSettings();
        initializeKeyHandlers();

        // Load the StartScreen
        try{
            loadGameFXML("StartScreen.fxml");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // utility methods

    /**
     * Will create a fresh GameState using user provided data from the Character Creation screen.
     * @param firstName String representing the user provided first name
     * @param lastName String representing the user provided last name
     * @param birthday LocalDate created from user provided int values
     */
    public static void initializeGameState(String firstName, String lastName, LocalDate birthday) {
        gameState = new GameState(firstName, lastName, birthday);
    }

    /**
     * Will check the LifeSimRPG/Setting.ini file for SCREEN_WIDTH and SCREEN_HEIGHT values
     */
    private static void initializeSettings() {
        String[] settings;
        if(FileOpUtil.JRT) {
            settings = FileOpUtil.getFileLines(URI.create(FileOpUtil.jrtBaseURI + "Settings.ini"));
        } else {
            settings = FileOpUtil.getFileLines(URI.create(
                    String.valueOf(App.class.getClassLoader().getResource("Settings.ini"))
            ));
        }
        for(String line: settings) {
            if(line.contains("SCREEN_WIDTH")) {
                SCREEN_WIDTH = Integer.parseInt(line.split("=")[1]);
            } else if(line.contains("SCREEN_HEIGHT")) {
                SCREEN_HEIGHT = Integer.parseInt(line.split("=")[1]);
            }
        }
    }

    /**
     * Will assign Key Listeners to the rootStage
     *  ESC -> closes the program via Stage.close()
     *  F7 -> will dump entity data to System.out
     */
    public static void initializeKeyHandlers() {
        rootStage.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode() == KeyCode.ESCAPE) {
                rootStage.close();
            } else if(key.getCode() == KeyCode.F7) {
                dumpEntityData();
            }
        });
    }

    /**
     * Will find the game level FXML file if in JRT or not and use FXMLLoader.load(URL) to load the new Scene
     * We use the static rootStage object and setScene() on the result of the .load()
     * @param fileName String the target filename ex: "StartScreen.fxml"
     * @throws Exception the Exception representing either an IO or File error, shouldn't happen,
     * but we print stack trace in a try catch anytime we call this method just in case.
     */
    public static void loadGameFXML(String fileName) throws Exception {
        Parent root = FXMLLoader.load(new URL(FileOpUtil.GAME_FXML_PATH + fileName));
        Scene rootScene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        rootStage.setScene(rootScene);
    }

    /**
     * Will find map specific FXML files whether in JRT or not.
     * @param fileName String representing the target FXML file ex: Maps/{mapName}/FXML/CharacterCreationScreen.fxml
     * @throws Exception the Exception representing either an IO or File error, shouldn't happen,
     * but we print stack trace in a try catch anytime we call this method just in case.
     */
    public static void loadMapFXML(String fileName) throws Exception {
        Parent root = FXMLLoader.load(new URL(FileOpUtil.MAP_FXML_PATH + fileName));
        Scene rootScene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        rootStage.setScene(rootScene);
    }

    /**
     * This will dump AbstractEntity related data into System.out
     */
    public static void dumpEntityData() {
        // get quick dump of entity data
        for(AbstractEntity e : AbstractEntity.entities) {
            if (e instanceof Job temp) {
                System.out.print("Job UID:" + temp.getUID());
                System.out.print(" " + temp.getName());
                System.out.println(" " + temp.getDailyPayRate());
            } else if (e instanceof Map temp) {
                System.out.print("Map UID:" + temp.getUID());
                System.out.print(" " + temp.getName());
                System.out.println(" total places: " + temp.getPlaces().size());
            } else if (e instanceof Place temp) {
                System.out.print("Place UID:" + temp.getUID());
                System.out.print(" " + temp.getName());
                System.out.println(" total connections: " + temp.getConnections().size());
            } else if (e instanceof AbstractCharacter temp) {
                System.out.print("AbstractCharacter UID:" + temp.getUID());
                System.out.print(" " + temp.getFirstName() + " " + temp.getLastName());
                System.out.println(" is at: " + temp.getCurrentLocation().getName());
            }
        }
        // dump URI info
        System.out.println("### URI INFORMATION ###");
        System.out.println(FileOpUtil.NAME_PATH);
        System.out.println(FileOpUtil.GAME_MAP_PATH);
        System.out.println(FileOpUtil.GAME_FXML_PATH);
        System.out.println(FileOpUtil.START_SCREEN_MAP_NAME);
        System.out.println(FileOpUtil.MAP_PATH);
        System.out.println(FileOpUtil.MAP_COURSES_PATH);
        System.out.println(FileOpUtil.MAP_JOBS_PATH);
        System.out.println(FileOpUtil.MAP_FXML_PATH);
    }

}
