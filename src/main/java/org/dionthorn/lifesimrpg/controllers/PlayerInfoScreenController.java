package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.dionthorn.lifesimrpg.entities.Character;

public class PlayerInfoScreenController {

    @FXML public HBox topBar;
    @FXML public Label moneyLbl;
    @FXML public Region hRegion;
    @FXML public Label currentDateLbl;
    @FXML public GridPane centerGridPane;
    @FXML public Region vRegion;
    @FXML public VBox leftBar;
    @FXML public Region vRegion3;
    @FXML public VBox rightBar;
    @FXML public Region vRegion2;
    @FXML public TextArea console;
    @FXML public Button nextWeekBtn;
    @FXML public Button nextDayBtn;
    @FXML public Label playerNameLbl;
    @FXML public Label currentLocationLbl;
    @FXML public Label currentHouseInfoLbl;
    @FXML public Button playerInfoBtn;
    @FXML public Button jobInfoBtn;
    @FXML public Button mapInfoBtn;
    @FXML public Button clearConsoleBtn;

    public void initialize() {
        Engine.CURRENT_SCREEN = Engine.SCREEN.PLAYER_INFO;
        updateAll();
    }

    public void onNextDay() {
        Engine.nextDay(console);
        updateAll();
    }

    public void onNextWeek() {
        Engine.nextWeek(console);
        updateAll();
    }

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

    public void clearConsole() {
        console.clear();
    }
}
