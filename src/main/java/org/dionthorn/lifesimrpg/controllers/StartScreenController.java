package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class StartScreenController {

    // FXML JavaFX Nodes
    @FXML public HBox topBar; // top
    @FXML public VBox leftBar; // left
    @FXML public VBox rightBar; // right
    @FXML public TextArea console; // bottom
    @FXML public GridPane centerGridPane; // center
    @FXML public Region vRegion;
    @FXML public Button startGameBtn;

    public void initialize() {
        // don't need to initialize just yet, maybe load a splash screen image into centerGridPane?
    }

    public void onStartGame() {
        // load fxml for characterCreation.fxml
        try {
            Engine.loadFXML("CharacterCreationScreen.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Finally, direct user to Create Player button
        console.setText(
                """
                Please enter all required information
                Then press the "Create Player" button on the right bar
                """
        );
    }

}
