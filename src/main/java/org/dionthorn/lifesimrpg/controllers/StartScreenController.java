package org.dionthorn.lifesimrpg.controllers;

/**
 * Will manage the start screen
 */
public class StartScreenController extends AbstractStartScreenController {

    /**
     * Override just populates the selectMapBox after loading FXML variables
     */
    @Override
    public void initialize() {
        populateMapBox();
    }

}
