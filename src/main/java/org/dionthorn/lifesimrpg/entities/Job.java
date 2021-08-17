package org.dionthorn.lifesimrpg.entities;

import org.dionthorn.lifesimrpg.FileOpUtil;
import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

/**
 * Job manages either a default job or a job loaded from .job files
 * ex: /Maps/{MapName}/Jobs/{JobName}.job
 */
public class Job extends AbstractEntity {

    // Job information variables
    private LocalDate oneYearDateTracker;
    private int dailyPayRate;
    private int daysWorked = 0;
    private int daysPaidOut = 0;
    private int yearsWorked = 0;
    private int[] titlesPay;
    private int[] workDays;
    private String[] statRQNames;
    private double[] statRQValues;
    private String[] jobTitles;
    private String[] titleRequirements;
    private String currentTitle;
    private final String name;
    private final boolean isFromFile; // used to distinguish default jobs from jobs loaded from .job files

    /**
     * Will generate a default job with isFromFile set to false,
     * used for salary person and unemployed default jobs.
     * @param name String representing the default name
     * @param dailyPayRate int representing the default pay rate
     */
    public Job(String name, int dailyPayRate) {
        super();
        this.isFromFile = false;
        this.name = name;
        this.dailyPayRate = dailyPayRate;
        this.titleRequirements = new String[1];
        this.titleRequirements[0] = "";
        this.titlesPay = new int[1];
        this.jobTitles = new String[1];
        this.jobTitles[0] = this.name;
        this.currentTitle = this.name;
        this.workDays = new int[5];
        for(int i=0; i<5; i++) {
            this.workDays[i] = i;
        }
    }

    /**
     * Will load a job from file targeting /Maps/{MapName}/Jobs/{JobName}.job
     * @param jobName String representing the jobName to target
     * @param mapName String representing the mapName to target
     */
    public Job(String jobName, String mapName) {
        super();
        this.isFromFile = true;
        this.name = jobName.split("\\.")[0];
        String[] fileLines;
        if(FileOpUtil.JRT) {
            fileLines = FileOpUtil.getFileLines(URI.create(FileOpUtil.jrtBaseURI + "Maps/" + mapName + "/Jobs/" + jobName));
        } else {
            fileLines = FileOpUtil.getFileLines(URI.create(getClass().getResource("/Maps/" + mapName + "/Jobs") + jobName));
        }
        boolean TR = false, SR = false, JT = false, PA = false, DA = false; // 5 state machine
        for(String line: fileLines) {
            if(line.contains(":TITLE_REQUIREMENTS:")) {
                SR = JT = PA = DA = false;
                TR = true;
            } else if(line.contains(":STAT_REQUIREMENTS:")) {
                TR = JT = PA = DA = false;
                SR = true;
            } else if(line.contains(":TITLE:")) {
                SR = TR = PA = DA = false;
                JT = true;
            } else if(line.contains(":PAY:")) {
                SR = TR = JT = DA = false;
                PA = true;
            } else if(line.contains(":DAYS:")) {
                SR = TR = JT = PA = false;
                DA = true;
            } else {
                if(TR) {
                    String[] temp = line.split(",");
                    this.titleRequirements = new String[temp.length];
                    System.arraycopy(temp, 0, this.titleRequirements, 0, temp.length);
                } else if(SR) {
                    String[] temp = line.split(",");
                    this.statRQNames = new String[temp.length];
                    this.statRQValues = new double[temp.length];
                    if(!Objects.equals(temp[0], "")) {
                        for (int i = 0; i < temp.length; i++) {
                            String statRequire = temp[i];
                            String[] temp2 = statRequire.split(":");
                            this.statRQNames[i] = temp2[0];
                            this.statRQValues[i] = Double.parseDouble(temp2[1]);
                        }
                    }
                } else if(JT) {
                    String[] temp = line.split(",");
                    this.jobTitles = new String[temp.length];
                    System.arraycopy(temp, 0, this.jobTitles, 0, temp.length);
                } else if(PA) {
                    String[] temp = line.split(",");
                    this.titlesPay = new int[temp.length];
                    for(int i=0; i<this.titlesPay.length; i++) {
                        this.titlesPay[i] = Integer.parseInt(temp[i]);
                    }
                } else if(DA) {
                    String[] temp = line.split(",");
                    this.workDays = new int[temp.length];
                    for(int i=0; i<this.workDays.length; i++) {
                        this.workDays[i] = Integer.parseInt(temp[i]);
                    }
                }
            }
        }
        this.currentTitle = this.jobTitles[0];
        this.dailyPayRate = this.titlesPay[0];
    }

    // Logical

