package org.dionthorn.lifesimrpg.controllers;

import org.dionthorn.lifesimrpg.Engine;
import org.dionthorn.lifesimrpg.entities.PlayerCharacter;

/**
 * Will manage the game over screen
 */
public class GameOverScreenController extends AbstractStartScreenController {

    /**
     * Override will let us set the Screen flag, populate the map box,
     * set console text and if the player died say how
     */
    @Override
    public void initialize() {
        Engine.currentScreen = Engine.SCREEN.DEAD;
        populateMapBox();
        console.appendText(getDateString());
        PlayerCharacter player = Engine.gameState.getPlayer();;
        if(player.getHealth() == 0) {
            console.appendText(String.format(
                    "You died of starvation after %s days!\n",
                    player.getDaysWithoutFood()
            ));
        }
    }

}
