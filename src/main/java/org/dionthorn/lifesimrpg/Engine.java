package org.dionthorn.lifesimrpg;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Engine {

    // GameState data
    private GameState gameState;

    // JavaFX components
    private final GridPane centerGridPane   = new GridPane();
    private final HBox topBar               = new HBox(8);
    private final VBox leftBar              = new VBox(8);
    private final VBox rightBar             = new VBox(8);
    private final Region vRegion            = new Region();
    private final Region vRegion2           = new Region();
    private final Region vRegion3           = new Region();
    private final Region hRegion            = new Region();
    private final TextArea console          = new TextArea("Welcome! Please Press the New Game Button Above!");
    private final Label currentDateLbl      = new Label("Date: 0/0/0");
    private final Label moneyLbl            = new Label("Cash: $-0");
    private final Button newGameBtn         = new Button("New Game");
    // overboard maybe?
    private final Label enterFirstNameLbl   = new Label("Enter Your First Name: ");
    private final Label enterLastNameLbl    = new Label("Enter Your Last Name: ");
    private final Label enterBirthdayLbl    = new Label("Enter Your Birth Day\nDefault is 18 years old");
    private final Label dayLbl              = new Label("Day");
    private final Label monthLbl            = new Label("Month");
    private final Label yearLbl             = new Label("Year");
    private final TextField firstNameInput  = new TextField();
    private final TextField lastNameInput   = new TextField();
    private final TextField dayInput        = new TextField("1");
    private final TextField monthInput      = new TextField("1");
    private final TextField yearInput       = new TextField("1972");
    // ye thats a lot of components
    private final ArrayList<Label> connectingPlaceLbls  = new ArrayList<>();
    private final ArrayList<Button> connectingPlaceBtns = new ArrayList<>();
    private final ArrayList<Label> charLbls     = new ArrayList<>();
    private final ArrayList<Button> talkBtns    = new ArrayList<>();
    private final Label currentLocationLbl      = new Label();
    private final Button createNewPlayerBtn     = new Button("Create Player");
    private final Label currentPeopleLbl        = new Label("Current People Present:");
    private final Label playerNameLbl           = new Label();
    private final Label currentHouseInfoLbl     = new Label();
    private final Label currentJobLbl           = new Label();
    private final Label currentDaysWorkedLbl    = new Label();
    private final Label getJobLbl               = new Label("Get A Job $ is per day: ");
    private final ComboBox<String> jobOptions   = new ComboBox<>();
    private final Button chooseNewJob           = new Button("Apply for Job");
    private final Button nextWeekBtn            = new Button("Next Week");
    private final Button nextDayBtn             = new Button("Next Day");
    private final Button playerInfoBtn          = new Button();
    private final Button jobInfoBtn             = new Button("Job Info");
    private final Button mapInfoBtn             = new Button("Map Info");
    private final Button clearConsoleBtn        = new Button("Clear Console");

    public Engine(Stage primaryStage) {
        // Add root nodes
        BorderPane rootPane = new BorderPane();
        rootPane.setCenter(centerGridPane);
        rootPane.setTop(topBar);
        rootPane.setLeft(leftBar);
        rootPane.setRight(rightBar);
        rootPane.setBottom(console);
        Scene rootScene = new Scene(rootPane, App.SCREEN_WIDTH, App.SCREEN_HEIGHT);

        // Prep common variables
        int padding = 10;
        Insets insets = new Insets(padding, padding, padding, padding);
        String style = "-fx-background-color: #336699;";
        double minWidth = App.SCREEN_WIDTH/32d;
        Font bigFont = new Font("Times New Roman", 28);

        // setup nodes
        moneyLbl.setFont(bigFont);
        currentDateLbl.setFont(bigFont);
        HBox.setHgrow(hRegion, Priority.ALWAYS);
        GridPane.setVgrow(vRegion, Priority.ALWAYS);
        VBox.setVgrow(vRegion2, Priority.ALWAYS);
        VBox.setVgrow(vRegion3, Priority.ALWAYS);

        // setup bottom bar (console)
        console.setPrefSize(App.SCREEN_WIDTH, App.SCREEN_HEIGHT/4d);

        // setup top bar
        topBar.setPadding(insets);
        topBar.setStyle(style);
        topBar.setSpacing(padding);

        // setup left bar
        leftBar.setPadding(insets);
        leftBar.setStyle(style);
        leftBar.setMinWidth(minWidth);

        // setup right bar
        rightBar.setPadding(insets);
        rightBar.setStyle(style);
        rightBar.setMinWidth(minWidth);

        // setup New Game Button and add to right bar
        newGameBtn.setOnAction(ActionEvent -> newGame());
        rightBar.getChildren().add(newGameBtn);

        // setup center grid pane
        centerGridPane.setVgap(padding);
        centerGridPane.setHgap(padding);
        centerGridPane.setPadding(insets);

        // set the scene
        primaryStage.setScene(rootScene);

        // add key handlers for debugging and esc exit key
        rootScene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode() == KeyCode.ESCAPE) {
                primaryStage.close();
            } else if(key.getCode() == KeyCode.F7) {
                dumpEntityData();
            }
        });

        // Check if we need to use JRT
        FileOpUtils.checkJRT();
    }

    // Screen

    public void newGame() {
        // Clear area
        clearAll();
        // Set the screen flag
        App.CURRENT_SCREEN = App.SCREEN.CHARACTER_CREATION;

        // Set GridPane constraints
        GridPane.setConstraints(enterFirstNameLbl,   0, 0);
        GridPane.setConstraints(firstNameInput,      1, 0);
        GridPane.setConstraints(enterLastNameLbl,    0, 1);
        GridPane.setConstraints(lastNameInput,       1, 1);
        GridPane.setConstraints(enterBirthdayLbl,    0, 2);
        GridPane.setConstraints(dayLbl,              1, 2);
        GridPane.setConstraints(dayInput,            1, 3);
        GridPane.setConstraints(monthLbl,            3, 2);
        GridPane.setConstraints(monthInput,          3, 3);
        GridPane.setConstraints(yearLbl,             5, 2);
        GridPane.setConstraints(yearInput,           5, 3);

        // Add Create Player button
        rightBar.getChildren().add(createNewPlayerBtn);
        createNewPlayerBtn.setOnAction(ActionEvent -> {
            // pull data from text fields
            String firstName = firstNameInput.getText();
            String lastName = lastNameInput.getText();
            int day = Integer.parseInt(dayInput.getText());
            int month = Integer.parseInt(monthInput.getText());
            int year = Integer.parseInt(yearInput.getText());
            // try to make a LocalDate from the birthday
            LocalDate birthday = null;
            try {
                birthday = LocalDate.of(year, month, day);
            } catch(Exception e) {
                // suppress exception if invalid date
            }
            // if the birthday is invalid or after the age cap will warn in console
            if(birthday == null || birthday.isAfter(GameState.AGE_CAP)) {
                console.clear();
                console.appendText("\nSorry that date is invalid try again!\n");
            } else {
                // if birthday is valid then create a new GameState and display the main screen
                gameState = new GameState(firstName, lastName, birthday);
                displayMainScreen();
            }
        });
        // Add all components to the center grid pane
        centerGridPane.getChildren().addAll(
                enterFirstNameLbl,  firstNameInput,
                enterLastNameLbl,   lastNameInput,
                enterBirthdayLbl,   dayLbl,     monthLbl,   yearLbl,
                                    dayInput,   monthInput, yearInput
        );

        // Finally, direct user to Create Player button
        console.setText(
                "Please enter all required information\n" +
                "Then press the \"Create Player\" button on the right bar"
        );
    }

    private void displayMapInfo() {
        // set screen
        App.CURRENT_SCREEN = App.SCREEN.MAP;

        // Move all characters around if user press the mapInfoBtn
        Character player = gameState.getPlayer();
        for(Character c: gameState.getCurrentMap().getAllCharacters()) {
            if(!(c == player)) {
                c.moveRandom();
            }
        }

        // setup centerGridPane and labels/button arrays
        centerGridPane.getChildren().clear();

        // setup current location label
        currentLocationLbl.setText(String.format(
                "Current Location: %s\n\nConnecting Locations:",
                player.getCurrentLocation().getName()
        ));
        centerGridPane.getChildren().add(currentLocationLbl);
        GridPane.setConstraints(currentLocationLbl, 0,0, 4, 1);

        // generate the Label and Button for each connecting location
        int lastYindex = generatePlaceBtnsAndLbls(player);

        // generate the Label and Button for each character in the current location
        generateTalkBtnsAndLbls(player, lastYindex);
    }

    public void generateTalkBtnsAndLbls(Character player, int lastYindex) {
        // clear
        charLbls.clear(); // maybe instead we setup these with default buttons/labels on first pass
        talkBtns.clear(); // and do .setText() calls instead of generating new in the below loop?

        // tracking variables
        lastYindex = lastYindex + 2;
        Character targetChar;
        int xCap = 0;
        int yCap = 0;

        // loop through characters in the current location
        for(int i=0; i<player.getCurrentLocation().getCharacters().size(); i++) {
            // add character name labels and talk to buttons
            targetChar = player.getCurrentLocation().getCharacters().get(i);
            charLbls.add(new Label(String.format("%s %s", targetChar.getFirstName(), targetChar.getLastName())));
            talkBtns.add(new Button("Talk To"));
            GridPane.setConstraints(charLbls.get(i), xCap, lastYindex + yCap);
            GridPane.setConstraints(talkBtns.get(i), xCap, lastYindex + yCap + 1);

            // setup buttons
            Character finalTargetChar = targetChar;
            talkBtns.get(i).setOnAction(ActionEvent -> {
                if(!player.hasTalkedToToday(finalTargetChar)) {
                    player.addRelationship(finalTargetChar, ThreadLocalRandom.current().nextDouble(0, 10));
                    finalTargetChar.addRelationship(player, ThreadLocalRandom.current().nextDouble(0, 10));
                    String output = String.format(
                            "%s talked with %s and have %.2f/100.00 relationship They like you %.2f/100.00\n",
                            player.getFirstName(),
                            finalTargetChar.getFirstName(),
                            player.getRelationship(finalTargetChar),
                            finalTargetChar.getRelationship(player)
                    );
                    console.appendText(output);
                }
            });

            // add button and label to centerGridPane
            if(!(targetChar == player)) {
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

    public int generatePlaceBtnsAndLbls(Character player) {
        // clear
        connectingPlaceLbls.clear(); // maybe instead we setup these with default buttons/labels on first pass
        connectingPlaceBtns.clear(); // and do .setText() calls instead of generating new in the below loop?

        // some tracking variables
        int lastYindex = 0;
        int xCap = 0;
        int yCap = 1;

        // loop current locations connections and generate labels and buttons for travel
        for(int i=0; i<player.getCurrentLocation().getConnections().size(); i++) {
            // target the location and make the buttons
            Place targetPlace = player.getCurrentLocation().getConnections().get(i);
            connectingPlaceLbls.add(new Label(targetPlace.getName()));
            connectingPlaceBtns.add(new Button("Go To"));
            connectingPlaceBtns.get(i).setOnAction(ActionEvent -> {
                gameState.getPlayer().moveTo(targetPlace);
                displayMapInfo();
            });

            // setup grid pane
            GridPane.setConstraints(connectingPlaceLbls.get(i), xCap, yCap); // 0,1
            GridPane.setConstraints(connectingPlaceBtns.get(i), xCap + 1, yCap); // 1,1
            centerGridPane.getChildren().addAll(connectingPlaceLbls.get(i), connectingPlaceBtns.get(i));

            // tracking
            lastYindex = yCap;
            xCap += 2;
            if(xCap == 6) {
                xCap = 0;
                yCap ++;
            }

        }

        // setup character label and buttons
        GridPane.setConstraints(currentPeopleLbl, 0, lastYindex + 1);
        centerGridPane.getChildren().addAll(currentPeopleLbl);

        // we return the last Y index used for placing nodes
        // on the GridPane used by the generateTalkBtnsAndLbls method
        return lastYindex;
    }

    public void displayPlayerInfo() {
        // set screen
        App.CURRENT_SCREEN = App.SCREEN.PLAYER_INFO;

        // clear
        centerGridPane.getChildren().clear();

        // setup labels from player info
        Character player = gameState.getPlayer();
        playerNameLbl.setText(String.format(
                "Player: %s %s Health: %.2f/%.2f",
                player.getFirstName(),
                player.getLastName(),
                player.getHealth(),
                player.getMaxHealth()
        ));
        GridPane.setConstraints(playerNameLbl, 0, 0);

        // add current location label and update it
        currentLocationLbl.setText(String.format(
                "Current Location: %s",
                player.getCurrentLocation().getName()
        ));
        GridPane.setConstraints(currentLocationLbl, 0, 1);

        // add current house info
        currentHouseInfoLbl.setText(String.format(
                "Rent $/day: $%d\nFood $/day: $%d\nMonths Rent Unpaid: %d\nTotal Rent Unpaid: $%d",
                player.getHome().getRent(),
                player.getFoodCost(),
                player.getHome().getMonthsUnpaid(),
                player.getHome().getTotalUnpaid()
        ));
        GridPane.setConstraints(currentHouseInfoLbl, 0, 2);

        // add all content
        centerGridPane.getChildren().addAll(playerNameLbl, currentLocationLbl, currentHouseInfoLbl);
    }

    public void displayJobInfo() {
        // set screen
        App.CURRENT_SCREEN = App.SCREEN.JOB_INFO;

        // clear
        centerGridPane.getChildren().clear();

        // Show Current Job Info
        Character player = gameState.getPlayer();

        // current job info
        currentJobLbl.setText(String.format(
                "Current Job: %s Current Salary: %d\nCurrent Title: %s",
                player.getJob().getName(),
                player.getJob().getSalary(),
                player.getJob().getCurrentTitle()
        ));
        GridPane.setConstraints(currentJobLbl, 0, 0);

        // current job info 2
        currentDaysWorkedLbl.setText(String.format(
                "Days Worked This Year: %d\nYears Worked: %d",
                player.getJob().getDaysWorked(),
                player.getJob().getYearsWorked()
        ));
        GridPane.setConstraints(currentDaysWorkedLbl, 0, 1);
        GridPane.setConstraints(vRegion, 0, 2);

        // Show Job Options
        GridPane.setConstraints(getJobLbl, 0, 3);
        for(Entity e: Entity.entities) {
            if(e instanceof Job && ((Job)e).isFromFile()) {
                Job target = (Job) e;
                String targetInfo = String.format(
                        "%s $%d",
                        target.getName(),
                        target.getSalary()
                );
                if(!(jobOptions.getItems().contains(targetInfo))) {
                    jobOptions.getItems().add(targetInfo);
                }
            }
        }
        jobOptions.getSelectionModel().select(0);
        GridPane.setConstraints(jobOptions, 1, 3);

        // Add job choice button
        chooseNewJob.setOnAction(ActionEvent -> {
            String selection = jobOptions.getSelectionModel().getSelectedItem();
            String jobName = selection.split("\\$")[0].replaceAll(" ", "");
            Job target;
            for(Entity e: Entity.entities) {
                if(e instanceof Job && ((Job)e).isFromFile()) {
                    if(((Job)e).getName().equals(jobName)) {
                        target = (Job) e;
                        // check if player meets job requirements
                        boolean canApply = false;
                        if((target.getTitleRequirements() == null && target.getStatRequirements().size() == 0)) {
                            canApply = true;
                        } else {
                            // prep boolean trackers
                            boolean[] hasTitleRQs = new boolean[target.getTitleRequirements().length];
                            boolean hasTitleRQ = true;
                            boolean[] hasStatRQs = new boolean[target.getStatRequirements().size()];
                            boolean hasStatRQ = true;

                            // process title requirements
                            for(int t=0; t<target.getTitleRequirements().length; t++) {
                                for(int p=0; p<player.getTitles().size(); p++) {
                                    if(target.getTitleRequirements()[t].equals(player.getTitles().get(p))) {
                                        hasTitleRQs[t] = true;
                                        break;
                                    }
                                }
                            }
                            for(boolean b: hasTitleRQs)  {
                                if(!b) {
                                    hasTitleRQ = false;
                                    break;
                                }
                            }

                            // process stat requirements
                            String[] targetKeys = target.getStatRequirements().keySet().toArray(new String[0]);
                            String[] playerKeys = player.getStats().keySet().toArray(new String[0]);
                            for(int s=0; s<target.getStatRequirements().size(); s++) {
                                for(int p=0; p<player.getStats().size(); p++) {
                                    if(targetKeys[s].equals(playerKeys[p])) {
                                        if(target.getStatRequirements().get(playerKeys[p]).equals(player.getStats().get(playerKeys[p]))) {
                                            hasStatRQs[s] = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            for(boolean b: hasStatRQs) {
                                if(!b) {
                                    hasStatRQ = false;
                                    break;
                                }
                            }
                            // if they meet requirements then set canApply true
                            if(hasStatRQ && hasTitleRQ) {
                                canApply = true;
                            }
                        }

                        // process application
                        if(canApply) {
                            player.setJob(target, gameState.getCurrentDate());
                            console.appendText(String.format(
                                    "%s started working at %s as a %s",
                                    player.getFirstName(),
                                    player.getJob().getName(),
                                    player.getJob().getCurrentTitle()
                            ));
                        } else {
                            console.appendText("You didn't meet job requirements to apply!");
                        }
                        break;
                    }
                }
            }
            displayJobInfo();
        });
        GridPane.setConstraints(chooseNewJob, 0, 4);

        // add all nodes to the grid pane
        centerGridPane.getChildren().addAll(
                currentJobLbl,
                currentDaysWorkedLbl,
                vRegion,
                getJobLbl,
                jobOptions,
                chooseNewJob
        );
    }

    private void displayMainScreen() {
        // Set screen
        App.CURRENT_SCREEN = App.SCREEN.MAIN;

        // Clear topBar then setup.
        topBar.getChildren().clear();
        topBar.getChildren().addAll(moneyLbl, hRegion, currentDateLbl);
        updateDateLbl();
        updateMoneyLbl();

        // clear context rightBar then add context options
        rightBar.getChildren().clear();
        nextWeekBtn.setOnAction(ActionEvent -> onNextWeek());
        nextDayBtn.setOnAction(ActionEvent -> onNextDay());
        rightBar.getChildren().addAll(vRegion2, nextWeekBtn, nextDayBtn);

        // clear main leftBar then add 'static' options
        leftBar.getChildren().clear();
        playerInfoBtn.setText(String.format("%s Info", gameState.getPlayer().getFirstName()));
        playerInfoBtn.setOnAction(ActionEvent   -> displayPlayerInfo());
        jobInfoBtn.setOnAction(ActionEvent      -> displayJobInfo());
        mapInfoBtn.setOnAction(ActionEvent      -> displayMapInfo());
        clearConsoleBtn.setOnAction(ActionEvent -> console.clear());
        leftBar.getChildren().addAll(playerInfoBtn, jobInfoBtn, mapInfoBtn, vRegion3, clearConsoleBtn);

        // build centerGridPane area
        displayPlayerInfo();

        // display any relevant output in console
        console.clear();
        if(gameState.getCurrentDate().compareTo(GameState.DAY_ONE) == 0) {
            console.appendText(String.format(
                    "Welcome to your first day in SimLifeRPG!\n%s should probably get a job to cover rent.\n",
                    gameState.getPlayer().getFirstName()
            ));
        }
    }

    // Logical

    private void updateMoneyLbl() {
        moneyLbl.setText(String.format("Cash: $%d", gameState.getPlayer().getMoney()));
    }

    private void updateDateLbl() {
        LocalDate currentDate = gameState.getCurrentDate();
        currentDateLbl.setText(String.format(
                "Date: %d / %d / %d",
                currentDate.getDayOfMonth(),
                currentDate.getMonthValue(),
                currentDate.getYear()
        ));
    }

    public void clearAll() {
        centerGridPane.getChildren().clear();
        topBar.getChildren().clear();
        leftBar.getChildren().clear();
        rightBar.getChildren().clear();
    }

    // Time Progress

    public void onNextWeek() {
        console.clear();
        for(int i=0; i<7; i++) {
            if(gameState.getPlayer().getHealth() > 0) {
                onNextDay();
            }
        }
    }

    public void onNextDay() {
        // update currentDate and date labels
        updateDateLbl();
        updateMoneyLbl();

        // update entities for now this is just sending them home on the new day
        for(Entity e: Entity.entities) {
            if(e instanceof Character) {
                ((Character) e).update();
            }
        }

        // advance a day
        LocalDate currentDate = gameState.getCurrentDate();
        gameState.setCurrentDate(currentDate.plusDays(1));
        console.appendText(String.format(
                "%s: %s %d %d\n",
                currentDate.getDayOfWeek().name(),
                currentDate.getMonth().name(),
                currentDate.getDayOfMonth(),
                currentDate.getYear()
        ));

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
                console.appendText(String.format(
                        "%s worked at %s today \n",
                        player.getFirstName(),
                        player.getJob().getName()
                ));
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
                console.appendText(String.format(
                        "Made $%d from working at %s\n",
                        payout,
                        player.getJob().getName()
                ));

                // set the days paid out to equal the amount of days worked
                player.getJob().setDaysPaidOut(player.getJob().getDaysWorked());
                player.setMoney(player.getMoney() + payout);
                if(originalSalary > 0) {
                    console.appendText(String.format(
                            "%s got a raise of $%d per day!",
                            player.getFirstName(),
                            player.getJob().getSalary() - originalSalary
                    ));
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
                console.appendText(String.format(
                        "%s paid $%d in rent!\n",
                        player.getFirstName(),
                        rentCost
                ));
            } else {
                // maybe set rent higher based on 12 month rent distributed?
                int rentIncrease = (rentCost / rentPeriod) / 12;

                // couldn't afford rent
                console.appendText(String.format(
                        "%s couldn't pay rent! An increase of $%d per day has been added\n",
                        player.getFirstName(),
                        rentIncrease
                ));

                // pretty heavy penalty at the moment
                player.getHome().setRent(player.getHome().getRent() + rentIncrease);
                player.getHome().setMonthsUnpaid(player.getHome().getMonthsUnpaid() + 1);
                player.getHome().setTotalUnpaid(rentCost);
                console.appendText(String.format(
                        "%s haven't paid for %d months and owe a total of $%d\n",
                        player.getFirstName(),
                        player.getHome().getMonthsUnpaid(),
                        player.getHome().getTotalUnpaid()
                ));
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
                console.appendText(String.format(
                        "You died of starvation after %s days!\n",
                        player.getDaysWithoutFood()
                ));

                // setup game over screen, we leave console output
                clearAll();
                App.CURRENT_SCREEN = App.SCREEN.DEAD;
                rightBar.getChildren().add(newGameBtn);
            }
        }

        // Will decide what to show based on the current screen setting
        if(App.CURRENT_SCREEN == App.SCREEN.MAIN || App.CURRENT_SCREEN == App.SCREEN.PLAYER_INFO) {
            displayPlayerInfo();
        }
        if(App.CURRENT_SCREEN == App.SCREEN.JOB_INFO) {
            displayJobInfo();
        }
        if (App.CURRENT_SCREEN == App.SCREEN.MAP) {
            displayMapInfo();
        }
    }

    // static utility method for debugging entities.
    public static void dumpEntityData() {
        ArrayList<Entity> entities = Entity.entities;
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