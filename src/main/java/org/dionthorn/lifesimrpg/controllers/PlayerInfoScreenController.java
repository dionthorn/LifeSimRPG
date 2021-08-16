package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.dionthorn.lifesimrpg.Engine;
import org.dionthorn.lifesimrpg.entities.Character;

public class PlayerInfoScreenController extends GameScreenController {

    @FXML public Label playerNameLbl;
    @FXML public Label currentLocationLbl;
    @FXML public Label currentHouseInfoLbl;

    @Override
    public void initialize() {
        Engine.CURRENT_SCREEN = Engine.SCREEN.PLAYER_INFO;
        updateAll();
    }

    @Override
    public void updateAll() {
        // update variable texts
        playerInfoBtn.setText(String.format("%s Info", Engine.gameState.getPlayer().getFirstName()));

        // update player name label
        Character player = Engine.gameState.getPlayer();
        playerNameLbl.setText(
                """
                Player: %s %s Health: %.2f/%.2f
                """.formatted(
                        player.getFirstName(),
                        player.getLastName(),
                        player.getHealth(),
                        player.getMaxHealth()
                )
        );

        // update current connection label
        currentLocationLbl.setText(String.format(
                "Current Location: %s",
                player.getCurrentLocation().getName()
        ));

        // update current house info label
        currentHouseInfoLbl.setText(
                """
                Rent $/day: $%d
                Food $/day: $%d
                Months Rent Unpaid: %d
                Total Rent Unpaid: $%d
                """.formatted(
                        player.getHome().getRent(),
                        player.getFoodCost(),
                        player.getHome().getMonthsUnpaid(),
                        player.getHome().getTotalUnpaid()
                )
        );
        Engine.updateDateLbl(currentDateLbl);
        Engine.updateMoneyLbl(moneyLbl);
    }

    // Screen changers different for each screen

    public void onPlayerInfo() {
        updateAll();
    }

    public void onJobInfo() {
        // load fxml for JobInfoScreen.fxml
        try {
            Engine.loadFXML("JobInfoScreen.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onMapInfo() {
        // load fxml for MapInfoScreen.fxml
        try {
            Engine.loadFXML("MapInfoScreen.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
