package org.dionthorn.lifesimrpg.entities;

import org.dionthorn.lifesimrpg.FileOpUtil;
import java.net.URI;

/**
 * Course will manage .course data
 */
public class Course extends AbstractEntity {

    public String courseName;
    public int courseLevel = 0;
    public String[] titles;
    public String statName;
    public double[] titleRequirements;
    public double[] statGains;

    /**
     * Course constructor will locate the Course data in /Maps/{mapName}/Course/{courseName}.course
     * @param courseName String representing this Course name
     * @param mapName String representing this Course respective map folder
     */
    public Course(String courseName, String mapName) {
        super();

        // set name and check JRT for fileLines
        this.courseName = courseName.split("\\.")[0];
        String[] fileLines;
        if(FileOpUtil.JRT) {
            fileLines = FileOpUtil.getFileLines(URI.create(FileOpUtil.jrtBaseURI + "Maps/" + mapName + "/Courses/" + courseName));
        } else {
            fileLines = FileOpUtil.getFileLines(URI.create(getClass().getResource("/Maps/" + mapName + "/Courses") + courseName));
        }

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
                titles = line.split(",");
            } else if(RQ) {
                String[] byTitle = line.split(",");
                titleRequirements = new double[byTitle.length];
                for(int i = 0; i < byTitle.length; i++) {
                    String req = byTitle[i];
                    String[] keyValue = req.split(":");
                    statName = keyValue[0];
                    titleRequirements[i] = Double.parseDouble(keyValue[1]);
                }
            } else if(SG) {
                String[] byTitle = line.split(",");
                statGains = new double[byTitle.length];
                for(int i = 0; i < byTitle.length; i++) {
                    String statG = byTitle[i];
                    String[] keyValue = statG.split(":");
                    statName = keyValue[0];
                    statGains[i] = Double.parseDouble(keyValue[1]);
                }
            }
        }
    }

    // logical

    public void plusCourseLevel() {
        courseLevel++;
    }

    // getters and setters

    public String getCourseName() {
        return courseName;
    }

    public String getStatName() {
        return statName;
    }

    public double getCurrentTitleRequirement() {
        if(courseLevel > titleRequirements.length - 1) {
            return titleRequirements[titleRequirements.length - 1];
        }
        return titleRequirements[courseLevel];
    }

    public double getCurrentStatGain() {
        if(courseLevel > statGains.length - 1) {
            return statGains[statGains.length - 1];
        }
        return statGains[courseLevel];
    }

    public int getCourseLevel() {
        return courseLevel;
    }

    public double[] getTitleRequirements() {
        return titleRequirements;
    }

    public String[] getTitles() {
        return titles;
    }

}
