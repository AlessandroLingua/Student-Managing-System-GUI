package com.uopeople.studentmanagingsystemgui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * StudentManagementApp is the main GUI application for managing students,
 * course enrollments, and grades. Built with JavaFX, it uses event-driven
 * programming to handle all user interactions dynamically.
 *
 * Design logic:
 * - TabPane organizes the three major functional areas (Students, Enrollment, Grades)
 *   into separate, clearly labeled tabs for intuitive navigation.
 * - TableView with ObservableList ensures dynamic updates: any changes to the
 *   underlying data are automatically reflected in the GUI without manual refreshing.
 * - Dialog-based forms (TextInputDialog, custom dialogs) keep the main interface
 *   uncluttered while providing focused data-entry experiences.
 * - ComboBox dropdowns give administrators quick selection of students and courses,
 *   reducing input errors.
 * - MenuBar provides an alternative navigation path for users who prefer
 *   keyboard shortcuts and menu-driven workflows.
 */
public class StudentManagementApp extends Application {

    // Observable collections – the single source of truth
    private final ObservableList<Student> studentList = FXCollections.observableArrayList();
    private final ObservableList<Course> courseList = FXCollections.observableArrayList();
    private final ObservableList<Enrollment> enrollmentList = FXCollections.observableArrayList();

    // Auto-incrementing ID counter for new students
    private int nextStudentId = 1;

    // GUI components that need class-level access for dynamic updates
    private TableView<Student> studentTable;
    private TableView<Enrollment> enrollmentTable;
    private TableView<Enrollment> gradeTable;
    private ComboBox<Student> enrollStudentCombo;
    private ComboBox<Course> enrollCourseCombo;
    private ComboBox<Student> gradeStudentCombo;
    private ComboBox<Course> gradeCourseCombo;
    private Label statusLabel;

    /**
     * Entry point for the JavaFX application. Sets up the entire GUI layout
     * and registers all event handlers.
     */
    @Override
    public void start(Stage primaryStage) {
        // Initialize sample data so the application is not empty on launch
        initializeSampleData();

        // Root layout
        BorderPane root = new BorderPane();
        root.setTop(createMenuBar(primaryStage));
        root.setCenter(createMainContent());
        root.setBottom(createStatusBar());

        // Scene and stage
        Scene scene = new Scene(root, 900, 650);
        primaryStage.setTitle("Student Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(750);
        primaryStage.setMinHeight(500);
        primaryStage.show();

        updateStatus("Application loaded. Ready.");
    }

    // MENU BAR – provides menu-driven access to all major actions
    /**
     * Creates the top menu bar with File, Student, Course, and Help menus.
     * Each MenuItem is wired to the same logic as its corresponding button,
     * ensuring consistency between menu and button interactions.
     */
    private MenuBar createMenuBar(Stage stage) {
        // File menu
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> stage.close());
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().add(exitItem);

        // Student menu
        MenuItem addStudentItem = new MenuItem("Add Student");
        addStudentItem.setOnAction(e -> handleAddStudent());
        MenuItem updateStudentItem = new MenuItem("Update Student");
        updateStudentItem.setOnAction(e -> handleUpdateStudent());
        Menu studentMenu = new Menu("Student");
        studentMenu.getItems().addAll(addStudentItem, updateStudentItem);

        // Course menu
        MenuItem enrollItem = new MenuItem("Enroll Student");
        enrollItem.setOnAction(e -> handleEnrollStudent());
        Menu courseMenu = new Menu("Course");
        courseMenu.getItems().add(enrollItem);

        // Help menu
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().add(aboutItem);

        return new MenuBar(fileMenu, studentMenu, courseMenu, helpMenu);
    }

    // MAIN CONTENT – TabPane with three functional areas
    /**
     * Creates the central TabPane with Student Management, Course Enrollment,
     * and Grade Management tabs.
     */
    private TabPane createMainContent() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab studentTab = new Tab("Student Management", createStudentTab());
        Tab enrollmentTab = new Tab("Course Enrollment", createEnrollmentTab());
        Tab gradeTab = new Tab("Grade Management", createGradeTab());

        tabPane.getTabs().addAll(studentTab, enrollmentTab, gradeTab);

