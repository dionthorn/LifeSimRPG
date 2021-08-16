package org.dionthorn.lifesimrpg.controllers;

public class StartScreenController extends NewGameScreenController {

    // singleton just so we can Instantiate the NewGameScreen,
    // notice how GameOverScreenController is slightly different.
    // In that we can Override initialize() change the CURRENT_SCREEN
    // and output to the console.

}
