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
import org.dionthorn.lifesimrpg.entities.Place;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MapInfoScreenController {

    private final ArrayList<Label> connectingPlaceLbls = new ArrayList<>();
    private final ArrayList<Button> connectingPlaceBtns = new ArrayList<>();
    private final ArrayList<Label> charLbls = new ArrayList<>();
    private final ArrayList<Button> talkBtns = new ArrayList<>();

    @FXML public HBox topBar;
    @FXML public GridPane centerGridPane;
    @FXML public VBox leftBar;
    @FXML public VBox rightBar;
    @FXML public TextArea console;
    @FXML public Label moneyLbl;
    @FXML public Region hRegion;
    @FXML public Label currentDateLbl;
    @FXML public Label currentLocationLbl;
    @FXML public Label currentPeopleLbl;
    @FXML public Button playerInfoBtn;
    @FXML public Button jobInfoBtn;
    @FXML public Button mapInfoBtn;
    @FXML public Region vRegion3;
    @FXML public Button clearConsoleBtn;
    @FXML public Region vRegion2;
    @FXML public Button nextWeekBtn;
    @FXML public Button nextDayBtn;

    public void initialize() {
        Engine.CURRENT_SCREEN = Engine.SCREEN.MAP_INFO;
        updateAll();
    }

    public void updateAll() {
        if(Engine.CURRENT_SCREEN == Engine.SCREEN.MAP_INFO) {
            // clear grid pane if already in Map Info
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

                    console.appendText(output);
                }
            });

            // add button and label to centerGridPane
            if(!(targetChar == player)) {
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

    public void clearConsole() {
        console.clear();
    }

    public void onNextWeek() {
        Engine.nextWeek(console);
        updateAll();
    }

    public void onNextDay() {
        Engine.nextDay(console);
        updateAll();
    }
}