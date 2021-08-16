package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.dionthorn.lifesimrpg.Engine;

/**
 * Used for PlayerInfo, MapInfo, and JobInfo as they all share these nodes
 */
public abstract class GameScreenController extends ScreenController {

    @FXML public Button playerInfoBtn;
    @FXML public Button jobInfoBtn;
    @FXML public Button mapInfoBtn;
    @FXML public Button nextWeekBtn;
    @FXML public Button nextDayBtn;
    @FXML public Button clearConsoleBtn;
    @FXML public Label moneyLbl;
    @FXML public Label currentDateLbl;
    @FXML public Region hRegion;
    @FXML public Region vRegion2;

    protected void updateAll() {
        // all GameScreenController must have a updateAll() override
        // that will handle the screen specific updates
    }

    // Console Clear
    @FXML protected void clearConsole() {
        console.clear();
    }

    // Time Changers

    @FXML protected void onNextDay() {
        Engine.nextDay(console);
        console.appendText(Engine.getDateString());
        updateAll();
    }

    @FXML protected void onNextWeek() {
        Engine.nextWeek(console);
        console.appendText(Engine.getDateString());
        updateAll();
    }

}
