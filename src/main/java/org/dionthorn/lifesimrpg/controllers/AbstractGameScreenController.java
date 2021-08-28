package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.dionthorn.lifesimrpg.Engine;
import org.dionthorn.lifesimrpg.entities.*;
import java.time.LocalDate;

/**
 * Used as the usual primary 'shell' of the user interface during normal game play
 * We tend to treat the left bar as 'static' and every screen should have its interfacing buttons
 * Same with the top bar and the console
 * the right bar we only keep the nextDay and nextWeek button in, but the top portion is 'dynamic'
 * in that it will be altered 'contextually' by child classes
 * for example if you are in a PLACE_TYPE.SCHOOL while on the MapInfoScreen then you will see
 * a 'Course Info' button appear on the right bar
 */
public abstract class AbstractGameScreenController extends AbstractScreenController {

    // FXML JavaFX Nodes Common amongst all subclasses
    @FXML public Button playerInfoBtn;
    @FXML public Button jobInfoBtn;
    @FXML public Button mapInfoBtn;
    @FXML public Button nextWeekBtn;
    @FXML public Button nextDayBtn;
    @FXML public Button clearConsoleBtn;
    @FXML public Label moneyLbl;
    @FXML public Label currentDateLbl;
    @FXML public Region hRegion;
    @FXML public Region vRegion2;

    // FXML methods simply button press methods

    /**
     * FXML button method to clear the console
     */
    @FXML protected void onClearConsole() {
        this.console.clear();
        this.console.appendText(getDateString());
    }

    /**
     * FXML button method to advance a day
     */
    @FXML protected void onNextDay() {
        nextDay();
        update();
    }

    /**
     * FXML button to advance a week
     */
    @FXML protected void onNextWeek() {
        nextWeek();
        update();
    }

