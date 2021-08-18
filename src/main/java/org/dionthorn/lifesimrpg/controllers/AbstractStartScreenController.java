package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.dionthorn.lifesimrpg.Engine;
import org.dionthorn.lifesimrpg.FileOpUtil;

/**
 * Used for both StartScreen and GameOverScreen with only the GameOverScreen having an override currently
 */
public abstract class AbstractStartScreenController extends AbstractScreenController {

    // FXML JavaFX Nodes common amongst StartScreen and GameOverScreen
    @FXML public Button startGameBtn;
    @FXML public Label chooseMapLbl;
    @FXML public ComboBox<String> selectMapBox;

    @FXML public void onStartGame() {
        FileOpUtil.initializeMapPaths(selectMapBox.getSelectionModel().getSelectedItem());
        // load fxml for characterCreation.fxml
        try {
            Engine.loadMapFXML("CharacterCreationScreen.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populateMapBox() {
        String[] mapNames = FileOpUtil.getFolderNamesFromDirectory(FileOpUtil.GAME_MAP_PATH);
        for(String mapName: mapNames) {
            if(mapName != null) {
                selectMapBox.getItems().add(mapName);
            }
        }
        selectMapBox.getSelectionModel().select(0);
    }

}
