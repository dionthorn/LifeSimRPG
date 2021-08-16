package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.dionthorn.lifesimrpg.entities.Character;
import org.dionthorn.lifesimrpg.entities.Entity;
import org.dionthorn.lifesimrpg.entities.Job;

public class JobInfoScreenController {

    @FXML public HBox topBar;
    @FXML public GridPane centerGridPane;
    @FXML public VBox leftBar;
    @FXML public VBox rightBar;
    @FXML public TextArea console;
    @FXML public Label moneyLbl;
    @FXML public Region hRegion;
    @FXML public Label currentDateLbl;
    @FXML public Label currentJobLbl;
    @FXML public Label currentDaysWorkedLbl;
    @FXML public Region vRegion;
    @FXML public Label getJobLbl;
    @FXML public ComboBox<String> jobOptions;
    @FXML public Button applyBtn;
    @FXML public Button playerInfoBtn;
    @FXML public Button jobInfoBtn;
    @FXML public Button mapInfoBtn;
    @FXML public Region vRegion3;
    @FXML public Button clearConsoleBtn;
    @FXML public Region vRegion2;
    @FXML public Button nextWeekBtn;
    @FXML public Button nextDayBtn;

    public void initialize() {
        Engine.CURRENT_SCREEN = Engine.SCREEN.JOB_INFO;
        updateAll();
    }

    public void updateAll() {
        // update variable texts
        playerInfoBtn.setText(String.format("%s Info", Engine.gameState.getPlayer().getFirstName()));

        // Show Current Job Info
        Character player = Engine.gameState.getPlayer();
        currentJobLbl.setText(
                """
                Current Job: %s Current Salary: %d
                Current Title: %s
                """.formatted(
                        player.getJob().getName(),
                        player.getJob().getSalary(),
                        player.getJob().getCurrentTitle()
                )
        );

        // current job info part 2
        currentDaysWorkedLbl.setText(
                """
                Days Worked This Year: %d
                Years Worked: %d
                """.formatted(
                        player.getJob().getDaysWorked(),
                        player.getJob().getYearsWorked()
                )
        );

        // Show Job Options
        for(Entity e: Entity.entities) {
            if(e instanceof Job && ((Job)e).isFromFile()) {
                Job target = (Job) e;
                String targetInfo =
                        """
                        %s $%d
                        """.formatted(
                                target.getName(),
                                target.getSalary()
                        );
                if(!(jobOptions.getItems().contains(targetInfo))) {
                    jobOptions.getItems().add(targetInfo);
                }
            }
        }
        jobOptions.getSelectionModel().select(0);
        Engine.updateDateLbl(currentDateLbl);
        Engine.updateMoneyLbl(moneyLbl);
    }

    public void onApply() {
        // Get the selection
        String selection = jobOptions.getSelectionModel().getSelectedItem();
        String jobName = selection.split("\\$")[0].replaceAll(" ", "");
        Job target;
        Character player = Engine.gameState.getPlayer();
        // loop through entities and find the target job
        for(Entity e: Entity.entities) {
            // make sure it is from file and not a default job
            if(e instanceof Job && ((Job)e).isFromFile()) {
                // make sure the name matches
                if(((Job)e).getName().equals(jobName)) {
                    target = (Job) e;
                    // check if player meets job requirements
                    boolean canApply = false;
                    if((target.getTitleRequirements() == null && target.getStatRequirements().size() == 0)) {
                        canApply = true;
                    } else {
                        // prep boolean trackers
                        boolean[] hasTitleRQs = new boolean[target.getTitleRequirements().length];
                        boolean hasTitleRQ = true;
                        boolean[] hasStatRQs = new boolean[target.getStatRequirements().size()];
                        boolean hasStatRQ = true;

                        // process title requirements
                        for(int t=0; t<target.getTitleRequirements().length; t++) {
                            for(int p=0; p<player.getTitles().size(); p++) {
                                if(target.getTitleRequirements()[t].equals(player.getTitles().get(p))) {
                                    hasTitleRQs[t] = true;
                                    break;
                                }
                            }
                        }
                        for(boolean b: hasTitleRQs)  {
                            if(!b) {
                                hasTitleRQ = false;
                                break;
                            }
                        }

                        // process stat requirements
                        String[] targetKeys = target.getStatRequirements().keySet().toArray(new String[0]);
                        String[] playerKeys = player.getStats().keySet().toArray(new String[0]);
                        for(int s=0; s<target.getStatRequirements().size(); s++) {
                            for(int p=0; p<player.getStats().size(); p++) {
                                if(targetKeys[s].equals(playerKeys[p])) {
                                    if(target.getStatRequirements().get(playerKeys[p]).equals(player.getStats().get(playerKeys[p]))) {
                                        hasStatRQs[s] = true;
                                        break;
                                    }
                                }
                            }
                        }
                        for(boolean b: hasStatRQs) {
                            if(!b) {
                                hasStatRQ = false;
                                break;
                            }
                        }

                        // if they meet requirements then set canApply true
                        if(hasStatRQ && hasTitleRQ) {
                            canApply = true;
                        }
                    }

                    // process application
                    if(canApply) {
                        player.setJob(target, Engine.gameState.getCurrentDate());
                        console.appendText(
                                """
                                %s started working at %s as a %s
                                """.formatted(
                                        player.getFirstName(),
                                        player.getJob().getName(),
                                        player.getJob().getCurrentTitle()
                                )
                        );
                        updateAll();
                    } else {
                        console.appendText("You didn't meet job requirements to apply!");
                    }
                    break;
                }
            }
        }
    }

    public void onPlayerInfo() {
        // load fxml for PlayerInfoScreen.fxml
        try {
            Engine.loadFXML("PlayerInfoScreen.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onJobInfo() {
        updateAll();
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

    public void onNextWeek() {
        updateAll();
        Engine.nextWeek(console);
    }

    public void onNextDay() {
        updateAll();
        Engine.nextDay(console);
    }
}