    /**
     * Will return an int value representing the amount paid out on this call,
     * Will check the daysWorked and daysPaidOut variables to determine pay along with dailyPayRate
     * Will change those variables as needed. On the oneYearAni of this job will also give a raise,
     * or raise title
     * @param currentDate String representing the current date used to calculate pay
     * @return int representing the amount paid out on this call
     */
    public int payout(LocalDate currentDate) {
        int payout = (this.daysWorked - this.daysPaidOut) * this.dailyPayRate;
        LocalDate oneYearAni = this.oneYearDateTracker.plusYears(1);
        if(currentDate.isEqual(oneYearAni) || currentDate.isAfter(oneYearAni)) {
            int newRank = 0;
            for(int i = 0; i< this.jobTitles.length; i++) {
                if(this.jobTitles[i].equals(this.currentTitle)) {
                    newRank = i + 1;
                }
            }
            if(newRank<this.titlesPay.length) {
                // new rank
                this.dailyPayRate = this.titlesPay[newRank];
                this.currentTitle = this.jobTitles[newRank];
            } else {
                // max rank so give raise
                this.dailyPayRate = this.dailyPayRate + (int)(0.5 * (this.dailyPayRate * 1.1));
            }
            payout = (this.daysWorked - this.daysPaidOut) * this.dailyPayRate;
            this.daysWorked = 0;
            this.daysPaidOut = 0;
            this.yearsWorked++;
            this.oneYearDateTracker = oneYearAni;
        }
        return payout;
    }

    /**
     * Will increase this Job daysWorked by 1
     */
    public void workDay() {
        this.daysWorked++;
    }

    // Qualified boolean

    /**
     * Will return a boolean representing whether this job defines the provided dayOfWeek as a work day
     * @param dayOfWeek int representing the current day of week monday = 1
     * @return boolean representing whether this job defines the provided dayOfWeek as a work day
     */
    public boolean isWorkDay(int dayOfWeek) {
        return Arrays.stream(this.workDays).anyMatch(i -> i == dayOfWeek);
    }

    // Pure getters and setters and boolean

    /**
     * Will return a boolean representing whether this job is loaded from file
     * @return boolean representing whether this job is loaded from file
     */
    public boolean isFromFile() {
        return this.isFromFile;
    }

    /**
     * Will return a LocalDate representing the date you started, or the last date you raised title
     * @return LocalDate representing the date you started, or the last date you raised title
     */
    public LocalDate getOneYearDateTracker() {
        return this.oneYearDateTracker;
    }

    /**
     * Will set this Job oneYearDateTracker to initialDate
     * @param initialDate LocalDate representing the initialDate to set this Job oneYearDateTracker to
     */
    public void setOneYearDateTracker(LocalDate initialDate) {
        this.oneYearDateTracker = initialDate;
    }

    /**
     * Will set this Job daysPaidOut to the provided int
     * @param daysPaidOut int representing the new value to set this Job daysPaidOut
     */
    public void setDaysPaidOut(int daysPaidOut) {
        this.daysPaidOut = daysPaidOut;
    }

    /**
     * Will return a String representing this Job name
     * @return String representing this Job name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Will return an int representing this Job dailyPayRate
     * @return int representing this Job dailyPayRate
     */
    public int getDailyPayRate() {
        return this.dailyPayRate;
    }

    /**
     * Will return an int representing this Job daysWorked
     * @return int representing this Job daysWorked
     */
    public int getDaysWorked() {
        return this.daysWorked;
    }

    /**
     * Will return an int representing this Job yearsWorked
     * @return int representing this Job yearsWorked
     */
    public int getYearsWorked() {
        return this.yearsWorked;
    }

    /**
     * Will return a String[] representing this Job titleRequirements
     * @return String[] representing this Job titleRequirements
     */
    public String[] getTitleRequirements() {
        return this.titleRequirements;
    }

    /**
     * Will return a String[] representing this Job statRQNames for each title
     * @return String[] representing this Job statRQNames for each title
     */
    public String[] getStatRQNames() {
        return this.statRQNames;
    }

    /**
     * Will return a double[] representing this Job statRQValues for each title
     * @return double[] representing this Job statRQValues for each title
     */
    public double[] getStatRQValues() {
        return this.statRQValues;
    }

    /**
     * Will return a String representing this Job currentTitle
     * @return String representing this Job currentTitle
     */
    public String getCurrentTitle() {
        return this.currentTitle;
    }

    /**
     * Will return an int[] representing this Job titlesPay for each title
     * @return int[] representing this Job titlesPay for each title
     */
    public int[] getTitlesPay() {
        return this.titlesPay;
    }

    /**
     * Will return an int[] representing this Job workDays where 1=Monday and the length is the days worked in a week
     * @return int[] representing this Job workDays where 1=Monday and the length is the days worked in a week
     */
    public int[] getWorkDays() {
        return this.workDays;
    }

}
