package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.dionthorn.lifesimrpg.*;
import org.dionthorn.lifesimrpg.entities.*;
import org.dionthorn.lifesimrpg.entities.Character;

import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class Engine {

    // keep track of which screen we are on for flagging
    public enum SCREEN {
        BOOT,
        CHARACTER_CREATION,
        MAIN,
        PLAYER_INFO,
        JOB_INFO,
        MAP_INFO,
        DEAD
    }

    // Constants
    public static final int SCREEN_WIDTH = 1024;
    public static final int SCREEN_HEIGHT = 768;

    // Game data
    public static SCREEN CURRENT_SCREEN = SCREEN.BOOT;
    public static Stage rootStage;
    public static FXMLLoader rootLoader;
    public static GameState gameState;

    public static void setStage(Stage stage) {
        rootStage = stage;
        rootStage.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode() == KeyCode.ESCAPE) {
                rootStage.close();
            } else if(key.getCode() == KeyCode.F7) {
                dumpEntityData();
            }
        });
        try{
            loadFXML("StartScreen.fxml");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Game Time Progress

    public static void nextWeek(TextArea console) {
        console.clear();
        for(int i=0; i<7; i++) {
            if(gameState.getPlayer().getHealth() > 0) {
                nextDay(console);
            }
        }
    }

    public static void nextDay(TextArea console) {
        // update entities for now this is just sending them home on the new day
        for(Entity e: Entity.entities) {
            if(e instanceof Character) {
                ((Character) e).update();
            }
        }

        // advance a day
        LocalDate currentDate = gameState.getCurrentDate();
        gameState.setCurrentDate(currentDate.plusDays(1));
        console.appendText(
                """
                %s: %s %d %d
                """.formatted(
                        currentDate.getDayOfWeek().name(),
                        currentDate.getMonth().name(),
                        currentDate.getDayOfMonth(),
                        currentDate.getYear()
                )
        );

        // birthday check
        Character player = gameState.getPlayer();
        if(player.isBirthday(currentDate)) {
            console.appendText("Happy Birthday!\n");
        }

        // do Job logic
        if(player.getJob().getSalary() != 0) {
            // Check if it is a work day
            if(player.getJob().isWorkDay(currentDate.getDayOfWeek().getValue())) {
                // perform work logic and tell player in console they worked
                player.getJob().workDay();
                console.appendText(
                        """
                        %s worked at %s today
                        """.formatted(
                                player.getFirstName(),
                                player.getJob().getName()
                        )
                );
            }

            // Pay day is the 1st and 15th of every month regardless of job, might abstract this so jobs
            // can have different pay days/pay times much like the work days are already abstracted to the object
            if(currentDate.getDayOfMonth() == 1 || currentDate.getDayOfMonth() == 15) {
                int originalSalary = 0;

                // if it's been a year or more on this pay day we store the original salary
                if(player.getJob().getYearDate().plusYears(1).equals(currentDate)) {
                    originalSalary = player.getJob().getSalary();
                }

                // get the player payout up to the current date
                int payout = player.getJob().payout(currentDate);

                // inform them of what they've been paid
                console.appendText(
                        """
                        Made $%d from working at %s
                        """.formatted(
                                payout,
                                player.getJob().getName()
                        )
                );

                // set the days paid out to equal the amount of days worked
                player.getJob().setDaysPaidOut(player.getJob().getDaysWorked());
                player.setMoney(player.getMoney() + payout);
                if(originalSalary > 0) {
                    console.appendText(
                            """
                            %s got a raise of $%d per day!
                            """.formatted(
                                    player.getFirstName(),
                                    player.getJob().getSalary() - originalSalary
                            )
                    );
                }
            }
        }

        // residence update
        player.getHome().onNextDay();

        // pay rent on the first of the month
        if(currentDate.getDayOfMonth() == 1) {
            int rentPeriod = player.getHome().getDaysInPeriod();
            int rentCost = player.getHome().getRentPeriodCost();
            if(player.getMoney() - rentCost >= 0) {
                player.setMoney(player.getMoney() - rentCost);

                // successfully paid rent
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

                // couldn't afford rent
                console.appendText(
                        """
                        %s couldn't pay rent! An increase of $%d per day has been added
                        """.formatted(
                                player.getFirstName(),
                                rentIncrease
                        )
                );

                // pretty heavy penalty at the moment
                player.getHome().setRent(player.getHome().getRent() + rentIncrease);
                player.getHome().setMonthsUnpaid(player.getHome().getMonthsUnpaid() + 1);
                player.getHome().setTotalUnpaid(rentCost);
                console.appendText(
                        """
                        %s haven't paid for %d months and owe a total of $%d
                        """.formatted(
                                player.getFirstName(),
                                player.getHome().getMonthsUnpaid(),
                                player.getHome().getTotalUnpaid()
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
            console.appendText("You couldn't pay for food today!\n");
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
                    loadFXML("GameOverScreen.fxml");
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // FX static methods

    public static void updateMoneyLbl(Label moneyLbl) {
        moneyLbl.setText(String.format("Cash: $%d", gameState.getPlayer().getMoney()));
    }

    public static void updateDateLbl(Label currentDateLbl) {
        LocalDate currentDate = gameState.getCurrentDate();
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

    // static utility methods

    public static void loadFXML(String fileName) throws Exception {
        FileOpUtils.checkJRT();
        URL test;
        if(FileOpUtils.JRT) {
            test = URI.create(FileOpUtils.jrtBaseURI + "FXML/" + fileName).toURL();
        } else {
            test = Objects.requireNonNull(App.class.getResource("/FXML/" + fileName)).toURI().toURL();
        }
        Parent root = FXMLLoader.load(test);
        Scene rootScene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        rootStage.setScene(rootScene);
    }

    public static void dumpEntityData() {
        ArrayList<Entity> entities = Entity.entities; // get quick dump of entity data
        for(Entity e : entities) {
            if (e instanceof Job) {
                Job temp = ((Job) e);
                System.out.print("Job UID:" + temp.getUID());
                System.out.print(" " + temp.getName());
                System.out.println(" " + temp.getSalary());
            } else if (e instanceof Map) {
                Map temp = ((Map) e);
                System.out.print("Map UID:" + temp.getUID());
                System.out.print(" " + temp.getName());
                System.out.println(" total places: " + temp.getPlaces().size());
            } else if (e instanceof Place) {
                Place temp = ((Place) e);
                System.out.print("Place UID:" + temp.getUID());
                System.out.print(" " + temp.getName());
                System.out.println(" total connections: " + temp.getConnections().size());
            } else if (e instanceof Character) {
                Character temp = ((Character) e);
                System.out.print("Character UID:" + temp.getUID());
                System.out.print(" " + temp.getFirstName() + " " + temp.getLastName());
                System.out.println(" is at: " + temp.getCurrentLocation().getName());
            }
        }
    }

}
