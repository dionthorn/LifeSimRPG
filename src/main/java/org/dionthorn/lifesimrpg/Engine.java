package org.dionthorn.lifesimrpg;

import org.dionthorn.lifesimrpg.entities.*;
import org.dionthorn.lifesimrpg.entities.Character;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.net.URI;
import java.net.URL;

/**
 * The Engine class will manage the GameState, FXMLLoader
 * And which screen we are on, it is essentially the interface between the
 * controllers and the entities packages. All variables and methods are public static
 * Engine.loadFXML(String fileName) can be used to load a different screen from any controller object
 */
public class Engine {

    /**
     * SCREEN enum is used to flag which screen we should be currently on
     * for example when you press next day, if you are on mapInfo it will just update mapInfo
     *
     */
    public enum SCREEN {
        BOOT,
        CHARACTER_CREATION,
        MAIN,
        PLAYER_INFO,
        JOB_INFO,
        MAP_INFO,
        COURSES_INFO,
        DEAD
    }

    // Constants
    public static final int SCREEN_WIDTH = 1024;
    public static final int SCREEN_HEIGHT = 768;

    // Access these from any controller or entity
    public static SCREEN CURRENT_SCREEN = SCREEN.BOOT;
    public static Stage rootStage;
    public static FXMLLoader rootLoader;
    public static GameState gameState;

    /**
     * Essentially our constructor we take it the Stage object from App.start() call
     * Then we add the global event handlers for ESC key and F7 key
     * Then we loadFXML for the StartScreen.fxml
     * @param stage Stage representing the root Stage object for the program to be statically referenced
     */
    public static void setStage(Stage stage) {
        rootStage = stage;

        // set Key Handlers
        rootStage.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode() == KeyCode.ESCAPE) {
                rootStage.close();
            } else if(key.getCode() == KeyCode.F7) {
                dumpEntityData();
            }
        });

        // Load the StartScreen
        try{
            loadFXML("StartScreen.fxml");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // Game Time Progress methods can be called from any ScreenController with Engine.nextWeek(TextArea console)

    public static String getDateString() {
        LocalDate currentDate = gameState.getCurrentDate();
        return """
                %s: %s %d %d
                """.formatted(
                currentDate.getDayOfWeek().name(),
                currentDate.getMonth().name(),
                currentDate.getDayOfMonth(),
                currentDate.getYear()
        );
    }

    /**
     * Will advance the game 7 days by calling nextDay() 7 times
     * @param console TextArea representing the current console object
     */
    public static void nextWeek(TextArea console) {
        console.clear();
        for(int i=0; i<7; i++) {
            if(gameState.getPlayer().getHealth() > 0) {
                nextDay(console);
            }
        }
    }

    /**
     * Will advance the game a day and do all related processing
     * This is the core of the game loop
     * @param console TextArea representing the current console object
     */
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

        // birthday check
        Character player = gameState.getPlayer();
        if(player.isBirthday(currentDate)) {
            console.appendText("Happy Birthday!\n");
        }

        // do Course logic
        Course playerCourse = player.getCurrentCourse();
        if(playerCourse != null) {
            player.addStat(playerCourse.getStatName(), playerCourse.getCurrentStatGain());
        }

        // do Job logic
        Job playerJob = player.getJob();
        if(playerJob.getSalary() != 0) {
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
                if(playerJob.getYearDate().plusYears(1).equals(currentDate)) {
                    originalSalary = playerJob.getSalary();
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
                                    playerJob.getSalary() - originalSalary
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

    /**
     * Will update the provided label to show the players current cash
     * @param moneyLbl Label representing the moneyLbl
     */
    public static void updateMoneyLbl(Label moneyLbl) {
        moneyLbl.setText(String.format("Cash: $%d", gameState.getPlayer().getMoney()));
    }

    /**
     * Will update the provided label to show the current date
     * @param currentDateLbl Label representing the currentDateLbl
     */
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

    // utility methods

    /**
     * Will find the FXML file if in JRT or not and use FXMLLoader.load(URL) to load the new Scene
     * We use the static rootStage object and setScene() on the result of the .load()
     * @param fileName String the target filename ex: "StartScreen.fxml"
     * @throws Exception Exception representing either an IO or File error, shouldn't happen,
     * but we print stack trace in a try catch anytime we call this method just in case.
     */
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

    /**
     * This will dump Entity related data into System.out
     */
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
