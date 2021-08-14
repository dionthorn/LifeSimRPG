package org.dionthorn.lifesimrpg;

import java.net.URI;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * The Character object will manage all entities that are inhabitants of a Place object,
 * has an associated Job object, manages relationships with other Characters,
 * has various attributes like health, money, foodCost, daysWithoutFood.
 */
public class Character extends Entity {

    // static used for storing firstNames/lastNames for AI and the random object
    private static final ArrayList<String> firstNames = new ArrayList<>();
    private static final ArrayList<String> lastNames = new ArrayList<>();
    private static final Random rand = new Random();

    // Attributes of a Character
    private final HashMap<Integer, Double> relationships = new HashMap<>();
    private final HashMap<String, Double> stats = new HashMap<>();
    private final ArrayList<String> titles = new ArrayList<>();
    private final ArrayList<Character> talkedToToday = new ArrayList<>();
    private final String firstName;
    private final String lastName;
    private final LocalDate birthday;
    private final double maxHealth = 100.00;
    private double health = 100.00;
    private int money = 200;
    private int foodCost = 14; // $14 a day in food = $98 per week
    private int daysWithoutFood = 0; // 23 days in a row will kill you if you start with max health
    private Job job;
    private Residence home;
    private Place currentLocation;

    /**
     * AI constructor will use random information to generate a Character object
     */
    public Character() {
        super();
        this.birthday = getRandomDate();
        this.firstName = getRandomFirstName();
        this.lastName = getRandomLastName();
        this.job = new Job("Salary Person", 70); // temporary
    }

    /**
     * Player constructor used to generate the starting Character object
     * @param birthday the Characters LocalDate reference to their birthday date
     * @param firstName the String representing the Characters first name
     * @param lastName the String representing the Characters last name
     */
    public Character(LocalDate birthday, String firstName, String lastName) {
        super();
        this.birthday = birthday;
        this.firstName = firstName;
        this.lastName = lastName;
        this.job = new Job("Unemployed", 0);
    }

    // logical and boolean methods

    /**
     * Calls moveTo(this.home)
     */
    public void goHome() {
        moveTo(home);
    }

    /**
     * Relocates this Character to target Place
     * @param target the Place that this Character will move to
     */
    public void moveTo(Place target) {
        getCurrentLocation().getCharacters().remove(this);
        currentLocation = target;
        target.getCharacters().add(this);
    }

    /**
     * Will randomly decide a Place to move this Character to
     * from among the currentLocation connections reference
     */
    public void moveRandom() {
        int coinFlip = rand.nextInt(2); // 50%ish
        if(coinFlip > 0) {
            int cap = getCurrentLocation().getConnections().size();
            Place choice = getCurrentLocation().getConnections().get(rand.nextInt(cap));
            moveTo(choice);
        }
    }

    /**
     * Will send this Character home and clear their talkedToToday values
     * This is used in Engine.onNextDay()
     */
    public void update() {
        talkedToToday.clear();
        if(hasHome() && !isHome()) {
            goHome();
        }
    }

    /**
     * Will return the boolean value of if today is equal to birthday month and day of month values
     * @param today the LocalDate representing the currentDate in gameState
     * @return boolean representing if today Month and Day of Month values are equal to Character birthday values
     */
    public boolean isBirthday(LocalDate today) {
        return (today.getMonthValue() == birthday.getMonthValue()) && (today.getDayOfMonth() == birthday.getDayOfMonth());
    }

    /**
     * Will return the boolean value of if this character is at their assigned home
     * @return boolean representing if this character is at their assigned home
     */
    public boolean isHome() {
        return currentLocation == home;
    }

    /**
     * Will return the boolean value of if this character has a home assigned
     * @return boolean representing if this character has a home assigned
     */
    public boolean hasHome() {
        return home != null;
    }

    /**
     * Will return the boolean value of if this Character has a relationship with target
     * @param target the Character to test if this Character has a relationship with target
     * @return boolean representing if this Character has a relationship with target
     */
    public boolean hasRelationship(Character target) {
        return relationships.containsKey(target.getUID());
    }

    /**
     * Will return the boolean value of if this Character has talked to target today
     * @param target the Character to test if this Character has talked to today
     * @return boolean representing if this Character has talked to target today
     */
    public boolean hasTalkedToToday(Character target) {
        return talkedToToday.contains(target);
    }

    // static random methods

    /**
     * Will return a LocalDate representing a random valid Date between 1/1/1960 and gameState.AGE_CAP
     * @return LocalDate representing a random valid Date between 1/1/1960 and gameState.AGE_CAP
     */
    public LocalDate getRandomDate() {
        LocalDate start = LocalDate.of(1960, Month.JANUARY, 1);
        long days = ChronoUnit.DAYS.between(start, GameState.AGE_CAP);
        return start.plusDays(rand.nextInt((int) days + 1));
    }

    /**
     * Will return a String representing a random first name pulled from resources/AI/firstName.dat
     * @return String representing a random first name pulled from resources/AI/firstName.dat
     */
    public String getRandomFirstName() {
        if(firstNames.size() == 0) {
            String[] names;
            if(FileOpUtils.JRT) {
                names = FileOpUtils.getFileLines(URI.create(FileOpUtils.jrtBaseURI + "AI/" + "firstName.dat"));
            } else {
                names = FileOpUtils.getFileLines(URI.create(getClass().getResource("/AI") + "firstName.dat"));
            }
            Collections.addAll(firstNames, names);
        }
        return firstNames.get(rand.nextInt(firstNames.size()));
    }

