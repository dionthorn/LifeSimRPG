package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.dionthorn.lifesimrpg.Engine;
import org.dionthorn.lifesimrpg.data.Attributes;
import org.dionthorn.lifesimrpg.data.Dice;
import org.dionthorn.lifesimrpg.entities.AICharacter;
import org.dionthorn.lifesimrpg.entities.AbstractCharacter;
import org.dionthorn.lifesimrpg.entities.Place;
import org.dionthorn.lifesimrpg.entities.PlayerCharacter;
import java.util.ArrayList;

/**
 * Will manage the map info screen
 */
public class MapInfoScreenController extends AbstractGameScreenController {

    // Lists used to store the dynamically generated place and char labels and buttons
    private static final ArrayList<Label> connectingPlaceLbls = new ArrayList<>();
    private static final ArrayList<Button> connectingPlaceBtns = new ArrayList<>();
    private static final ArrayList<Label> charLbls = new ArrayList<>();
    private static final ArrayList<Button> talkBtns = new ArrayList<>();

    // FXML variables
    @FXML public Label currentLocationLbl;
    @FXML public Label currentPeopleLbl;
    @FXML public Button coursesInfoBtn;

    /**
     * Override allows us to set the screen flag and append text to the console
     * as well as a screen specific updateAll() after FXML variables load
     */
    @Override
    public void initialize() {
        Engine.currentScreen = Engine.SCREEN.MAP_INFO;
        console.appendText(getDateString());
        updateAll();
    }

    /**
     * Override allows us to do map info screen specific updates
     */
    @Override
    public void updateAll() {
        if(Engine.currentScreen != Engine.SCREEN.MAP_INFO) {
            console.appendText(getDateString());
        }
        if(Engine.currentScreen == Engine.SCREEN.MAP_INFO || Engine.currentScreen == Engine.SCREEN.DEAD) {
            centerGridPane.getChildren().clear();
        }

        // get a player reference
        PlayerCharacter player = Engine.gameState.getPlayer();

        // update variable texts
        playerInfoBtn.setText(String.format("%s Info", player.getFirstName()));

        // Move all characters around if user press the mapInfoBtn
        for(AbstractCharacter c: Engine.gameState.getCurrentMap().getAllCharacters()) {
            if(c instanceof AICharacter) {
                ((AICharacter) c).moveRandom();
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
        int lastYIndex = generatePlaceBtnsAndLbls(player);

        // generate the Label and Button for each character in the current location
        generateTalkBtnsAndLbls(player, lastYIndex);

        // If in a SCHOOL Place then set the coursesInfo button to visible
        coursesInfoBtn.setVisible(player.getCurrentLocation().getType() == Place.PLACE_TYPE.SCHOOL);

        updateDateLbl();
        updateMoneyLbl();
    }

    /**
     * Will generate place Buttons and Labels
     * @param player AbstractCharacter representing the player
     * @return int representing the last y index used
     */
    public int generatePlaceBtnsAndLbls(AbstractCharacter player) {
        // clear arrays
        connectingPlaceLbls.clear(); // maybe instead we set up these with default buttons/labels on first pass
        connectingPlaceBtns.clear(); // and do .setText() calls instead of generating new in the below loop?

        // some tracking variables
        int lastYIndex = 0;
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
            lastYIndex = yCap;
            xCap += 2;
            if(xCap == 6) {
                xCap = 0;
                yCap ++;
            }

        }

        // setup character label and buttons
        GridPane.setConstraints(currentPeopleLbl, 0, lastYIndex + 1);
        centerGridPane.getChildren().addAll(currentPeopleLbl);

        // we return the last Y index used for placing nodes
        // on the GridPane used by the generateTalkBtnsAndLbls method
        return lastYIndex;
    }

    /**
     * Will generate talk Buttons and Labels
     * @param player AbstractCharacter representing the player
     * @param lastYIndex int representing the last y index used
     */
    public void generateTalkBtnsAndLbls(AbstractCharacter player, int lastYIndex) {
        // clear arrays
        charLbls.clear();
        talkBtns.clear();

        // tracking variables
        lastYIndex = lastYIndex + 2;
        AbstractCharacter targetChar;
        int xCap = 0;
        int yCap = 0;

        // loop through characters in the current location
        for(int i=0; i<player.getCurrentLocation().getCharacters().size(); i++) {
            // add character name labels and talk to buttons
            targetChar = player.getCurrentLocation().getCharacters().get(i);
            charLbls.add(new Label(targetChar.getName()));
            talkBtns.add(new Button("Talk To"));
            GridPane.setConstraints(charLbls.get(i), xCap, lastYIndex + yCap);
            GridPane.setConstraints(talkBtns.get(i), xCap, lastYIndex + yCap + 1);

            // setup buttons
            AbstractCharacter finalTargetChar = targetChar;
            talkBtns.get(i).setOnAction(ActionEvent -> {
                if(!player.hasTalkedToToday(finalTargetChar)) {
                    // roll 1d4 + charisma bonus can be negative
                    Dice diceSet = new Dice(4);
                    double playerBonus = Attributes.checkBonus(player.getAttributes().getCharisma());
                    double targetBonus = Attributes.checkBonus(finalTargetChar.getAttributes().getCharisma());
                    player.addRelationship(finalTargetChar, diceSet.roll() + targetBonus);
                    finalTargetChar.addRelationship(player, diceSet.roll() + playerBonus);
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

    // FXML Button press methods

    /**
     * Override FXML Button from AbstractGameScreenController
     * Will allow us to just updateAll() on a nextDay() call instead of Engine.loadMapFXML("MapInfoScreen.fxml");
     */
    @Override
    @FXML public void onMapInfo() {
        updateAll();
    }

    /**
     * FXML button will load the CourseInfoScreen
     */
    @FXML public void onCoursesInfo() {
        // load fxml for CourseInfoScreen.fxml
        try {
            Engine.loadMapFXML("CoursesInfoScreen.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
