package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.dionthorn.lifesimrpg.Engine;

import java.time.LocalDate;

/**
 * The base AbstractScreenController, all ScreenControllers use these nodes
 */
public abstract class AbstractScreenController {

    // FXML JavaFX Nodes Common amongst all subclasses
    @FXML public HBox topBar; // top
    @FXML public VBox leftBar; // left
    @FXML public VBox rightBar; // right
    @FXML public TextArea console; // bottom
    @FXML public GridPane centerGridPane; // center
    @FXML public Region vRegion;

    protected void initialize() {
        // All ScreenControllers must have an initialize method for the FXML
        // Can override in order to do things on creation of the screen
    }

    /**
     * Will return a String representing the current date formatted for human readability
     * ex: Monday: January 1, 1972
     * @return String representing the current date formatted for human readability
     */
    protected static String getDateString() {
        LocalDate currentDate = Engine.gameState.getCurrentDate();
        return """
                %s: %s %d, %d
                """.formatted(
                currentDate.getDayOfWeek().name(),
                currentDate.getMonth().name(),
                currentDate.getDayOfMonth(),
                currentDate.getYear()
        );
    }

}