    /**
     * Will return a String representing a random last name pulled from resources/AI/lastName.dat
     * @return String representing a random last name pulled from resources/AI/lastName.dat
     */
    public String getRandomLastName() {
        if(lastNames.size() == 0) {
            String[] names;
            if(FileOpUtils.JRT) {
                names = FileOpUtils.getFileLines(URI.create(FileOpUtils.jrtBaseURI + "AI/" + "lastName.dat"));
            } else {
                names = FileOpUtils.getFileLines(URI.create(getClass().getResource("/AI") + "lastName.dat"));
            }
            Collections.addAll(lastNames, names);
        }
        return lastNames.get(rand.nextInt(lastNames.size()));
    }

    // getters and setters

    /**
     * Will increase by value and/or establish a relationship with target Character
     * relationships are stored by the Character Entity UID (always unique)
     * and a double between 0 and 100 in a HashMap < Integer, Double > called Character.relationships
     * @param target Character representing who this Character is adding relationship with
     * @param value double representing the amount of relationship to increase
     */
    public void addRelationship(Character target, double value) {
        int targetUID = target.getUID();
        if(!hasRelationship(target)) {
            relationships.put(targetUID, value);
        } else {
            double relation = relationships.get(targetUID);
            if(relation + value > 100) {
                relationships.put(targetUID, 100d);
            } else {
                relationships.put(targetUID, relation + value);
            }
        }
        talkedToToday.add(target);
    }

    /**
     * Will return a double representing the value of the relationship of this Character to target
     * @param target Character to get the relationship value out of this Character.relationships HashMap
     * @return double representing the value of the relationship of this Character to target
     */
    public double getRelationship(Character target) {
        int targetUID = target.getUID();
        return relationships.get(targetUID);
    }

    public void addStat(String statName, double value) {
        boolean doesExist = false;
        for(String test: stats.keySet()) {
            if(test.equals(statName)) {
                doesExist = true;
                break;
            }
        }
        if(!doesExist) {
            stats.put(statName, value);
        } else {
            double toAdd = stats.get(statName);
            stats.put(statName, toAdd + value);
        }
    }

    public double getStat(String statName) {
        double toReturn = 0;
        if(stats.get(statName) != null) {
            toReturn += stats.get(statName);
        }
        return toReturn;
    }

    /**
     * Will return a String representing this Character first name
     * @return String representing this Character first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Will return a String representing this Character last name
     * @return String representing this Character last ame
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Will return an int representing how much money this character has
     * @return int representing how much money this character has
     */
    public int getMoney() {
        return money;
    }

    /**
     * Will set this Character money attribute to a new value of money
     * @param money int representing amount of money this character has now
     */
    public void setMoney(int money) {
        this.money = money;
    }

    /**
     * will return a Job representing this Character job
     * @return Job representing this Character job
     */
    public Job getJob() {
        return job;
    }

    /**
     * Will set this Character job to a new Job and update that job start date
     * @param job Job representing the job this Character is gaining
     * @param currentDate LocalDate representing the job start date
     */
    public void setJob(Job job, LocalDate currentDate) {
        this.job = job;
        job.setYearDate(currentDate);
    }

    /**
     * Will return a Residence representing this Character home
     * @return Residence representing this Character home
     */
    public Residence getHome() {
        return home;
    }

    /**
     * Will set this Character home attribute to the provided home
     * @param home Residence representing a new home for this Character
     */
    public void setHome(Residence home) {
        this.home = home;
    }

    /**
     * Will set the int value for this Character foodCost
     * @param foodCost int representing the daily money worth of food this Character eats
     */
    public void setFoodCost(int foodCost) {
        this.foodCost = foodCost;
    }

    /**
     * Will return an int representing this Character daily money worth of food eaten
     * @return int representing this Character daily money worth of food eaten
     */
    public int getFoodCost() {
        return foodCost;
    }

    /**
     * Will return a double representing this character health
     * @return double representing this character health
     */
    public double getHealth() {
        return health;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    /**
     * Will set this Character health to the provided double value but cap at max_health
     * @param health double to set this Character health to will cap at max_health
     */
    public void setHealth(double health) {
        this.health = Math.min(health, maxHealth);
    }

    /**
     * Will return an int representing the days this Character has gone without eating food
     * @return int representing the days this Character has gone without eating food
     */
    public int getDaysWithoutFood() {
        return daysWithoutFood;
    }

    /**
     * Will set this Character daysWithoutFood to the provided int value
     * @param daysWithoutFood int representing the new value of this Character daysWithoutFood
     */
    public void setDaysWithoutFood(int daysWithoutFood) {
        this.daysWithoutFood = daysWithoutFood;
    }

    /**
     * Will return a Place representing the current location of this Character
     * @return Place representing the current location of this Character
     */
    public Place getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Will set this Character currentLocation to the provided Place value
     * @param currentLocation Place representing the new location of this Character
     */
    public void setCurrentLocation(Place currentLocation) {
        this.currentLocation = currentLocation;
    }

    public ArrayList<String> getTitles() {
        return titles;
    }

    public HashMap<String, Double> getStats() {
        return stats;
    }
}
