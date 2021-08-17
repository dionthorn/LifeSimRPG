package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.dionthorn.lifesimrpg.Engine;
import org.dionthorn.lifesimrpg.entities.Character;
import org.dionthorn.lifesimrpg.entities.Place;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MapInfoScreenController extends AbstractGameScreenController {

    private static final ArrayList<Label> connectingPlaceLbls = new ArrayList<>();
    private static final ArrayList<Button> connectingPlaceBtns = new ArrayList<>();
    private static final ArrayList<Label> charLbls = new ArrayList<>();
    private static final ArrayList<Button> talkBtns = new ArrayList<>();

    @FXML public Label currentLocationLbl;
    @FXML public Label currentPeopleLbl;
    @FXML public Button coursesInfoBtn;

    @Override
    public void initialize() {
        Engine.CURRENT_SCREEN = Engine.SCREEN.MAP_INFO;
        console.appendText(Engine.getDateString());
        updateAll();
    }

    // essentially a refresh method
    @Override
    public void updateAll() {
        if(Engine.CURRENT_SCREEN != Engine.SCREEN.MAP_INFO) {
            console.appendText(Engine.getDateString());
        }
        if(Engine.CURRENT_SCREEN == Engine.SCREEN.MAP_INFO) {
            centerGridPane.getChildren().clear();
        }
        // update variable texts
        playerInfoBtn.setText(String.format("%s Info", Engine.gameState.getPlayer().getFirstName()));

        // Move all characters around if user press the mapInfoBtn
        Character player = Engine.gameState.getPlayer();
        for(Character c: Engine.gameState.getCurrentMap().getAllCharacters()) {
            if(!(c == player)) {
                c.moveRandom();
            }
        }

        // setup current location label
        currentLocationLbl.setText(
                """
                Current Location: %s
                Connecting Locations:
                """.formatted(
                        player.getCurrentLocation().getName()
                )
        );
        centerGridPane.getChildren().add(currentLocationLbl);
        GridPane.setConstraints(currentLocationLbl, 0,0, 4, 1);

        // generate the Label and Button for each connecting location
        int lastYindex = generatePlaceBtnsAndLbls(player);

        // generate the Label and Button for each character in the current location
        generateTalkBtnsAndLbls(player, lastYindex);

        // check if we currently are in a SCHOOL type Place and add the courses button to rightBar
        coursesInfoBtn.setVisible(Engine.gameState.getPlayer().getCurrentLocation().getType() == Place.PLACE_TYPE.SCHOOL);

        Engine.updateDateLbl(currentDateLbl);
        Engine.updateMoneyLbl(moneyLbl);
    }

    public int generatePlaceBtnsAndLbls(Character player) {
        // clear arrays
        connectingPlaceLbls.clear(); // maybe instead we setup these with default buttons/labels on first pass
        connectingPlaceBtns.clear(); // and do .setText() calls instead of generating new in the below loop?

        // some tracking variables
        int lastYindex = 0;
        int xCap = 0;
        int yCap = 1;

        // loop current locations connections and generate labels and buttons for travel
        for(int i=0; i<player.getCurrentLocation().getConnections().size(); i++) {
            // target the location and make the buttons
            Place targetPlace = player.getCurrentLocation().getConnections().get(i);
            connectingPlaceLbls.add(new Label(targetPlace.getName()));
            connectingPlaceBtns.add(new Button("Go To"));
            connectingPlaceBtns.get(i).setOnAction(ActionEvent -> {
                Engine.gameState.getPlayer().moveTo(targetPlace);
                updateAll();
            });

            // setup grid pane
            GridPane.setConstraints(connectingPlaceLbls.get(i), xCap, yCap); // 0,1
            GridPane.setConstraints(connectingPlaceBtns.get(i), xCap + 1, yCap); // 1,1
            centerGridPane.getChildren().addAll(connectingPlaceLbls.get(i), connectingPlaceBtns.get(i));

            // tracking
            lastYindex = yCap;
            xCap += 2;
            if(xCap == 6) {
                xCap = 0;
                yCap ++;
            }

        }

        // setup character label and buttons
        GridPane.setConstraints(currentPeopleLbl, 0, lastYindex + 1);
        centerGridPane.getChildren().addAll(currentPeopleLbl);

        // we return the last Y index used for placing nodes
        // on the GridPane used by the generateTalkBtnsAndLbls method
        return lastYindex;
    }

    public void generateTalkBtnsAndLbls(Character player, int lastYindex) {
        // clear arrays
        charLbls.clear();
        talkBtns.clear();

        // tracking variables
        lastYindex = lastYindex + 2;
        Character targetChar;
        int xCap = 0;
        int yCap = 0;

        // loop through characters in the current location
        for(int i=0; i<player.getCurrentLocation().getCharacters().size(); i++) {
            // add character name labels and talk to buttons
            targetChar = player.getCurrentLocation().getCharacters().get(i);
            charLbls.add(new Label(String.format("%s %s", targetChar.getFirstName(), targetChar.getLastName())));
            talkBtns.add(new Button("Talk To"));
            GridPane.setConstraints(charLbls.get(i), xCap, lastYindex + yCap);
            GridPane.setConstraints(talkBtns.get(i), xCap, lastYindex + yCap + 1);

            // setup buttons
            Character finalTargetChar = targetChar;
            talkBtns.get(i).setOnAction(ActionEvent -> {
                if(!player.hasTalkedToToday(finalTargetChar)) {
                    player.addRelationship(finalTargetChar, ThreadLocalRandom.current().nextDouble(0, 10));
                    finalTargetChar.addRelationship(player, ThreadLocalRandom.current().nextDouble(0, 10));
                    String output =
                            """
                            %s talked with %s and have %.2f/100.00 relationship They like you %.2f/100.00
                            """.formatted(
                                    player.getFirstName(),
                                    finalTargetChar.getFirstName(),
                                    player.getRelationship(finalTargetChar),
                                    finalTargetChar.getRelationship(player)
                            );
                    updateAll();
                    console.appendText(output);
                }
            });

            if(!(targetChar == player) && !player.hasTalkedToToday(targetChar)) {
                centerGridPane.getChildren().addAll(talkBtns.get(i));
            }
            centerGridPane.getChildren().addAll(charLbls.get(i));

            // tracking
            xCap++;
            if(xCap == 6) {
                xCap = 0;
                yCap += 2;
            }
        }
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
        // load fxml for JobInfoScreen.fxml
        try {
            Engine.loadFXML("JobInfoScreen.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onMapInfo() {
        updateAll();
    }

    public void onCoursesInfo() {
        // load fxml for CourseInfoScreen.fxml
        try {
            Engine.loadFXML("CoursesInfoScreen.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
