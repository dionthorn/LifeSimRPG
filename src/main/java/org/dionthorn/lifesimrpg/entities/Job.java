package org.dionthorn.lifesimrpg.entities;

import org.dionthorn.lifesimrpg.FileOpUtil;
import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

/**
 * Job manages either a default Job or a Job loaded from .job files
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
    private final boolean isFromFile; // used to distinguish default jobs from jobs loaded from .job files

    /**
     * Will generate a default Job with isFromFile set to false,
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
     * Will load a Job from file targeting /Maps/{MapName}/Jobs/{JobName}.job
     * @param jobName String representing the jobName to target
     */
    public Job(String jobName) {
        super();
        this.isFromFile = true;
        this.name = jobName.split("\\.")[0];
        String[] fileLines = FileOpUtil.getFileLines(URI.create(FileOpUtil.MAP_JOBS_PATH + jobName));

        boolean TR = false, SR = false, TI = false, PA = false, DA = false; // 5 state machine
        for(String line: fileLines) {
            if(line.contains(":TITLE_REQUIREMENTS:")) {
                SR = TI = PA = DA = false;
                TR = true;
            } else if(line.contains(":STAT_REQUIREMENTS:")) {
                TR = TI = PA = DA = false;
                SR = true;
            } else if(line.contains(":TITLE:")) {
                SR = TR = PA = DA = false;
                TI = true;
            } else if(line.contains(":PAY:")) {
                SR = TR = TI = DA = false;
                PA = true;
            } else if(line.contains(":DAYS:")) {
                SR = TR = TI = PA = false;
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
                } else if(TI) {
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
     * Will determine the current rank of this Job based on the currentTitle
     * by checking against jobTitles
     * @return int representing the current 'rank' of this Job
     */
    public int getCurrentRank() {
        for (int i = 0; i < jobTitles.length; i++) {
            String title = jobTitles[i];
            if(currentTitle.equals(title)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Will set new values
     * for  dailyPayRate       and currentTitle
     * from titlesPay[newRank] and jobTitles[newRank] respectively
     * @param newRank int representing the new rank of this Job will set the dailyPayRate and currentTitle
     */
    public void rankUp(int newRank) {
        this.dailyPayRate = this.titlesPay[newRank];
        this.currentTitle = this.jobTitles[newRank];
    }

    /**
     * Will increase this Job daysWorked by 1
     */
    public void workDay() {
        this.daysWorked++;
    }

    // Qualified boolean

    /**
     * Will return a boolean representing whether this Job defines the provided dayOfWeek as a work day
     * @param dayOfWeek int representing the current day of week monday = 1
     * @return boolean representing whether this Job defines the provided dayOfWeek as a work day
     */
    public boolean isWorkDay(int dayOfWeek) {
        return Arrays.stream(this.workDays).anyMatch(i -> i == dayOfWeek);
    }

    // Pure getters and setters and boolean

    /**
     * Will return a boolean representing whether this Job is loaded from file
     * @return boolean representing whether this Job is loaded from file
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
     * Will return an int representing this Job dailyPayRate
     * @return int representing this Job dailyPayRate
     */
    public int getDailyPayRate() {
        return this.dailyPayRate;
    }

    /**
     * Will set this Job dailyPayRate to the provided int
     * @param dailyPayRate int representing the new value to set this Job dailyPayRate
     */
    public void setDailyPayRate(int dailyPayRate) {
        this.dailyPayRate = dailyPayRate;
    }

    /**
     * Will return an int representing this Job daysWorked
     * @return int representing this Job daysWorked
     */
    public int getDaysWorked() {
        return this.daysWorked;
    }

    /**
     * Will set this Job daysWorked to the provided int
     * @param daysWorked int representing the new value to set this Job daysWorked
     */
    public void setDaysWorked(int daysWorked) {
        this.daysWorked = daysWorked;
    }

    /**
     * Will return an int representing this Job daysPaidOut
     * @return int representing this Job daysPaidOut
     */
    public int getDaysPaidOut() {
        return this.daysPaidOut;
    }

    /**
     * Will return an int representing this Job yearsWorked
     * @return int representing this Job yearsWorked
     */
    public int getYearsWorked() {
        return this.yearsWorked;
    }

    /**
     * Will set this Job yearsWorked to the provided int
     * @param yearsWorked int representing the new value to set this Job yearsWorked
     */
    public void setYearsWorked(int yearsWorked) {
        this.yearsWorked = yearsWorked;
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

    /**
     * Will return a String[] representing this Job Titles where each String is a potential 'rank' or 'title'
     * @return String[] representing this Job Titles where each String is a potential 'rank' or 'title'
     */
    public String[] getJobTitles() {
        return jobTitles;
    }

}
