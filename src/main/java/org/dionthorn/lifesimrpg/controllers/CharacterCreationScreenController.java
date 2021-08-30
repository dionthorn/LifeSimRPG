package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.dionthorn.lifesimrpg.Engine;
import org.dionthorn.lifesimrpg.GameState;
import java.time.LocalDate;

/**
 * Will manage the character creation screen
 */
public class CharacterCreationScreenController extends AbstractScreenController {

    @FXML public Label enterFirstNameLbl;
    @FXML public Label enterLastNameLbl;
    @FXML public Label enterBirthdayLbl;
    @FXML public Label dayLbl;
    @FXML public Label monthLbl;
    @FXML public Label yearLbl;
    @FXML public Button createNewPlayerBtn;
    @FXML public TextField firstNameInput;
    @FXML public TextField lastNameInput;
    @FXML public TextField dayInput;
    @FXML public TextField monthInput;
    @FXML public TextField yearInput;

    /**
     * Override so after loading FXML variables we can set the currentScreen flag and console text
     */
    @Override
    public void initialize() {
        Engine.currentScreen = Engine.SCREEN.CHARACTER_CREATION;
        // Finally, direct user to Create Player button
        console.setText(
                """
                Please enter all required information
                Then press the "Create Player" button on the right bar
                """
        );
    }

    /**
     * FXML button will pull data from fields to create a new GameState
     */
    @FXML public void onCreatePlayer() {
        // pull data from text fields
        String firstName = firstNameInput.getText();
        String lastName = lastNameInput.getText();
        int day = Integer.parseInt(dayInput.getText());
        int month = Integer.parseInt(monthInput.getText());
        int year = Integer.parseInt(yearInput.getText());

        // try to make a LocalDate from the birthday
        LocalDate birthday = null;
        try {
            birthday = LocalDate.of(year, month, day);
        } catch(Exception e) {
            // suppress exception if invalid date
        }

        // if the birthday is invalid or after the age cap will warn in console
        if(birthday == null || birthday.isAfter(GameState.AGE_CAP)) {
            console.clear();
            console.appendText(
                    """
                     
                     Sorry that date is invalid try again!
                       
                     """
            );
        } else {
            // if birthday is valid then create a new GameState and display the main screen
            Engine.initializeGameState(firstName, lastName, birthday);

            // load fxml for PlayerInfoScreen.fxml
            try {
                Engine.loadMapFXML("PlayerInfoScreen.fxml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