        // Refresh combo boxes when switching tabs for dynamic updates
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            refreshComboBoxes();
        });

        return tabPane;
    }

    // TAB 1 – STUDENT MANAGEMENT
    /**
     * Builds the Student Management tab with a TableView displaying all
     * students and buttons for Add, Update, Delete, and View Details.
     */
    @SuppressWarnings("unchecked")
    private VBox createStudentTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        // Title label
        Label title = new Label("Student Records");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));

        //  Student TableView
        studentTable = new TableView<>();
        studentTable.setItems(studentList);
        studentTable.setPlaceholder(new Label("No students in the system."));

        TableColumn<Student, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(250);

        TableColumn<Student, String> majorCol = new TableColumn<>("Major");
        majorCol.setCellValueFactory(new PropertyValueFactory<>("major"));
        majorCol.setPrefWidth(200);

        studentTable.getColumns().addAll(idCol, nameCol, emailCol, majorCol);
        VBox.setVgrow(studentTable, Priority.ALWAYS);

        //  Button bar
        Button addBtn = new Button("Add Student");
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        addBtn.setOnAction(e -> handleAddStudent());

        Button updateBtn = new Button("Update Student");
        updateBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        updateBtn.setOnAction(e -> handleUpdateStudent());

        Button deleteBtn = new Button("Delete Student");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteBtn.setOnAction(e -> handleDeleteStudent());

        Button viewBtn = new Button("View Details");
        viewBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-weight: bold;");
        viewBtn.setOnAction(e -> handleViewStudentDetails());

        HBox buttonBar = new HBox(10, addBtn, updateBtn, deleteBtn, viewBtn);
        buttonBar.setAlignment(Pos.CENTER_LEFT);

        vbox.getChildren().addAll(title, studentTable, buttonBar);
        return vbox;
    }

    // TAB 2 – COURSE ENROLLMENT
    /**
     * Builds the Course Enrollment tab with ComboBox dropdowns for selecting
     * a student and a course, an Enroll button, and a table showing all
     * current enrollments.
     */
    @SuppressWarnings("unchecked")
    private VBox createEnrollmentTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        Label title = new Label("Course Enrollment");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));

        // Selection controls
        Label studentLabel = new Label("Select Student:");
        enrollStudentCombo = new ComboBox<>(studentList);
        enrollStudentCombo.setPromptText("Choose a student...");
        enrollStudentCombo.setPrefWidth(300);

        Label courseLabel = new Label("Select Course:");
        enrollCourseCombo = new ComboBox<>(courseList);
        enrollCourseCombo.setPromptText("Choose a course...");
        enrollCourseCombo.setPrefWidth(300);

        Button enrollBtn = new Button("Enroll Student in Course");
        enrollBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        enrollBtn.setOnAction(e -> handleEnrollStudent());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(studentLabel, 0, 0);
        form.add(enrollStudentCombo, 1, 0);
        form.add(courseLabel, 0, 1);
        form.add(enrollCourseCombo, 1, 1);
        form.add(enrollBtn, 1, 2);

        // Enrollment TableView
        Label tableTitle = new Label("Current Enrollments");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

        enrollmentTable = new TableView<>();
        enrollmentTable.setItems(enrollmentList);
        enrollmentTable.setPlaceholder(new Label("No enrollments yet."));

        TableColumn<Enrollment, String> enrStudentCol = new TableColumn<>("Student");
        enrStudentCol.setCellValueFactory(data -> data.getValue().studentNameProperty());
        enrStudentCol.setPrefWidth(250);

        TableColumn<Enrollment, String> enrCourseCol = new TableColumn<>("Course");
        enrCourseCol.setCellValueFactory(data -> data.getValue().courseNameProperty());
        enrCourseCol.setPrefWidth(250);

        TableColumn<Enrollment, String> enrGradeCol = new TableColumn<>("Grade");
        enrGradeCol.setCellValueFactory(data -> data.getValue().gradeProperty());
        enrGradeCol.setPrefWidth(100);

        enrollmentTable.getColumns().addAll(enrStudentCol, enrCourseCol, enrGradeCol);
        VBox.setVgrow(enrollmentTable, Priority.ALWAYS);

        vbox.getChildren().addAll(title, form, new Separator(), tableTitle, enrollmentTable);
        return vbox;
    }

    //  TAB 3 – GRADE MANAGEMENT
    /**
     * Builds the Grade Management tab with ComboBoxes for selecting a student
     * and their enrolled course, a ComboBox for grade selection, and a table
     * showing the selected student's grades across all enrolled courses.
     */
    @SuppressWarnings("unchecked")
    private VBox createGradeTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        Label title = new Label("Grade Management");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));

        // Student selection
        Label studentLabel = new Label("Select Student:");
        gradeStudentCombo = new ComboBox<>(studentList);
        gradeStudentCombo.setPromptText("Choose a student...");
        gradeStudentCombo.setPrefWidth(300);

        // Course selection (filtered by student enrollment)
        Label courseLabel = new Label("Enrolled Course:");
        gradeCourseCombo = new ComboBox<>();
        gradeCourseCombo.setPromptText("Select student first...");
        gradeCourseCombo.setPrefWidth(300);

        // Event handler: when a student is selected, filter courses to only
        // those the student is enrolled in
        gradeStudentCombo.setOnAction(e -> {
            Student selected = gradeStudentCombo.getValue();
            if (selected != null) {
                ObservableList<Course> enrolledCourses = FXCollections.observableArrayList(
                    enrollmentList.stream()
                        .filter(en -> en.getStudent().getId() == selected.getId())
                        .map(Enrollment::getCourse)
                        .collect(Collectors.toList())
                );
                gradeCourseCombo.setItems(enrolledCourses);
                gradeCourseCombo.setPromptText("Choose a course...");

                // Refresh the grade table to show this student's grades
                refreshGradeTable(selected);
            }
        });

        // Grade selection
        Label gradeLabel = new Label("Assign Grade:");
        ComboBox<String> gradeValueCombo = new ComboBox<>(
            FXCollections.observableArrayList("A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D", "F")
        );
        gradeValueCombo.setPromptText("Select grade...");
        gradeValueCombo.setPrefWidth(150);

        Button assignBtn = new Button("Assign Grade");
        assignBtn.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white; -fx-font-weight: bold;");
        assignBtn.setOnAction(e -> handleAssignGrade(gradeValueCombo));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(studentLabel, 0, 0);
        form.add(gradeStudentCombo, 1, 0);
        form.add(courseLabel, 0, 1);
        form.add(gradeCourseCombo, 1, 1);
        form.add(gradeLabel, 0, 2);
        form.add(gradeValueCombo, 1, 2);
        form.add(assignBtn, 1, 3);

        // Grade display table
        Label tableTitle = new Label("Student Grades");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

        gradeTable = new TableView<>();
        gradeTable.setPlaceholder(new Label("Select a student to view grades."));

        TableColumn<Enrollment, String> gCourseCol = new TableColumn<>("Course");
        gCourseCol.setCellValueFactory(data -> data.getValue().courseNameProperty());
        gCourseCol.setPrefWidth(350);

        TableColumn<Enrollment, String> gGradeCol = new TableColumn<>("Grade");
        gGradeCol.setCellValueFactory(data -> data.getValue().gradeProperty());
        gGradeCol.setPrefWidth(150);

        gradeTable.getColumns().addAll(gCourseCol, gGradeCol);
        VBox.setVgrow(gradeTable, Priority.ALWAYS);

        vbox.getChildren().addAll(title, form, new Separator(), tableTitle, gradeTable);
        return vbox;
    }

    // STATUS BAR
    private HBox createStatusBar() {
        statusLabel = new Label("Ready");
        statusLabel.setFont(Font.font("System", 12));
        HBox bar = new HBox(statusLabel);
        bar.setPadding(new Insets(5, 10, 5, 10));
        bar.setStyle("-fx-background-color: #ECEFF1;");
        return bar;
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    // EVENT HANDLERS – Student Management
    /**
     * Handles the Add Student action. Opens a custom dialog with text fields
     * for entering the student's name, email, and major. Validates input
     * before creating the Student object and adding it to the observable list.
     */
    private void handleAddStudent() {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Add New Student");
        dialog.setHeaderText("Enter student information:");

        // Set up dialog buttons
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        TextField emailField = new TextField();
        emailField.setPromptText("email@example.com");
        TextField majorField = new TextField();
        majorField.setPromptText("e.g., Computer Science");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Major:"), 0, 2);
        grid.add(majorField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convert the result when Add is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String major = majorField.getText().trim();

                //  Input validation
                if (name.isEmpty() || email.isEmpty() || major.isEmpty()) {
                    showError("Validation Error", "All fields are required. Please fill in every field.");
                    return null;
                }
                if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                    showError("Validation Error", "Please enter a valid email address.");
                    return null;
                }

                return new Student(nextStudentId++, name, email, major);
            }
            return null;
        });

        Optional<Student> result = dialog.showAndWait();
        result.ifPresent(student -> {
            studentList.add(student);
            updateStatus("Added student: " + student.getName());
        });
    }

    /**
     * Handles the Update Student action. The user must first select a student
     * from the table. A pre-filled dialog appears allowing the administrator
     * to modify the student's details.
     */
    private void handleUpdateStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select a student from the table first.");
            return;
        }

        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Update Student");
        dialog.setHeaderText("Modify student information for: " + selected.getName());

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(selected.getName());
        TextField emailField = new TextField(selected.getEmail());
        TextField majorField = new TextField(selected.getMajor());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Major:"), 0, 2);
        grid.add(majorField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                String major = majorField.getText().trim();

                if (name.isEmpty() || email.isEmpty() || major.isEmpty()) {
                    showError("Validation Error", "All fields are required.");
                    return null;
                }
                if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                    showError("Validation Error", "Please enter a valid email address.");
                    return null;
                }

                selected.setName(name);
                selected.setEmail(email);
                selected.setMajor(major);
                return selected;
            }
            return null;
        });

        Optional<Student> result = dialog.showAndWait();
        result.ifPresent(student -> {
            studentTable.refresh();
            updateStatus("Updated student: " + student.getName());
        });
    }

    /**
     * Handles the Delete Student action. Asks for confirmation, then removes
     * the student and all associated enrollments from the system.
     */
    private void handleDeleteStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select a student from the table first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete student: " + selected.getName() + "?");
        confirm.setContentText("This will also remove all enrollments for this student.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Remove associated enrollments first
            enrollmentList.removeIf(en -> en.getStudent().getId() == selected.getId());
            studentList.remove(selected);
            updateStatus("Deleted student: " + selected.getName());
        }
    }

    /**
     * Handles the View Student Details action. Shows a read-only dialog with
     * the selected student's information and their enrollment/grade summary.
     */
    private void handleViewStudentDetails() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select a student from the table first.");
            return;
        }

        // Build enrollment summary
        StringBuilder enrollments = new StringBuilder();
        for (Enrollment en : enrollmentList) {
            if (en.getStudent().getId() == selected.getId()) {
                enrollments.append("  - ")
                    .append(en.getCourse().getCourseName())
                    .append(" | Grade: ").append(en.getGrade())
                    .append("\n");
            }
        }

        String details = String.format(
            "ID: %d\nName: %s\nEmail: %s\nMajor: %s\n\nEnrolled Courses:\n%s",
            selected.getId(), selected.getName(), selected.getEmail(), selected.getMajor(),
            enrollments.length() > 0 ? enrollments.toString() : "  (No enrollments)"
        );

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Student Details");
        info.setHeaderText(selected.getName());
        info.setContentText(details);
        info.getDialogPane().setMinWidth(400);
        info.showAndWait();
    }

    // EVENT HANDLERS – Course Enrollment
    /**
     * Handles the Enroll Student action. Validates that both a student and
     * course are selected, checks for duplicate enrollment, and creates
     * a new Enrollment record that immediately appears in the enrollment table.
     */
    private void handleEnrollStudent() {
        Student student = enrollStudentCombo.getValue();
        Course course = enrollCourseCombo.getValue();

        if (student == null || course == null) {
            showError("Incomplete Selection",
                "Please select both a student and a course before enrolling.");
            return;
        }

        // Check for duplicate enrollment
        boolean alreadyEnrolled = enrollmentList.stream().anyMatch(
            en -> en.getStudent().getId() == student.getId()
               && en.getCourse().getCourseId() == course.getCourseId()
        );

        if (alreadyEnrolled) {
            showError("Duplicate Enrollment",
                student.getName() + " is already enrolled in " + course.getCourseName() + ".");
            return;
        }

        // Check course capacity
        long currentCount = enrollmentList.stream()
            .filter(en -> en.getCourse().getCourseId() == course.getCourseId())
            .count();

        if (currentCount >= course.getMaxCapacity()) {
            showError("Course Full",
                course.getCourseName() + " has reached its maximum capacity of "
                + course.getMaxCapacity() + " students.");
            return;
        }

        // Create enrollment – the ObservableList ensures the table updates automatically
        Enrollment enrollment = new Enrollment(student, course);
        enrollmentList.add(enrollment);
        updateStatus("Enrolled " + student.getName() + " in " + course.getCourseName());

        // Reset selections
        enrollStudentCombo.setValue(null);
        enrollCourseCombo.setValue(null);
    }

    //  EVENT HANDLERS – Grade Management
    /**
     * Handles the Assign Grade action. Validates selections, finds the
     * matching Enrollment record, and updates its grade property. Because
     * the grade is a JavaFX SimpleStringProperty, the table cell updates
     * instantly through data binding.
     */
    private void handleAssignGrade(ComboBox<String> gradeValueCombo) {
        Student student = gradeStudentCombo.getValue();
        Course course = gradeCourseCombo.getValue();
        String grade = gradeValueCombo.getValue();

        if (student == null || course == null || grade == null) {
            showError("Incomplete Selection",
                "Please select a student, a course, and a grade before assigning.");
            return;
        }

        // Find the matching enrollment
        Optional<Enrollment> target = enrollmentList.stream()
            .filter(en -> en.getStudent().getId() == student.getId()
                       && en.getCourse().getCourseId() == course.getCourseId())
            .findFirst();

        if (target.isPresent()) {
            target.get().setGrade(grade);
            gradeTable.refresh();
            enrollmentTable.refresh();
            updateStatus("Assigned grade " + grade + " to " + student.getName()
                + " for " + course.getCourseName());
        } else {
            showError("Enrollment Not Found",
                student.getName() + " is not enrolled in " + course.getCourseName() + ".");
        }
    }

    // HELPER METHODS
    /**
     * Refreshes the grade table to show only enrollments for the specified student.
     */
    private void refreshGradeTable(Student student) {
        ObservableList<Enrollment> filtered = FXCollections.observableArrayList(
            enrollmentList.stream()
                .filter(en -> en.getStudent().getId() == student.getId())
                .collect(Collectors.toList())
        );
        gradeTable.setItems(filtered);
    }

    /**
     * Refreshes all ComboBox controls to reflect the current data.
     */
    private void refreshComboBoxes() {
        if (enrollStudentCombo != null) enrollStudentCombo.setItems(studentList);
        if (enrollCourseCombo != null) enrollCourseCombo.setItems(courseList);
        if (gradeStudentCombo != null) gradeStudentCombo.setItems(studentList);
    }

    /**
     * Displays an error dialog with the given title and message.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays the About dialog for the application.
     */
    private void showAboutDialog() {
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("About");
        about.setHeaderText("Student Management System v1.0");
        about.setContentText(
            "A JavaFX GUI application for managing student records,\n"
            + "course enrollments, and grades.\n\n"
            + "Built with JavaFX.");
        about.showAndWait();
    }

    /**
     * Pre-populates the system with sample students and courses so the
     * application has data to display immediately upon launch.
     */
    private void initializeSampleData() {
        // Sample students
        studentList.add(new Student(nextStudentId++, "Alessandro Lingua", "a.lingua@uopeople.com", "Computer Science"));
        studentList.add(new Student(nextStudentId++, "Martina Pernice", "m.pernice@uopeople.com", "Law"));
        studentList.add(new Student(nextStudentId++, "Alex David", "a.david@uopeople.com", "Computer Science"));

        // Sample courses
        courseList.add(new Course(101, "Introduction to Programming", 30));
        courseList.add(new Course(102, "English Composition 2", 25));
        courseList.add(new Course(103, "Algebra I", 35));
        courseList.add(new Course(104, "Statistics", 30));
        courseList.add(new Course(105, "Programming 1", 25));
    }

    /**
     * Main method – delegates to the Launcher class for JDK 11+ compatibility.
     * When running directly, this method can be used as a fallback.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
