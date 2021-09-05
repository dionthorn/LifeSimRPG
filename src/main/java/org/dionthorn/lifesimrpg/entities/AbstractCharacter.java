package org.dionthorn.lifesimrpg.entities;
import org.dionthorn.lifesimrpg.records.Attributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The AbstractCharacter object will manage all entities that are inhabitants of a Place object,
 * has an associated Job object, manages relationships with other Characters,
 * has various attributes like health
 */
public abstract class AbstractCharacter extends AbstractEntity {

    // Reference lists for an AbstractCharacter
    protected final HashMap<Integer, Double> relationships = new HashMap<>();
    protected final HashMap<String, Double> stats = new HashMap<>();
    protected final ArrayList<String> titles = new ArrayList<>();
    protected final ArrayList<AbstractCharacter> talkedToToday = new ArrayList<>();

    // information variables
    protected String firstName;
    protected String lastName;
    protected LocalDate birthday;
    protected int daysWithoutFood = 0; // 23 days in a row will kill you if you start with max health
    protected int foodCostPerDay = 14; // $14 a day in food = $98 per week
    protected int money = 200;

    // attribute variables
    protected double health = 100.00; // HP
    protected Attributes attributes = new Attributes(5.0, 5.0, 5.0,
            5.0, 5.0, 5.0);

    // has a relationship object references
    protected Job job;
    protected Residence home;
    protected Place currentLocation;
    protected Course currentCourse; // can be null

    /**
     * Abstract constructor
     */
    protected AbstractCharacter() {
        super();
    }

    // logical methods

    /**
     * Will return a String representing the course title currently qualified for
     * @return String representing the course title currently qualified for
     */
    public String checkTitle() {
        String toReturn = "Not Qualified - " + this.currentCourse.getName();
        if(this.hasCourse() && this.getStats().size() > 0) {
            if(this.getStats().get(this.currentCourse.getStatName()) >= this.currentCourse.getCurrentTitleRequirement()) {
                if(this.currentCourse.getCourseLevel() > this.currentCourse.getTitles().length - 1) {
                    toReturn = this.currentCourse.getTitles()[this.currentCourse.getTitles().length - 1];
                } else {
                    toReturn = this.currentCourse.getTitles()[this.currentCourse.getCourseLevel()];
                    this.currentCourse.plusCourseLevel();
                }
            }
        }
        return toReturn;
    }

    /**
     * Will return an int value representing the amount paid out on this call,
     * Will check the daysWorked and daysPaidOut variables to determine pay along with dailyPayRate
     * Will change those variables as needed. On the oneYearAni of this job will also give a raise,
     * or raise title
     * @param currentDate String representing the current date used to calculate pay
     * @return int representing the amount paid out on this call
     */
    public int payout(LocalDate currentDate) {
        int payout = (this.job.getDaysWorked() - this.job.getDaysPaidOut()) * this.job.getDailyPayRate();
        LocalDate oneYearAni = this.job.getOneYearDateTracker().plusYears(1);
        if(currentDate.isEqual(oneYearAni) || currentDate.isAfter(oneYearAni)) {
            int newRank = 0;
            for(int i = 0; i< this.job.getJobTitles().length; i++) {
                if(this.job.getJobTitles()[i].equals(this.job.getCurrentTitle())) {
                    newRank = i + 1;
                }
            }
            if(newRank<this.job.getTitlesPay().length) {
                //check if the newRank is qualified check Job Title Requirements against Player Titles
                //does job have title requirements?
                if(this.job.getTitleRequirements().length == 1 && this.job.getTitleRequirements()[0].equals("")) {
                    // no title requirements
                    this.job.rankUp(newRank);
                } else {
                    if(this.job.getTitleRequirements()[job.getCurrentRank() + 1].equals("")) {
                        // if the next rank has no requirement then rank up
                        this.job.rankUp(newRank);
                    } else {
                        if(titles.size() != 0) {
                            String target = this.job.getTitleRequirements()[job.getCurrentRank() + 1];
                            if(titles.contains(target)) {
                                this.job.rankUp(newRank);
                            }
                        }
                    }
                }
            } else {
                // max rank so give raise
                this.job.setDailyPayRate(this.job.getDailyPayRate() + (int)(0.5 * (this.job.getDailyPayRate() * 0.5)));
            }
            payout = (this.job.getDaysWorked() - this.job.getDaysPaidOut()) * this.job.getDailyPayRate();
            this.job.setDaysWorked(0);
            this.job.setDaysPaidOut(0);
            this.job.setYearsWorked(this.job.getYearsWorked() + 1);
            this.job.setOneYearDateTracker(oneYearAni);
        }
        return payout;
    }

