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
            workDays[i] = i;
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
                    titleRequirements = new String[temp.length];
                    System.arraycopy(temp, 0, titleRequirements, 0, temp.length);
                } else if(SR) {
                    String[] temp = line.split(",");
                    statRQNames = new String[temp.length];
                    statRQValues = new double[temp.length];
                    if(!Objects.equals(temp[0], "")) {
                        for (int i = 0; i < temp.length; i++) {
                            String statRequire = temp[i];
                            String[] temp2 = statRequire.split(":");
                            statRQNames[i] = temp2[0];
                            statRQValues[i] = Double.parseDouble(temp2[1]);
                        }
                    }
                } else if(JT) {
                    String[] temp = line.split(",");
                    jobTitles = new String[temp.length];
                    System.arraycopy(temp, 0, jobTitles, 0, temp.length);
                } else if(PA) {
                    String[] temp = line.split(",");
                    titlesPay = new int[temp.length];
                    for(int i=0; i<titlesPay.length; i++) {
                        titlesPay[i] = Integer.parseInt(temp[i]);
                    }
                } else if(DA) {
                    String[] temp = line.split(",");
                    workDays = new int[temp.length];
                    for(int i=0; i<workDays.length; i++) {
                        workDays[i] = Integer.parseInt(temp[i]);
                    }
                }
            }
        }
        currentTitle = jobTitles[0];
        dailyPayRate = titlesPay[0];
    }

    // calculates pay for job

    /**
     * Will return an int value representing the amount paid out on this call,
     * Will check the daysWorked and daysPaidOut variables to determine pay along with dailyPayRate
     * Will change those variables as needed. On the oneYearAni of this job will also give a raise,
     * or raise title
     * @param currentDate String representing the current date used to calculate pay
     * @return int representing the amount paid out on this call
     */
    public int payout(LocalDate currentDate) {
        int payout = (daysWorked - daysPaidOut) * dailyPayRate;
        LocalDate oneYearAni = oneYearDateTracker.plusYears(1);
        if(currentDate.isEqual(oneYearAni) || currentDate.isAfter(oneYearAni)) {
            int newRank = 0;
            for(int i = 0; i< jobTitles.length; i++) {
                if(jobTitles[i].equals(currentTitle)) {
                    newRank = i + 1;
                }
            }
            if(newRank<titlesPay.length) {
                // new rank
                dailyPayRate = titlesPay[newRank];
                currentTitle = jobTitles[newRank];
            } else {
                // max rank so give raise
                dailyPayRate = dailyPayRate + (int)(0.5 * (dailyPayRate * 1.1));
            }
            payout = (daysWorked - daysPaidOut) * dailyPayRate;
            daysWorked = 0;
            daysPaidOut = 0;
            yearsWorked++;
            oneYearDateTracker = oneYearAni;
        }
        return payout;
    }

    // logical

    /**
     * Will return a boolean representing whether this job defines the provided dayOfWeek as a work day
     * @param dayOfWeek int representing the current day of week monday = 1
     * @return boolean representing whether this job defines the provided dayOfWeek as a work day
     */
    public boolean isWorkDay(int dayOfWeek) {
        return Arrays.stream(workDays).anyMatch(i -> i == dayOfWeek);
    }

    /**
     * Will return a boolean representing whether this job is loaded from file
     * @return boolean representing whether this job is loaded from file
     */
    public boolean isFromFile() {
        return this.isFromFile;
    }

    // getters and setters

    /**
     * Will return a LocalDate representing the date you started, or the last date you raised title
     * @return LocalDate representing the date you started, or the last date you raised title
     */
    public LocalDate getOneYearDateTracker() {
        return oneYearDateTracker;
    }

    public void setOneYearDateTracker(LocalDate initialDate) {
        this.oneYearDateTracker = initialDate;
    }

    public String getName() {
        return name;
    }

    public int getDailyPayRate() {
        return dailyPayRate;
    }

    public void workDay() {
        daysWorked++;
    }

    public int getDaysWorked() {
        return daysWorked;
    }

    public void setDaysPaidOut(int daysPaidOut) {
        this.daysPaidOut = daysPaidOut;
    }

    public int getYearsWorked() {
        return yearsWorked;
    }

    public String[] getTitleRequirements() {
        return titleRequirements;
    }

    public String[] getStatRQNames() {
        return statRQNames;
    }

    public double[] getStatRQValues() {
        return statRQValues;
    }

    public String getCurrentTitle() {
        return currentTitle;
    }

    public int[] getTitlesPay() {
        return titlesPay;
    }

    public int[] getWorkDays() {
        return workDays;
    }

}
