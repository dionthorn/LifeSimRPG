package org.dionthorn.lifesimrpg.entities;

import org.dionthorn.lifesimrpg.FileOpUtil;
import java.net.URI;

/**
 * Course will manage .course data
 */
public class Course extends AbstractEntity {

    // Course variables
    private final String courseName;
    private int courseLevel = 0;
    private String[] titles;
    private String statName;
    private double[] titleRequirements;
    private double[] statGains;

    /**
     * Course constructor will locate the Course data in /Maps/{mapName}/Course/{courseName}.course
     * @param courseName String representing this Course name
     */
    public Course(String courseName) {
        super();

        // set name and check JRT for fileLines
        this.courseName = courseName.split("\\.")[0];
        String[] fileLines = FileOpUtil.getFileLines(URI.create(FileOpUtil.MAP_COURSES_PATH + courseName));

        // loop through all file lines and process using a 3 state machine
        boolean TI = false, RQ = false, SG = false;
        for(String line: fileLines) {
            if(line.contains(":TITLES:")) {
                RQ = SG = false;
                TI = true;
            } else if(line.contains(":REQUIREMENTS:")) {
                TI = SG = false;
                RQ = true;
            } else if(line.contains(":STAT_GAIN:")) {
                TI = RQ = false;
                SG = true;
            } else if(TI) {
                this.titles = line.split(",");
            } else if(RQ) {
                String[] byTitle = line.split(",");
                this.titleRequirements = new double[byTitle.length];
                for(int i = 0; i < byTitle.length; i++) {
                    String req = byTitle[i];
                    String[] keyValue = req.split(":");
                    this.statName = keyValue[0];
                    this.titleRequirements[i] = Double.parseDouble(keyValue[1]);
                }
            } else if(SG) {
                String[] byTitle = line.split(",");
                this.statGains = new double[byTitle.length];
                for(int i = 0; i < byTitle.length; i++) {
                    String statG = byTitle[i];
                    String[] keyValue = statG.split(":");
                    this.statName = keyValue[0];
                    this.statGains[i] = Double.parseDouble(keyValue[1]);
                }
            }
        }
    }

    // Logical methods

    /**
     * Will increase this Course courseLevel by 1
     */
    public void plusCourseLevel() {
        this.courseLevel++;
    }

    // Qualified getters

    /**
     * Will return a double representing the current title requirement or the max if at max
     * @return double representing the current title requirement or the max if at max
     */
    public double getCurrentTitleRequirement() {
        if(this.courseLevel > this.titleRequirements.length - 1) {
            return this.titleRequirements[this.titleRequirements.length - 1];
        }
        return this.titleRequirements[this.courseLevel];
    }

    /**
     * Will return a double representing the current title statGain or the max if at max
     * @return double representing the current title statGain or the max if at max
     */
    public double getCurrentStatGain() {
        if(this.courseLevel > this.statGains.length - 1) {
            return this.statGains[this.statGains.length - 1];
        }
        return this.statGains[this.courseLevel];
    }

    // Pure getters

    /**
     * Will return a String representing this Course courseName
     * @return String representing this Course courseName
     */
    public String getCourseName() {
        return this.courseName;
    }

    /**
     * Will return a String representing this Course required statName
     * @return String representing this Course required statName
     */
    public String getStatName() {
        return this.statName;
    }

    /**
     * Will return an int representing this Course courseLevel
     * @return int representing this Course courseLevel
     */
    public int getCourseLevel() {
        return this.courseLevel;
    }

    /**
     * Will return a double[] representing this Course title requirement value for all titles
     * @return double[] representing this Course title requirement value for all titles
     */
    public double[] getTitleRequirements() {
        return this.titleRequirements;
    }

    /**
     * Will return a String[] representing this Course titles
     * @return String[] representing this Course titles
     */
    public String[] getTitles() {
        return this.titles;
    }

}
