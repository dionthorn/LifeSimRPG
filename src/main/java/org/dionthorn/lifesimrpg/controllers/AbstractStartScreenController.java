package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.dionthorn.lifesimrpg.Engine;

/**
 * Used for both StartScreen and GameOverScreen with only the GameOverScreen having an override currently
 */
public abstract class AbstractStartScreenController extends AbstractScreenController {

    // FXML JavaFX Nodes common amongst StartScreen and GameOverScreen
    @FXML public Button startGameBtn;

    /**
     *
     */
    @FXML public void onStartGame() {
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
