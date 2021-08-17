package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.dionthorn.lifesimrpg.Engine;
import org.dionthorn.lifesimrpg.entities.AbstractCharacter;
import org.dionthorn.lifesimrpg.entities.AbstractEntity;
import org.dionthorn.lifesimrpg.entities.Job;

public class JobInfoScreenController extends AbstractGameScreenController {

    @FXML public ComboBox<String> jobOptions;
    @FXML public Label currentJobLbl;
    @FXML public Label currentDaysWorkedLbl;
    @FXML public Label getJobLbl;
    @FXML public Button applyBtn;
    @FXML public Region vRegion3;

    @Override
    public void initialize() {
        Engine.CURRENT_SCREEN = Engine.SCREEN.JOB_INFO;
        console.appendText(getDateString());
        updateAll();
    }

    @Override
    public void updateAll() {
        if(Engine.CURRENT_SCREEN != Engine.SCREEN.JOB_INFO) {
            console.appendText(getDateString());
        }
        // update variable texts
        playerInfoBtn.setText(String.format("%s Info", Engine.gameState.getPlayer().getFirstName()));

        // Show Current Job Info
        AbstractCharacter player = Engine.gameState.getPlayer();
        currentJobLbl.setText(
                """
                Current Job: %s Current Salary: %d
                Current Title: %s
                """.formatted(
                        player.getJob().getName(),
                        player.getJob().getDailyPayRate(),
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
        for(AbstractEntity e: AbstractEntity.entities) {
            if(e instanceof Job && ((Job)e).isFromFile()) {
                Job target = (Job) e;
                String targetInfo =
                        """
                        %s $%d
                        """.formatted(
                                target.getName(),
                                target.getDailyPayRate()
                        );
                if(!(jobOptions.getItems().contains(targetInfo))) {
                    jobOptions.getItems().add(targetInfo);
                }
            }
        }
        jobOptions.getSelectionModel().select(0);

        updateDateLbl();
        updateMoneyLbl();
    }

    public void onApply() {
        // Get the selection
        String selection = jobOptions.getSelectionModel().getSelectedItem();
        String jobName = selection.split("\\$")[0].replaceAll(" ", "");
        Job target;
        AbstractCharacter player = Engine.gameState.getPlayer();
        // loop through entities and find the target job
        for(AbstractEntity e: AbstractEntity.entities) {
            // make sure it is from file and not a default job
            if(e instanceof Job && ((Job)e).isFromFile()) {
                // make sure the name matches
                if(((Job)e).getName().equals(jobName)) {
                    target = (Job) e;
                    // check if player already has this job
                    if(player.hasJob() && player.getJob().isFromFile()) {
                        if(target.getName().equals(player.getJob().getName())) {
                            console.appendText(String.format("You already work at %s\n", player.getJob().getName()));
                            break;
                        }
                    }
                    // check if player meets job requirements
                    boolean canApply = false;
                    if((target.getTitleRequirements()[0].equals("") && target.getStatRQValues()[0] == 0)) {
                        canApply = true;
                    } else {
                        // check if we meet the titles in a loop, then in another loop check if we meet skill
                        String needTitle = target.getTitleRequirements()[0];
                        String needStatName = target.getStatRQNames()[0];
                        double needStatValue = target.getStatRQValues()[0];
                        if(player.hasStat(needStatName)) {
                            if(player.getStat(needStatName) >= needStatValue) {
                                if(player.getTitles().contains(needTitle)) {
                                    canApply = true;
                                }
                            }
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
                    } else {
                        console.appendText("You didn't meet job requirements to apply!");
                    }
                    break;
                }
            }
        }
        updateAll();
    }

    // Screen changers different for each screen

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

}
