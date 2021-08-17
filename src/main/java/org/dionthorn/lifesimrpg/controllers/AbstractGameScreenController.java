package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import org.dionthorn.lifesimrpg.Engine;
import org.dionthorn.lifesimrpg.entities.*;
import java.time.LocalDate;

/**
 * Used for PlayerInfo, MapInfo, and JobInfo as they all share these nodes
 */
public abstract class AbstractGameScreenController extends AbstractScreenController {

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

    protected void updateAll() {
        // all AbstractGameScreenController must have a updateAll() override
        // that will handle the screen specific updates
    }

    // Console Clear
    @FXML protected void clearConsole() {
        console.clear();
        console.appendText(getDateString());
    }

    // Time Changers

    @FXML protected void onNextDay() {
        nextDay();
        update();
    }

    @FXML protected void onNextWeek() {
        nextWeek();
        update();
    }

    protected void update() {
        updateTitle(false);
        updateAll();
    }

    protected void updateTitle(boolean isButton) {
        AbstractCharacter player = Engine.gameState.getPlayer();
        if(player.hasCourse()) {
            int currentLevel = player.getCurrentCourse().getCourseLevel();
            String newTitle = player.checkTitle();
            if(!(player.getTitles().contains(newTitle))) {
                player.getTitles().add(newTitle);
            }
            if(currentLevel < player.getCurrentCourse().getCourseLevel()) {
                console.appendText("You increased in title to: %s\n".formatted(newTitle));
            } else {
                if(isButton) {
                    console.appendText("You are not yet qualified for the next title\n");
                    console.appendText(
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
        moneyLbl.setText(String.format("Cash: $%d", Engine.gameState.getPlayer().getMoney()));
    }

    /**
     * Will update the date label
     */
    protected void updateDateLbl() {
        LocalDate currentDate = Engine.gameState.getCurrentDate();
        currentDateLbl.setText(
                """
                Date: %d / %d / %d
                """.formatted(
                        currentDate.getDayOfMonth(),
                        currentDate.getMonthValue(),
                        currentDate.getYear()
                )
        );
    }

    // Game Time Progress methods can be called from any AbstractScreenController with Engine.nextWeek(TextArea console)

    /**
     * Will advance the game 7 days by calling nextDay() 7 times
     */
    public void nextWeek() {
        console.clear();
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
        console.appendText(getDateString());

        // birthday check
        AbstractCharacter player = Engine.gameState.getPlayer();
        if(player.isBirthday(currentDate)) {
            console.appendText("Happy Birthday!\n");
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
                console.appendText(
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
                int payout = playerJob.payout(currentDate);

                // inform them of what they've been paid
                console.appendText(
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
                    console.appendText(
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

        // pay rent on the first of the month
        if(currentDate.getDayOfMonth() == 1) {
            int rentPeriod = playerHome.getDaysInPeriod();
            int rentCost = playerHome.getRentPeriodCost();

            // check if player can pay rent
            if(player.getMoney() - rentCost >= 0) {
                player.setMoney(player.getMoney() - rentCost);
                console.appendText(
                        """
                        %s paid $%d in rent!
                        """.formatted(
                                player.getFirstName(),
                                rentCost
                        )
                );
            } else {
                // maybe set rent higher based on 12 month rent distributed?
                int rentIncrease = (rentCost / rentPeriod) / 12;
                console.appendText(
                        """
                        %s couldn't pay rent! An increase of $%d per day has been added
                        """.formatted(
                                player.getFirstName(),
                                rentIncrease
                        )
                );

                // set rent and calculate rent debt
                playerHome.setRent(player.getHome().getRent() + rentIncrease);
                playerHome.setMonthsUnpaid(player.getHome().getMonthsUnpaid() + 1);
                playerHome.setTotalUnpaid(rentCost);
                console.appendText(
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
            console.appendText(String.format("%s couldn't pay for food today!\n", player.getFirstName()));
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
                    Engine.loadFXML("GameOverScreen.fxml");
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
