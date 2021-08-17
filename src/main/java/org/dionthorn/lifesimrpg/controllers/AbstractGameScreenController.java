package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.dionthorn.lifesimrpg.Engine;
import org.dionthorn.lifesimrpg.entities.Character;

/**
 * Used for PlayerInfo, MapInfo, and JobInfo as they all share these nodes
 */
public abstract class AbstractGameScreenController extends AbstractScreenController {

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
        // all AbstractGameScreenController must have a updateAll() override
        // that will handle the screen specific updates
    }

    // Console Clear
    @FXML protected void clearConsole() {
        console.clear();
        console.appendText(Engine.getDateString());
    }

    // Time Changers

    @FXML protected void onNextDay() {
        Engine.nextDay(console);
        update();
    }

    @FXML protected void onNextWeek() {
        Engine.nextWeek(console);
        update();
    }

    protected void update() {
        updateTitle(false);
        updateAll();
    }

    protected void updateTitle(boolean isButton) {
        Character player = Engine.gameState.getPlayer();
        if(player.hasCourse()) {
            int currentLevel = player.getCurrentCourse().getCourseLevel();
            String newTitle = player.getCurrentCourse().checkTitle(player);
            if(!(player.getTitles().contains(newTitle))) {
                player.getTitles().add(newTitle);
            }
            if(currentLevel < player.getCurrentCourse().getCourseLevel()) {
                console.appendText("You increased in title to: %s\n".formatted(newTitle));
            } else {
                if(isButton) {
                    console.appendText("You are not yet qualified for the next title\n");
                    console.appendText("You need %s stat to be %s\n".formatted(player.getCurrentCourse().getStatName(), player.getCurrentCourse().getCurrentTitleRequirement()));
                }
            }
        }
        player.getTitles().removeIf(title -> title.contains("Not Qualified"));
        updateAll();
    }

}
