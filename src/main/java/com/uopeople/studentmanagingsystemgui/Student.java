package com.studentmanagement;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Student model class using JavaFX properties for data binding with TableView.
 * Each property is observable, enabling dynamic updates in the GUI.
 */
public class Student {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty email;
    private final SimpleStringProperty major;

    public Student(int id, String name, String email, String major) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.major = new SimpleStringProperty(major);
    }

    // Property accessors for TableView column binding
    public SimpleIntegerProperty idProperty() { return id; }
    public SimpleStringProperty nameProperty() { return name; }
    public SimpleStringProperty emailProperty() { return email; }
    public SimpleStringProperty majorProperty() { return major; }

    // Standard getters and setters
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }

    public String getMajor() { return major.get(); }
    public void setMajor(String major) { this.major.set(major); }

    @Override
    public String toString() {
        return id.get() + " - " + name.get();
    }
}
