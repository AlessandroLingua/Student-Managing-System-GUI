module com.uopeople.studentmanagingsystemgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.uopeople.studentmanagingsystemgui to javafx.fxml;
    exports com.uopeople.studentmanagingsystemgui;
}