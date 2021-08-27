package org.dionthorn.lifesimrpg.controllers;

import org.dionthorn.lifesimrpg.Engine;

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
        if(Engine.gameState.getPlayer().getHealth() == 0) {
            console.appendText(String.format(
                    "You died of starvation after %s days!\n",
                    Engine.gameState.getPlayer().getDaysWithoutFood()
            ));
        }
    }

}