    /**
     * FXML button that should be overridden if the screen change is unneeded.
     */
    @FXML public void onPlayerInfo() {
        // load fxml for PlayerInfoScreen.fxml
        try {
            Engine.loadMapFXML("PlayerInfoScreen.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * FXML button that should be overridden if the screen change is unneeded.
     */
    @FXML public void onJobInfo() {
        // load fxml for JobInfoScreen.fxml
        try {
            Engine.loadMapFXML("JobInfoScreen.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * FXML button that should be overridden if the screen change is unneeded.
     */
    @FXML public void onMapInfo() {
        // load fxml for JobInfoScreen.fxml
        try {
            Engine.loadMapFXML("MapInfoScreen.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Logical

    /**
     * AbstractGameScreenController children should override updateAll() for their specific needs
     */
    protected void updateAll() {
        // all AbstractGameScreenController children should override updateAll()
        // that will handle the screen specific updates
    }

    /**
     * Will updateTitle(false) and updateAll() this is called after every nextDay and nextWeek
     */
    protected void update() {
        updateTitle(false);
        updateAll();
    }

    /**
     * Will update the player Course title if applicable and append relevant text to console
     * @param isButton boolean representing if the caller is a Button reaction or not
     */
    protected void updateTitle(boolean isButton) {
        AbstractCharacter player = Engine.gameState.getPlayer();
        if(player.hasCourse()) {
            int currentLevel = player.getCurrentCourse().getCourseLevel();
            String newTitle = player.checkTitle();
            if(!(player.getTitles().contains(newTitle))) {
                player.getTitles().add(newTitle);
            }
            if(currentLevel < player.getCurrentCourse().getCourseLevel()) {
                this.console.appendText("You increased in title to: %s\n".formatted(newTitle));
                // make sure to remove the first course level as that is usually just 'student' or 'participant' etc
                System.out.println(player.getCurrentCourse().getCourseLevel());
                System.out.println(player.getCurrentCourse().getTitles()[0]);
                if(player.getCurrentCourse().getCourseLevel() == 2) {
                    player.getTitles().remove(player.getCurrentCourse().getTitles()[0]);
                }
            } else {
                if(isButton) {
                    this.console.appendText("You are not yet qualified for the next title\n");
                    this.console.appendText(
                            """
                            You need %s stat to be %s
                            """.formatted(
                                    player.getCurrentCourse().getStatName(),
                                    player.getCurrentCourse().getCurrentTitleRequirement()
                            )
                    );
                }
            }
        }
        player.getTitles().removeIf(title -> title.contains("Not Qualified"));
        updateAll();
    }

    /**
     * Will update the money label
     */
    protected void updateMoneyLbl() {
        this.moneyLbl.setText(String.format("Cash: $%d", Engine.gameState.getPlayer().getMoney()));
    }

    /**
     * Will update the date label
     */
    protected void updateDateLbl() {
        LocalDate currentDate = Engine.gameState.getCurrentDate();
        this.currentDateLbl.setText(
                """
                Date: %d / %d / %d
                """.formatted(
                        currentDate.getDayOfMonth(),
                        currentDate.getMonthValue(),
                        currentDate.getYear()
                )
        );
    }

    // Game Time Progress methods

    /**
     * Will advance the game 7 days by calling nextDay() 7 times
     */
    public void nextWeek() {
        this.console.clear();
        for(int i=0; i<7; i++) {
            if(Engine.gameState.getPlayer().getHealth() > 0) {
                nextDay();
            }
        }
    }

    /**
     * Will advance the game a day and do all related processing
     * This is the core of the game loop
     */
    public void nextDay() {
        // update entities for now this is just sending them home on the new day
        for(AbstractEntity e: AbstractEntity.entities) {
            if(e instanceof AbstractCharacter) {
                ((AbstractCharacter) e).update();
            }
        }

        // advance a day
        LocalDate currentDate = Engine.gameState.getCurrentDate();
        Engine.gameState.setCurrentDate(currentDate.plusDays(1));
        currentDate = Engine.gameState.getCurrentDate();
        this.console.appendText(getDateString());

        // birthday check
        AbstractCharacter player = Engine.gameState.getPlayer();
        if(player.isBirthday(currentDate)) {
            this.console.appendText("Happy Birthday!\n");
        }

        // do Course stat gain logic
        Course playerCourse = player.getCurrentCourse();
        if(playerCourse != null) {
            player.addStat(playerCourse.getStatName(), playerCourse.getCurrentStatGain());
        }

        // do Job logic
        Job playerJob = player.getJob();
        if(playerJob.getDailyPayRate() != 0) {
            // Check if it is a work day
            if(playerJob.isWorkDay(currentDate.getDayOfWeek().getValue())) {
                // perform work logic and tell player in console they worked
                playerJob.workDay();
                this.console.appendText(
                        """
                        %s worked at %s today
                        """.formatted(
                                player.getFirstName(),
                                playerJob.getName()
                        )
                );
            }

            // Pay day is the 1st and 15th of every month regardless of job, might abstract this so jobs
            // can have different pay days/pay times much like the work days are already abstracted to the object
            if(currentDate.getDayOfMonth() == 1 || currentDate.getDayOfMonth() == 15) {
                int originalSalary = 0;

                // if it's been a year or more on this pay day we store the original salary
                if(playerJob.getOneYearDateTracker().plusYears(1).equals(currentDate)) {
                    originalSalary = playerJob.getDailyPayRate();
                }

                // get the player payout up to the current date
                int payout = player.payout(currentDate);

                // inform them of what they've been paid
                this.console.appendText(
                        """
                        Made $%d from working at %s
                        """.formatted(
                                payout,
                                playerJob.getName()
                        )
                );

                // set the days paid out to equal the amount of days worked
                playerJob.setDaysPaidOut(playerJob.getDaysWorked());
                player.setMoney(player.getMoney() + payout);
                if(originalSalary > 0) {
                    this.console.appendText(
                            """
                            %s got a raise of $%d per day!
                            """.formatted(
                                    player.getFirstName(),
                                    playerJob.getDailyPayRate() - originalSalary
                            )
                    );
                }
            }
        }

        // residence update
        Residence playerHome = player.getHome();
        playerHome.onNextDay();

        // pay rentPerDay on the first of the month
        if(currentDate.getDayOfMonth() == 1) {
            int rentPeriod = playerHome.getDaysInPeriod();
            int rentCost = playerHome.getRentPeriodCost();

            // check if player can pay rentPerDay
            if(player.getMoney() - rentCost >= 0) {
                player.setMoney(player.getMoney() - rentCost);
                this.console.appendText(
                        """
                        %s paid $%d in rent!
                        """.formatted(
                                player.getFirstName(),
                                rentCost
                        )
                );
            } else {
                // maybe set rentPerDay higher based on 12 month rentPerDay distributed?
                int rentIncrease = (rentCost / rentPeriod) / 12;
                this.console.appendText(
                        """
                        %s couldn't pay rent! An increase of $%d per day has been added
                        """.formatted(
                                player.getFirstName(),
                                rentIncrease
                        )
                );

                // set rentPerDay and calculate rentPerDay debt
                playerHome.setRentPerDay(player.getHome().getRentPerDay() + rentIncrease);
                playerHome.setMonthsUnpaid(player.getHome().getMonthsUnpaid() + 1);
                playerHome.setTotalUnpaid(rentCost);
                this.console.appendText(
                        """
                        %s haven't paid for %d months and owe a total of $%d
                        """.formatted(
                                player.getFirstName(),
                                playerHome.getMonthsUnpaid(),
                                playerHome.getTotalUnpaid()
                        )
                );
            }
        }

        // eat food logic
        if(player.getMoney() - player.getFoodCost() >= 0) {
            player.setMoney(player.getMoney() - player.getFoodCost());
            player.setDaysWithoutFood(0);
            player.setHealth(player.getHealth() + 0.5);
        } else {
            // do not enough money for food logic here
            this.console.appendText(String.format("%s couldn't pay for food today!\n", player.getFirstName()));
            player.setDaysWithoutFood(player.getDaysWithoutFood() + 1);

            // will die after 23 days in a row with no food starting from 100 health
            if(player.getHealth() - (0.25 * (player.getDaysWithoutFood() * 1.5)) > 0) {
                // apply damage
                player.setHealth(player.getHealth() - (0.25 * (player.getDaysWithoutFood() * 1.5)));
            } else {
                // dead
                player.setHealth(0);

                // setup game over screen, we leave console output
                try{
                    Engine.loadMapFXML("GameOverScreen.fxml");
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
