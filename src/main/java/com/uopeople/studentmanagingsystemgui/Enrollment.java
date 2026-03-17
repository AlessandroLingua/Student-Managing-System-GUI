package com.uopeople.studentmanagingsystemgui;

import javafx.beans.property.SimpleStringProperty;

/**
 * Enrollment model class linking a Student to a Course with a grade.
 * Uses JavaFX properties for observable binding in the GUI tables.
 */
public class Enrollment {
    private final Student student;
    private final Course course;
    private final SimpleStringProperty grade;

    public Enrollment(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.grade = new SimpleStringProperty("N/A");
    }

    // Property accessors for TableView binding
    public SimpleStringProperty studentNameProperty() {
        return student.nameProperty();
    }

    public SimpleStringProperty courseNameProperty() {
        return course.courseNameProperty();
    }

    public SimpleStringProperty gradeProperty() {
        return grade;
    }

    // Getters and setters
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public String getGrade() { return grade.get(); }
    public void setGrade(String grade) { this.grade.set(grade); }

    @Override
    public String toString() {
        return student.getName() + " -> " + course.getCourseName() + " [" + grade.get() + "]";
    }
}
