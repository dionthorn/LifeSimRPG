module LifeSimRPG {

    requires java.base;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    exports org.dionthorn.lifesimrpg;
    exports org.dionthorn.lifesimrpg.controllers;
    exports org.dionthorn.lifesimrpg.entities;
    exports org.dionthorn.lifesimrpg.records;
    opens org.dionthorn.lifesimrpg to javafx.graphics, javafx.fxml;
    opens org.dionthorn.lifesimrpg.controllers to javafx.fxml, javafx.graphics;
    opens org.dionthorn.lifesimrpg.entities to javafx.fxml, javafx.graphics;
    opens org.dionthorn.lifesimrpg.records to javafx.fxml, javafx.graphics;

}