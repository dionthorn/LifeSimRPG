package org.dionthorn.lifesimrpg.controllers;

import org.dionthorn.lifesimrpg.Engine;

public class GameOverScreenController extends AbstractStartScreenController {

    @Override
    public void initialize() {
        Engine.CURRENT_SCREEN = Engine.SCREEN.DEAD;
        console.appendText(Engine.getDateString());
        if(Engine.gameState.getPlayer().getHealth() == 0) {
            console.appendText(String.format(
                    "You died of starvation after %s days!\n",
                    Engine.gameState.getPlayer().getDaysWithoutFood()
            ));
        }
    }

}
