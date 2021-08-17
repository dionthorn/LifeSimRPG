package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.dionthorn.lifesimrpg.Engine;
import org.dionthorn.lifesimrpg.GameState;
import org.dionthorn.lifesimrpg.entities.AbstractCharacter;

import java.util.Map;

public class PlayerInfoScreenController extends AbstractGameScreenController {

    @FXML public Label playerNameLbl;
    @FXML public Label currentLocationLbl;
    @FXML public Label currentHouseInfoLbl;
    @FXML public Label currentStatsLbl;
    @FXML public Label currentTitlesLbl;

    @Override
    public void initialize() {
        Engine.CURRENT_SCREEN = Engine.SCREEN.PLAYER_INFO;
        if(Engine.gameState.getCurrentDate().isEqual(GameState.DAY_ONE.minusDays(1))) {
            nextDay();
        } else {
            console.appendText(getDateString());
        }
        updateAll();
    }

    @Override
    public void updateAll() {
        if(Engine.CURRENT_SCREEN != Engine.SCREEN.PLAYER_INFO) {
            console.appendText(getDateString());
        }
        // update variable texts
        playerInfoBtn.setText(String.format("%s Info", Engine.gameState.getPlayer().getFirstName()));

        // update player name label
        AbstractCharacter player = Engine.gameState.getPlayer();
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

        // might want region here so dynamic labels are moved to bottom and look ordered

        // show player stats
        StringBuilder sb = new StringBuilder();
        sb.append("Stats:\n");
        if(player.getStats().size() > 0) {
            for(Map.Entry<String, Double> entry : player.getStats().entrySet()) {
                String statName = entry.getKey();
                Double statValue = entry.getValue();
                sb.append(String.format("%8s : %.2f\n", statName, statValue));
            }
        } else {
            sb.append("NONE");
        }
        currentStatsLbl.setText(String.valueOf(sb));

        // show player titles
        sb = new StringBuilder();
        sb.append("Titles:\n");
        if(player.getTitles().size() > 0) {
            for(String title: player.getTitles()) {
                sb.append(String.format("%s\n", title));
            }
        } else {
            sb.append("NONE");
        }
        currentTitlesLbl.setText(String.valueOf(sb));

        updateDateLbl();
        updateMoneyLbl();
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
