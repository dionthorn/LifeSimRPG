package org.dionthorn.lifesimrpg;

import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class Job extends Entity {

    private LocalDate yearDate;
    private int salary;
    private int daysWorked = 0;
    private int daysPaidOut = 0;
    private int yearsWorked = 0;
    private int[] titlesPay;
    private int[] workDays;
    private final HashMap<String, Double> statRequirements = new HashMap<>();
    private String[] titles;
    private String[] titleRequirements;
    private String currentTitle;
    private final String name;

    // used for unemployed job creation
    public Job(String name, int salary) {
        this.name = name;
        this.salary = salary;
        this.titleRequirements = new String[1];
        this.titleRequirements[0] = "none";
        this.titlesPay = new int[1];
        this.titles = new String[1];
        this.titles[0] = this.name;
        this.currentTitle = this.name;
        this.workDays = new int[7];
        for(int i=0; i<workDays.length; i++) {
            workDays[i] = i+1;
        }
    }

    // used for loading jobs from file
    public Job(String jobName) {
        this.name = jobName.split("\\.")[0];
        String[] fileLines;
        if(FileOpUtils.JRT) {
            fileLines = FileOpUtils.getFileLines(URI.create(FileOpUtils.jrtBaseURI + "Jobs/" + jobName));
        } else {
            fileLines = FileOpUtils.getFileLines(URI.create(getClass().getResource("/Jobs") + jobName));
        }
        boolean TR = false; // Title requirements - 5 state machine
        boolean SR = false; // Stat requirements
        boolean TI = false; // Title
        boolean PA = false; // Pay
        boolean DA = false; // Work Days
        for(String line: fileLines) {
            if(line.contains(":")) {
                if(line.contains("TITLE_REQUIREMENTS")) {
                    SR = false;
                    TI = false;
                    PA = false;
                    DA = false;
                    TR = true;
                } else if(line.contains("STAT_REQUIREMENTS")) {
                    SR = true;
                    TR = false;
                    TI = false;
                    PA = false;
                    DA = false;
                } else if(line.contains("TITLE")) {
                    SR = false;
                    TR = false;
                    PA = false;
                    DA = false;
                    TI = true;
                } else if(line.contains("PAY")) {
                    SR = false;
                    TR = false;
                    TI = false;
                    DA = false;
                    PA = true;
                } else if(line.contains("DAYS")) {
                    SR = false;
                    TR = false;
                    TI = false;
                    PA = false;
                    DA = true;
                }
            } else {
                if(TR) {
                    String[] temp = line.split(",");
                    if(!Objects.equals(temp[0], "")) {
                        titleRequirements = new String[temp.length];
                        System.arraycopy(temp, 0, titleRequirements, 0, titleRequirements.length);
                    }
                } else if(SR) {
                    String[] temp = line.split(",");
                    if(!Objects.equals(temp[0], "")) {
                        for(String statRequire: temp) {
                            String[] temp2 = statRequire.split(":");
                            statRequirements.put(temp2[0], Double.parseDouble(temp2[1]));
                        }
                    }
                } else if(TI) {
                    String[] temp = line.split(",");
                    titles = new String[temp.length];
                    System.arraycopy(temp, 0, titles, 0, titles.length);
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
        currentTitle = titles[0];
        salary = titlesPay[0];
    }

    // calculates pay for job
    public int payout(LocalDate currentDate) {
        int payout = (daysWorked - daysPaidOut) * salary;
        LocalDate oneYearAni = yearDate.plusYears(1);
        if(currentDate.isEqual(oneYearAni) || currentDate.isAfter(oneYearAni)) {
            int newRank = 0;
            for(int i=0; i<titles.length; i++) {
                if(titles[i].equals(currentTitle)) {
                    newRank = i + 1;
                }
            }
            if(newRank<titlesPay.length) {
                // new rank
                salary = titlesPay[newRank];
                currentTitle = titles[newRank];
            } else {
                // max rank so give raise
                salary = salary + (int)(0.5 * (salary * 1.1));
            }
            payout = (daysWorked - daysPaidOut) * salary;
            daysWorked = 0;
            daysPaidOut = 0;
            yearsWorked++;
            yearDate = oneYearAni;
        }
        return payout;
    }

    // getters and setters

    public LocalDate getYearDate() {
        return yearDate;
    }

    public String getName() {
        return name;
    }

    public int getSalary() {
        return salary;
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

    public HashMap<String, Double> getStatRequirements() {
        return statRequirements;
    }

    public String getCurrentTitle() {
        return currentTitle;
    }

    public String[] getTitles() {
        return titles;
    }

    public int[] getTitlesPay() {
        return titlesPay;
    }

    public int[] getWorkDays() {
        return workDays;
    }

    public boolean isWorkDay(int dayOfWeek) {
        return Arrays.stream(workDays).anyMatch(i -> i == dayOfWeek);
    }

    public void setYearDate(LocalDate yearDate) {
        this.yearDate = yearDate;
    }
}