    /**
     * Relocates this AbstractCharacter to target Place and removes from previous Place
     * @param target the Place that this AbstractCharacter will move to
     */
    public void moveTo(Place target) {
        this.currentLocation.getCharacters().remove(this);
        this.currentLocation = target;
        this.currentLocation.getCharacters().add(this);
    }

    /**
     * Will send this AbstractCharacter home and clear their talkedToToday values
     * This is used in Engine.onNextDay()
     */
    public void update() {
        this.talkedToToday.clear();
        if(hasHome() && !isHome()) {
            goHome();
        }
    }

    /**
     * Calls this.moveTo(this.home) simply for code readability
     */
    public void goHome() {
        moveTo(this.home);
    }

    // boolean methods is/has

    /**
     * Will return the boolean value of if today is equal to birthday month and day of month values
     * @param today the LocalDate representing the currentDate in gameState
     * @return boolean representing if today Month and Day of Month values are equal to AbstractCharacter birthday values
     */
    public boolean isBirthday(LocalDate today) {
        return (today.getMonthValue() == this.birthday.getMonthValue()) && (today.getDayOfMonth() == this.birthday.getDayOfMonth());
    }

    /**
     * Will return the boolean value of if this character is at their assigned home
     * @return boolean representing if this character is at their assigned home
     */
    public boolean isHome() {
        return this.currentLocation == this.home;
    }

    /**
     * Will return the boolean value of if this character has a home assigned
     * @return boolean representing if this character has a home assigned
     */
    public boolean hasHome() {
        return this.home != null;
    }

    /**
     * Will return the boolean value of if this AbstractCharacter has a relationship with target
     * @param target the AbstractCharacter to test if this AbstractCharacter has a relationship with target
     * @return boolean representing if this AbstractCharacter has a relationship with target
     */
    public boolean hasRelationship(AbstractCharacter target) {
        return this.relationships.containsKey(target.getUID());
    }

    /**
     * Will return the boolean value of if this AbstractCharacter has talked to target today
     * @param target the AbstractCharacter to test if this AbstractCharacter has talked to today
     * @return boolean representing if this AbstractCharacter has talked to target today
     */
    public boolean hasTalkedToToday(AbstractCharacter target) {
        return this.talkedToToday.contains(target);
    }

    /**
     * Will return boolean if this character has statName
     * @param statName String representing the stat to check
     * @return boolean representing whether this character has statName
     */
    public boolean hasStat(String statName) {
        return this.stats.containsKey(statName);
    }

    /**
     * Will return a boolean representing if this character currentCourse is not null
     * @return boolean representing if this character currentCourse is not null
     */
    public boolean hasCourse() {
        return this.currentCourse != null;
    }

    /**
     * Will return a boolean if this character job is not null
     * @return boolean if this character job is not null
     */
    public boolean hasJob() {
        return this.job != null;
    }

    // special set methods we use 'add'

    /**
     * Will increase by value and/or establish a relationship with target AbstractCharacter
     * relationships are stored by the AbstractCharacter AbstractEntity UID (always unique)
     * and a double between 0 and 100 in a HashMap < Integer, Double > called AbstractCharacter.relationships
     * @param target AbstractCharacter representing who this AbstractCharacter is adding relationship with
     * @param value double representing the amount of relationship to increase
     */
    public void addRelationship(AbstractCharacter target, double value) {
        int targetUID = target.getUID();
        if(!this.hasRelationship(target)) {
            this.relationships.put(targetUID, value);
        } else {
            double relation = this.relationships.get(targetUID);
            this.relationships.put(targetUID, relation + value > 100 ? 100d : relation + value);
        }
        this.talkedToToday.add(target);
    }

    /**
     * Will add a new entry in the stats HashMap where < String , Double >
     *     And if the stat name given is NONE we ignore
     * @param statName String representing the stat name to add
     * @param value double representing the stat value to add
     */
    public void addStat(String statName, double value) {
        if(!statName.equals("NONE")) {
            boolean doesExist = this.stats.keySet().stream().anyMatch(test -> test.equals(statName));
            if(!doesExist) {
                this.stats.put(statName, value);
            } else {
                double toAdd = this.stats.get(statName);
                this.stats.put(statName, toAdd + value);
            }
        }
    }

    // Qualified getters and setters

    /**
     * Will return a double representing the value of statName or 0 if null
     * @param statName String representing the stat name
     * @return double representing the stat name value
     */
    public double getStat(String statName) {
        double toReturn = 0;
        if(this.hasStat(statName)) {
            toReturn += this.stats.get(statName);
        }
        return toReturn;
    }

    /**
     * Will set this AbstractCharacter job to a new Job and update that job start date
     * @param job Job representing the job this AbstractCharacter is gaining
     * @param currentDate LocalDate representing the job start date
     */
    public void setJob(Job job, LocalDate currentDate) {
        this.job = job;
        this.job.setOneYearDateTracker(currentDate);
    }

