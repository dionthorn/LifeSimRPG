package org.dionthorn.lifesimrpg.entities;

import org.dionthorn.lifesimrpg.FileOpUtils;

import java.net.URI;

public class Course extends Entity {

    public String courseName;
    public int courseLevel = 0;
    public String[] titles;
    public String statName;
    public double[] titleRequirements;
    public double[] statGains;

    public Course(String courseName) {
        super();
        this.courseName = courseName.split("\\.")[0];
        String[] fileLines;
        if(FileOpUtils.JRT) {
            fileLines = FileOpUtils.getFileLines(URI.create(FileOpUtils.jrtBaseURI + "Maps/Vanillaton/Courses/" + courseName));
        } else {
            fileLines = FileOpUtils.getFileLines(URI.create(getClass().getResource("/Maps/Vanillaton/Courses") + courseName));
        }

        // loop through all file lines and process
        boolean TI = false;
        boolean RQ = false;
        boolean SG = false;
        for(String line: fileLines) {
            if(line.contains(":TITLES:")) {
                TI = true;
                RQ = false;
                SG = false;
            } else if(line.contains(":REQUIREMENTS:")) {
                RQ = true;
                TI = false;
                SG = false;
            } else if(line.contains(":STAT_GAIN:")) {
                SG = true;
                TI = false;
                RQ = false;
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

    public String getCourseName() {
        return courseName;
    }

    public String getStatName() {
        return statName;
    }

    public double getCurrentTitleRequirement() {
        return titleRequirements[courseLevel];
    }

    public double getCurrentStatGain() {
        return statGains[courseLevel];
    }


    public String getHighestTitle(Character target) {
        String toReturn = "Not Qualified - " + courseName;
        if(target.hasCourse() && target.getStats().size() > 0) {
            if(target.getStats().get(statName) >= titleRequirements[courseLevel]) {
                toReturn = titles[courseLevel];
                target.getTitles().remove("Not Qualified Yet");
                courseLevel++;
            }
        }
        return toReturn;
    }

    public int getCourseLevel() {
        return courseLevel;
    }

}
