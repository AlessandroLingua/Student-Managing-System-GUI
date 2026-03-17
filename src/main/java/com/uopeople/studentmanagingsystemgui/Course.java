package com.uopeople.studentmanagingsystemgui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Course model class with JavaFX observable properties.
 * Represents an academic course with an ID, name, and maximum capacity.
 */
public class Course {
    private final SimpleIntegerProperty courseId;
    private final SimpleStringProperty courseName;
    private final SimpleIntegerProperty maxCapacity;

    public Course(int courseId, String courseName, int maxCapacity) {
        this.courseId = new SimpleIntegerProperty(courseId);
        this.courseName = new SimpleStringProperty(courseName);
        this.maxCapacity = new SimpleIntegerProperty(maxCapacity);
    }

    // Property accessors
    public SimpleIntegerProperty courseIdProperty() { return courseId; }
    public SimpleStringProperty courseNameProperty() { return courseName; }
    public SimpleIntegerProperty maxCapacityProperty() { return maxCapacity; }

    // Getters and setters
    public int getCourseId() { return courseId.get(); }
    public String getCourseName() { return courseName.get(); }
    public int getMaxCapacity() { return maxCapacity.get(); }

    @Override
    public String toString() {
        return courseId.get() + " - " + courseName.get();
    }
}
