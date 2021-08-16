package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.dionthorn.lifesimrpg.GameState;

import java.time.LocalDate;

public class CharacterCreationScreenController {

    @FXML public HBox topBar;
    @FXML public GridPane centerGridPane;
    @FXML public Label enterFirstNameLbl;
    @FXML public TextField firstNameInput;
    @FXML public Label enterLastNameLbl;
    @FXML public TextField lastNameInput;
    @FXML public Label enterBirthdayLbl;
    @FXML public Label dayLbl;
    @FXML public TextField dayInput;
    @FXML public Label monthLbl;
    @FXML public TextField monthInput;
    @FXML public Label yearLbl;
    @FXML public TextField yearInput;
    @FXML public VBox leftBar;
    @FXML public VBox rightBar;
    @FXML public Region vRegion2;
    @FXML public Button createNewPlayerBtn;
    @FXML public TextArea console;

    public void initialize() {
        Engine.CURRENT_SCREEN = Engine.SCREEN.CHARACTER_CREATION;
    }

    public void onCreatePlayer() {
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
            Engine.gameState = new GameState(firstName, lastName, birthday);
            System.out.println(Engine.gameState.getPlayer().getFirstName());

            // load fxml for PlayerInfoScreen.fxml
            try {
                Engine.loadFXML("PlayerInfoScreen.fxml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
