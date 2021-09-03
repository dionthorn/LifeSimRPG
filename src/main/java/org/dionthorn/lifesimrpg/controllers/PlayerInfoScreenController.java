package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.dionthorn.lifesimrpg.Engine;
import org.dionthorn.lifesimrpg.GameState;
import org.dionthorn.lifesimrpg.entities.AbstractCharacter;
import org.dionthorn.lifesimrpg.entities.PlayerCharacter;
import java.util.Map;

/**
 * Will manage the player info screen
 */
public class PlayerInfoScreenController extends AbstractGameScreenController {

    // FXML variables
    @FXML public Label playerNameLbl;
    @FXML public Label currentLocationLbl;
    @FXML public Label currentHouseInfoLbl;
    @FXML public Label currentStatsLbl;
    @FXML public Label currentTitlesLbl;
    @FXML public Label playerAttributesLbl;

    /**
     * Override will allow us to set the screen flag,
     * advance a day if on the very start of game,
     * append text to console
     * and do screen specific updateAll()
     */
    @Override
    public void initialize() {
        Engine.currentScreen = Engine.SCREEN.PLAYER_INFO;
        if(Engine.gameState.getCurrentDate().isEqual(GameState.DAY_ONE.minusDays(1))) {
            nextDay();
        } else {
            console.appendText(getDateString());
        }
        updateAll();
    }

    /**
     * Override allows us to do player info screen specific updates
     */
    @Override
    public void updateAll() {
        if(Engine.currentScreen != Engine.SCREEN.PLAYER_INFO) {
            console.appendText(getDateString());
        }

        // get player reference
        PlayerCharacter player = Engine.gameState.getPlayer();

        // update variable texts
        playerInfoBtn.setText(String.format("%s Info", player.getFirstName()));

        // update player name label
        playerNameLbl.setText(
                """
                Player: %s Health: %.2f/%.2f
                """.formatted(
                        player.getName(),
                        player.getHealth(),
                        AbstractCharacter.MAX_ATTRIBUTE
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
                        player.getHome().getRentPerDay(),
                        player.getFoodCostPerDay(),
                        player.getHome().getMonthsUnpaid(),
                        player.getHome().getTotalUnpaid()
                )
        );

        // update player attribute label
        playerAttributesLbl.setText(
                """
                Strength:     %.2f
                Constitution: %.2f
                Dexterity:    %.2f
                Intelligence: %.2f
                Wisdom:       %.2f
                Charisma:     %.2f
                """.formatted(
                        player.getStrength(),
                        player.getConstitution(),
                        player.getDexterity(),
                        player.getIntelligence(),
                        player.getWisdom(),
                        player.getWisdom()
                )
        );

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

    /**
     * Override FXML Button then we can update instead of Engine.loadMapFXML("PlayerInfoScreen.fxml");
     */
    @Override
    @FXML public void onPlayerInfo() {
        updateAll();
    }

}