    /**
     * Will set this AbstractCharacter health to the provided double value but cap at max_health
     * @param health double to set this AbstractCharacter health to will cap at max_health
     */
    public void setHealth(double health) {
        this.health = Math.min(health, Attributes.MAX_ATTRIBUTE);
    }

    /**
     * Override AbstractEntity.getName for AbstractCharacter it will return
     * "{firstName} {lastName}" as a String when calling getName()
     * @return String formatted as "{firstName} {lastName}" of this AbstractCharacter
     */
    @Override
    public String getName() {
        return "%s %s".formatted(this.firstName, this.lastName);
    }

    // pure getters and setters, these do no qualifications just pure return or assignment

    /**
     * Will return a double representing the value of the relationship of this AbstractCharacter to target
     * @param target AbstractCharacter to get the relationship value out of this AbstractCharacter relationships HashMap
     * @return double representing the value of the relationship of this AbstractCharacter to target
     */
    public double getRelationship(AbstractCharacter target) {
        return this.relationships.get(target.getUID());
    }

    /**
     * Will return a String representing this AbstractCharacter first name
     * @return String representing this AbstractCharacter first name
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Will return a String representing this AbstractCharacter last name
     * @return String representing this AbstractCharacter last ame
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Will return an int representing how much money this character has
     * @return int representing how much money this character has
     */
    public int getMoney() {
        return this.money;
    }

    /**
     * Will set this AbstractCharacter money attribute to a new value of money
     * @param money int representing amount of money this character has now
     */
    public void setMoney(int money) {
        this.money = money;
    }

    /**
     * will return a Job representing this AbstractCharacter job
     * @return Job representing this AbstractCharacter job
     */
    public Job getJob() {
        return this.job;
    }

    /**
     * Will return a Residence representing this AbstractCharacter home
     * @return Residence representing this AbstractCharacter home
     */
    public Residence getHome() {
        return this.home;
    }

    /**
     * Will set this AbstractCharacter home attribute to the provided home
     * @param home Residence representing a new home for this AbstractCharacter
     */
    public void setHome(Residence home) {
        this.home = home;
    }

    /**
     * Will set the int value for this AbstractCharacter foodCostPerDay
     * @param foodCostPerDay int representing the daily money worth of food this AbstractCharacter eats
     */
    public void setFoodCostPerDay(int foodCostPerDay) {
        this.foodCostPerDay = foodCostPerDay;
    }

    /**
     * Will return an int representing this AbstractCharacter daily money worth of food eaten
     * @return int representing this AbstractCharacter daily money worth of food eaten
     */
    public int getFoodCostPerDay() {
        return this.foodCostPerDay;
    }

    /**
     * Will return an int representing the days this AbstractCharacter has gone without eating food
     * @return int representing the days this AbstractCharacter has gone without eating food
     */
    public int getDaysWithoutFood() {
        return this.daysWithoutFood;
    }

    /**
     * Will set this AbstractCharacter daysWithoutFood to the provided int value
     * @param daysWithoutFood int representing the new value of this AbstractCharacter daysWithoutFood
     */
    public void setDaysWithoutFood(int daysWithoutFood) {
        this.daysWithoutFood = daysWithoutFood;
    }

    /**
     * Will return a Place representing the current location of this AbstractCharacter
     * @return Place representing the current location of this AbstractCharacter
     */
    public Place getCurrentLocation() {
        return this.currentLocation;
    }

    /**
     * Will set this AbstractCharacter currentLocation to the provided Place value
     * @param currentLocation Place representing the new location of this AbstractCharacter
     */
    public void setCurrentLocation(Place currentLocation) {
        this.currentLocation = currentLocation;
    }

    /**
     * Will return an ArrayList representing the titles this character has
     * @return ArrayList representing the titles this character has
     */
    public ArrayList<String> getTitles() {
        return this.titles;
    }

    /**
     * Will return a HashMap representing the stats this character has
     * @return HashMap representing the stats this character has
     */
    public HashMap<String, Double> getStats() {
        return this.stats;
    }

    /**
     * Will return a Course representing this characters current course
     * @return Course representing this characters current course
     */
    public Course getCurrentCourse() {
        return this.currentCourse;
    }

    /**
     * Will return a Course representing the new Course this character will use for currentCourse
     * @param currentCourse Course representing the new Course this character will use for currentCourse
     */
    public void setCurrentCourse(Course currentCourse) {
        this.currentCourse = currentCourse;
    }

    /**
     * Will return a double representing this character health
     * @return double representing this character health
     */
    public double getHealth() {
        return this.health;
    }

    public Attributes getAttributes() {
        return attributes;
    }

}
