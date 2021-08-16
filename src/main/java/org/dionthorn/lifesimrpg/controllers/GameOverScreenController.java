package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class GameOverScreenController {

    @FXML public HBox topBar;
    @FXML public GridPane centerGridPane;
    @FXML public VBox leftBar;
    @FXML public VBox rightBar;
    @FXML public Region vRegion2;
    @FXML public Button startGameBtn;
    @FXML public TextArea console;

    public void initialize() {
        Engine.CURRENT_SCREEN = Engine.SCREEN.DEAD;
        if(Engine.gameState.getPlayer().getHealth() == 0) {
            console.appendText(String.format(
                    "You died of starvation after %s days!\n",
                    Engine.gameState.getPlayer().getDaysWithoutFood()
            ));
        }
    }

    public void onStartGame() {
        // Set the screen flag
        Engine.CURRENT_SCREEN = Engine.SCREEN.CHARACTER_CREATION;

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
