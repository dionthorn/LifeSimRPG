package org.dionthorn.lifesimrpg.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.dionthorn.lifesimrpg.Engine;
import org.dionthorn.lifesimrpg.entities.AbstractCharacter;
import org.dionthorn.lifesimrpg.entities.Course;
import org.dionthorn.lifesimrpg.entities.Map;

public class CoursesInfoScreenController extends AbstractGameScreenController {

    @FXML public Label coursesLbl;
    @FXML public Label currentCourseLbl;
    @FXML public Button checkTitleBtn;
    @FXML public Label currentCourseStatLbl;

    @Override
    public void initialize() {
        Engine.CURRENT_SCREEN = Engine.SCREEN.COURSES_INFO;
        updateAll();
    }

    @Override
    public void updateAll() {
        centerGridPane.getChildren().clear();
        AbstractCharacter player = Engine.gameState.getPlayer();
        playerInfoBtn.setText(String.format("%s Info", player.getFirstName()));
        if(player.hasCourse()) {
            currentCourseStatLbl.setText(
                    String.format(
                            "Stat Required: %s %s current stat: %.2f",
                            player.getCurrentCourse().getStatName(),
                            player.getFirstName(),
                            player.getStat(player.getCurrentCourse().getStatName())

                    )
            );
        }
        centerGridPane.getChildren().addAll(currentCourseLbl, currentCourseStatLbl, coursesLbl, checkTitleBtn);

        // update currentCourseLbl
        if(player.hasCourse()) {
            currentCourseLbl.setText("Current Course: " + player.getCurrentCourse().getCourseName());
        } else {
            currentCourseLbl.setText("Current Course: NONE");
        }

        // List a course Label and Take Course Button for each course
        int xCap = 0;
        int yCap = 2;
        Map map = Engine.gameState.getCurrentMap();
        for(Course course: map.getCourses()) {
            // for each course make a label and button and add to center grid pane
            Label courseName = new Label(course.getCourseName());
            Button takeCourse = new Button("Take Course");
            takeCourse.setOnAction(ActionEvent -> onTakeCourse(course.getCourseName()));
            GridPane.setConstraints(courseName, xCap, yCap);
            GridPane.setConstraints(takeCourse, xCap + 1, yCap);
            centerGridPane.getChildren().addAll(courseName, takeCourse);
        }

        updateDateLbl();
        updateMoneyLbl();
    }

    // Screen changers different for each screen

    public void onPlayerInfo() {
        // load fxml for PlayerInfoScreen.fxml
        try {
            Engine.loadFXML("PlayerInfoScreen.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onJobInfo() {
        // load fxml for JobInfoScreen.fxml
        try {
            Engine.loadFXML("JobInfoScreen.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onMapInfo() {
        // load fxml for JobInfoScreen.fxml
        try {
            Engine.loadFXML("MapInfoScreen.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // other buttons

    public void onTakeCourse(String courseName) {
        AbstractCharacter player = Engine.gameState.getPlayer();
        if(player.getCurrentCourse() == null) {
            for(Course course: Engine.gameState.getCurrentMap().getCourses()) {
                if(course.getCourseName().equals(courseName)) {
                    player.setCurrentCourse(course);
                }
            }
        }
        updateAll();
    }

    public void onCheckTitle() {
        updateTitle(true);
    }

}
