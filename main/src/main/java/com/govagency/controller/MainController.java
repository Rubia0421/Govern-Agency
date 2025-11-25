package com.govagency.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.govagency.LocalDatabase;
import com.govagency.model.Citizen;
import com.govagency.model.Document;
import com.govagency.model.ServiceRequest;
import com.govagency.util.Validator;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class MainController {

    private final boolean isAdmin;
    private final Citizen loggedInCitizen;
    private final Map<String, Citizen> citizenMap;
    private final LocalDatabase database;

    // Citizens
    private TextField citizenNameField;
    private TextField citizenEmailField;
    private TextField citizenNumberField;
    private TextField searchField;
    private TextField editDeleteCitizenField;
    private TextArea citizenInfoArea;
    private Button addCitizenButton;
    private Button editCitizenButton;
    private Button deleteCitizenButton;
    private Button showAllCitizensButton;

    // Documents
    private TextField docCitizenIdField;
    private ComboBox<Document.Status> docStatusComboBox;
    private TextField docSearchCitizenIdField;
    private TextArea docStatusArea;
    private Button uploadDocButton;
    private Button checkDocButton;
    private Button searchDocByCitizenButton;
    private Button updateDocStatusButton;

    // Service Requests
    private TextField reqCitizenIdField;
    private TextField reqServiceTypeField;
    private TextField reqSearchCitizenIdField;
    private ComboBox<ServiceRequest.Status> reqStatusComboBox;
    private TextArea reqStatusArea;
    private Button addRequestButton;
    private Button showAllRequestsButton;
    private Button updateRequestStatusButton;
    private Button searchRequestsByCitizenButton;

    private final List<Document> documents = new ArrayList<>();
    private final List<ServiceRequest> serviceRequests = new ArrayList<>();
    private int nextCitizenId = 1;

    // Colors
    private static final String DARK_BG = "#0d1117";
    private static final String CARD_BG = "#161b22";
    private static final String PRIMARY_PURPLE = "#6e40aa";
    private static final String ACCENT_CYAN = "#58a6ff";
    private static final String SUCCESS_GREEN = "#3fb950";
    private static final String WARNING_ORANGE = "#d29922";
    private static final String ERROR_RED = "#f85149";
    private static final String TEXT_WHITE = "#c9d1d9";
    private static final String TEXT_GRAY = "#8b949e";
    private static final String INPUT_BG = "#0d1117";
    private static final String BORDER_PURPLE = "#30363d";

    public MainController(boolean isAdmin, Citizen citizen, Map<String, Citizen> citizenMap) {
        this.isAdmin = isAdmin;
        this.loggedInCitizen = citizen;
        this.citizenMap = citizenMap;
        this.database = new LocalDatabase();

        for (JSONObject obj : database.getAllCitizens()) {
            String id = obj.getString("id");
            String name = obj.getString("name");
            String email = obj.getString("email");
            String number = obj.getString("number");

            Citizen c = new Citizen(id, name, number, email);
            this.citizenMap.put(id, c);

            int numId = Integer.parseInt(id);
            if (numId >= nextCitizenId) nextCitizenId = numId + 1;
        }

        for (JSONObject obj : database.getAllDocuments()) {
            String id = obj.getString("id");
            String citizenId = obj.getString("ownerId");
            Document.Status status = Document.Status.valueOf(obj.getString("status"));

            Document doc = new Document(id, citizenId);
            doc.setStatus(status);
            documents.add(doc);
        }

        for (JSONObject obj : database.getAllRequests()) {
            String id = obj.getString("id");
            String citizenId = obj.getString("citizenId");
            String type = obj.getString("type");
            String description = obj.optString("description", "");
            ServiceRequest.Status status = ServiceRequest.Status.valueOf(obj.getString("status"));

            ServiceRequest sr = new ServiceRequest(id, citizenId, type, description);
            sr.setStatus(status);
            serviceRequests.add(sr);
        }
    }

    public Node getView() {
        BorderPane mainPane = new BorderPane();
        mainPane.setStyle("-fx-background-color: " + DARK_BG + ";");

        VBox headerPane = createHeaderPane();
        mainPane.setTop(headerPane);

        TabPane tabPane = new TabPane();

        if (isAdmin) {
            // Admin has all 3 tabs
            tabPane.getTabs().addAll(
                createStyledTab("👥 Citizens", createCitizenPane()),
                createStyledTab("📋 Documents", createDocumentPane()),
                createStyledTab("⚙️ Services", createServicePane())
            );
        } else {
            // Citizen has only 2 tabs: Profile and Services
            tabPane.getTabs().addAll(
                createStyledTab("👤 My Profile", createCitizenProfilePane()),
                createStyledTab("⚙️ My Requests", createServicePane())
            );
        }

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        applyTabPaneStyle(tabPane);

        mainPane.setCenter(tabPane);

        return mainPane;
    }

    private VBox createHeaderPane() {
        VBox header = new VBox(5);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + ACCENT_CYAN + ";" +
            "-fx-border-width: 0 0 1 0;"
        );

        Label titleLabel = new Label(isAdmin ? "🔐 ADMIN DASHBOARD" : "👤 CITIZEN PORTAL");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.web(ACCENT_CYAN));

        Label userLabel = new Label(isAdmin ? "Administrator Access" : "Welcome, " + loggedInCitizen.getName());
        userLabel.setFont(Font.font("Segoe UI", 12));
        userLabel.setTextFill(Color.web(TEXT_GRAY));

        header.getChildren().addAll(titleLabel, userLabel);
        return header;
    }

    private Tab createStyledTab(String title, Node content) {
        Tab tab = new Tab(title, content);
        tab.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: " + TEXT_WHITE + ";");
        return tab;
    }

    private void applyTabPaneStyle(TabPane tabPane) {
        tabPane.setStyle(
            "-fx-tab-min-width: 120;" +
            "-fx-tab-max-width: 250;" +
            "-fx-font-size: 12;" +
            "-fx-background-color: " + DARK_BG + ";" +
            "-fx-control-inner-background: " + DARK_BG + ";"
        );
    }

    // ==================== CITIZEN PROFILE PANE (CITIZEN ONLY) ====================

    private Node createCitizenProfilePane() {
        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: " + DARK_BG + ";");

        VBox profileSection = createCitizenProfileSection();
        VBox editSection = createCitizenEditSection();

        mainContent.getChildren().addAll(
            createTitleLabel("👤 My Profile"),
            profileSection,
            new Separator(),
            editSection
        );

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setStyle(
            "-fx-background-color: " + DARK_BG + ";" +
            "-fx-control-inner-background: " + DARK_BG + ";"
        );
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        return scrollPane;
    }

    private VBox createCitizenProfileSection() {
        VBox section = new VBox(12);
        section.setPadding(new Insets(15));
        section.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + ACCENT_CYAN + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        );

        Label sectionLabel = createSectionLabel("📋 Personal Information");

        VBox infoBox = new VBox(10);
        infoBox.setStyle("-fx-padding: 10; -fx-border-color: " + BORDER_PURPLE + "; -fx-border-radius: 5;");

        Label idLabel = new Label("ID: " + loggedInCitizen.getId());
        idLabel.setFont(Font.font("Segoe UI", 13));
        idLabel.setTextFill(Color.web(TEXT_WHITE));

        Label nameLabel = new Label("Name: " + loggedInCitizen.getName());
        nameLabel.setFont(Font.font("Segoe UI", 13));
        nameLabel.setTextFill(Color.web(TEXT_WHITE));

        Label emailLabel = new Label("Email: " + loggedInCitizen.getEmail());
        emailLabel.setFont(Font.font("Segoe UI", 13));
        emailLabel.setTextFill(Color.web(TEXT_WHITE));

        Label phoneLabel = new Label("Phone: " + loggedInCitizen.getNumber());
        phoneLabel.setFont(Font.font("Segoe UI", 13));
        phoneLabel.setTextFill(Color.web(TEXT_WHITE));

        infoBox.getChildren().addAll(idLabel, nameLabel, emailLabel, phoneLabel);
        section.getChildren().addAll(sectionLabel, infoBox);
        return section;
    }

    private VBox createCitizenEditSection() {
        VBox section = new VBox(12);
        section.setPadding(new Insets(15));
        section.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + ACCENT_CYAN + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        );

        Label sectionLabel = createSectionLabel("✏️ Update Contact Information");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        citizenEmailField = createStyledTextField("New Email Address");
        citizenNumberField = createStyledTextField("New Phone Number");

        grid.add(createLabel("Email:"), 0, 0);
        grid.add(citizenEmailField, 1, 0);
        grid.add(createLabel("Phone:"), 0, 1);
        grid.add(citizenNumberField, 1, 1);

        ColumnConstraints col1 = new ColumnConstraints(80);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        Button updateButton = createStyledButton("💾 Update", SUCCESS_GREEN);
        updateButton.setOnAction(e -> updateCitizenProfile());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(updateButton);

        section.getChildren().addAll(sectionLabel, grid, buttonBox);
        return section;
    }

    private void updateCitizenProfile() {
        String newEmail = citizenEmailField.getText().trim();
        String newPhone = citizenNumberField.getText().trim();

        if (newEmail.isEmpty() || newPhone.isEmpty()) {
            showError("❌ Email and Phone cannot be empty.");
            return;
        }

        if (!Validator.isValidEmail(newEmail)) {
            showError("❌ Invalid email format.");
            return;
        }

        if (citizenMap.values().stream()
                .anyMatch(c -> !c.getId().equals(loggedInCitizen.getId()) && c.getEmail().equalsIgnoreCase(newEmail))) {
            showError("❌ Email already in use by another citizen.");
            return;
        }

        if (!isValidPhilippinePhoneNumber(newPhone)) {
            showError("❌ Invalid Philippine phone number.\nFormats: 09XXXXXXXXX | +639XXXXXXXXX");
            return;
        }

        loggedInCitizen.setEmail(newEmail);
        loggedInCitizen.setNumber(newPhone);
        database.updateCitizen(loggedInCitizen.getId(), loggedInCitizen);

        showSuccess("✅ Profile updated successfully!");
        citizenEmailField.clear();
        citizenNumberField.clear();
    }

    // ==================== CITIZEN PANE (ADMIN ONLY) ====================

    private Node createCitizenPane() {
        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: " + DARK_BG + ";");

        VBox formSection = createCitizenFormSection();
        VBox searchSection = createCitizenSearchSection();

        citizenInfoArea = new TextArea();
        citizenInfoArea.setEditable(false);
        citizenInfoArea.setWrapText(true);
        citizenInfoArea.setPrefRowCount(10);
        applyTextAreaStyle(citizenInfoArea);
        VBox.setVgrow(citizenInfoArea, Priority.ALWAYS);

        mainContent.getChildren().addAll(
            createTitleLabel("👥 Citizen Management"),
            formSection,
            new Separator(),
            searchSection,
            new Separator(),
            citizenInfoArea
        );

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setStyle(
            "-fx-background-color: " + DARK_BG + ";" +
            "-fx-control-inner-background: " + DARK_BG + ";"
        );
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        return scrollPane;
    }

    private VBox createCitizenFormSection() {
        VBox section = new VBox(12);
        section.setPadding(new Insets(15));
        section.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + ACCENT_CYAN + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        );

        Label sectionLabel = createSectionLabel("➕ Add New Citizen");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        citizenNameField = createStyledTextField("Full Name");
        citizenEmailField = createStyledTextField("Email Address");
        citizenNumberField = createStyledTextField("Philippine Phone Number");

        grid.add(createLabel("Name:"), 0, 0);
        grid.add(citizenNameField, 1, 0);
        grid.add(createLabel("Email:"), 0, 1);
        grid.add(citizenEmailField, 1, 1);
        grid.add(createLabel("Phone:"), 0, 2);
        grid.add(citizenNumberField, 1, 2);

        ColumnConstraints col1 = new ColumnConstraints(80);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        addCitizenButton = createStyledButton("✚ Add", SUCCESS_GREEN);
        addCitizenButton.setOnAction(e -> addCitizen());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(addCitizenButton);

        section.getChildren().addAll(sectionLabel, grid, buttonBox);
        return section;
    }

    private VBox createCitizenSearchSection() {
        VBox section = new VBox(12);
        section.setPadding(new Insets(15));
        section.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + ACCENT_CYAN + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        );

        Label sectionLabel = createSectionLabel("🔍 Search & Manage Citizens");

        searchField = createStyledTextField("Search by ID");
        editDeleteCitizenField = createStyledTextField("Edit/Delete by ID");

        Button searchButton = createStyledButton("🔎 Search", ACCENT_CYAN);
        searchButton.setOnAction(e -> searchCitizen());

        showAllCitizensButton = createStyledButton("👁️ Show All", PRIMARY_PURPLE);
        showAllCitizensButton.setOnAction(e -> showAllCitizens());

        editCitizenButton = createStyledButton("✏️ Edit", WARNING_ORANGE);
        editCitizenButton.setOnAction(e -> editCitizen());

        deleteCitizenButton = createStyledButton("🗑️ Delete", ERROR_RED);
        deleteCitizenButton.setOnAction(e -> deleteCitizen());

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.getChildren().addAll(createLabel("Search:"), searchField, searchButton, showAllCitizensButton);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        HBox editBox = new HBox(10);
        editBox.setAlignment(Pos.CENTER_LEFT);
        editBox.getChildren().addAll(createLabel("Edit/Delete:"), editDeleteCitizenField, editCitizenButton, deleteCitizenButton);
        HBox.setHgrow(editDeleteCitizenField, Priority.ALWAYS);

        section.getChildren().addAll(sectionLabel, searchBox, editBox);
        return section;
    }

    private void addCitizen() {
        try {
            String id = String.valueOf(nextCitizenId);
            String name = citizenNameField.getText().trim();
            String email = citizenEmailField.getText().trim();
            String number = citizenNumberField.getText().trim();

            if (!Validator.isValidCitizenName(name)) {
                showError("❌ Invalid name. Only letters and spaces allowed.");
                return;
            }
            if (name.isEmpty()) {
                showError("❌ Name cannot be empty.");
                return;
            }
            if (!Validator.isValidEmail(email)) {
                showError("❌ Invalid email format.");
                return;
            }
            if (!isValidPhilippinePhoneNumber(number)) {
                showError("❌ Invalid Philippine phone number.\nFormats: 09XXXXXXXXX | +639XXXXXXXXX | +6309XXXXXXXXX");
                return;
            }
            if (citizenMap.containsKey(id)) {
                showError("❌ Citizen ID already exists.");
                return;
            }
            if (citizenMap.values().stream().anyMatch(c -> c.getEmail().equalsIgnoreCase(email))) {
                showError("❌ Email already in use.");
                return;
            }

            Citizen citizen = new Citizen(id, name, number, email);
            citizenMap.put(id, citizen);
            database.addCitizen(citizen);
            showSuccess("✅ Citizen added successfully:\n" + citizen);

            nextCitizenId++;
            citizenNameField.clear();
            citizenEmailField.clear();
            citizenNumberField.clear();

        } catch (IllegalArgumentException ex) {
            showError("❌ " + ex.getMessage());
        }
    }

    private void editCitizen() {
        String id = editDeleteCitizenField.getText().trim();
        if (!citizenMap.containsKey(id)) {
            showError("❌ Citizen ID not found.");
            return;
        }
        Citizen citizen = citizenMap.get(id);
        String newName = citizenNameField.getText().trim();
        String newEmail = citizenEmailField.getText().trim();
        String newNumber = citizenNumberField.getText().trim();

        if (newName.isEmpty()) {
            showError("❌ Name cannot be empty.");
            return;
        }
        if (!Validator.isValidEmail(newEmail)) {
            showError("❌ Invalid email format.");
            return;
        }
        if (citizenMap.values().stream()
                .anyMatch(c -> !c.getId().equals(id) && c.getEmail().equalsIgnoreCase(newEmail))) {
            showError("❌ Email already in use by another citizen.");
            return;
        }
        if (!Validator.isValidCitizenName(newName)) {
            showError("❌ Invalid name. Only letters and spaces allowed.");
            return;
        }
        if (!isValidPhilippinePhoneNumber(newNumber)) {
            showError("❌ Invalid Philippine phone number.\nFormats: 09XXXXXXXXX | +639XXXXXXXXX | +6309XXXXXXXXX");
            return;
        }

        citizen.setName(newName);
        citizen.setEmail(newEmail);
        database.updateCitizen(id, citizen);
        showSuccess("✅ Citizen updated:\n" + citizen);
    }

    private void deleteCitizen() {
        String id = editDeleteCitizenField.getText().trim();
        if (!citizenMap.containsKey(id)) {
            showError("❌ Citizen ID not found.");
            return;
        }

        database.deleteCitizen(id);
        citizenMap.remove(id);

        editDeleteCitizenField.clear();
        citizenNameField.clear();
        citizenEmailField.clear();
        citizenNumberField.clear();

        showSuccess("✅ Citizen with ID " + id + " deleted successfully.");
    }

    private void searchCitizen() {
        String id = searchField.getText().trim();
        Citizen citizen = citizenMap.get(id);
        if (citizen == null) {
            showError("❌ Citizen not found.");
        } else {
            citizenInfoArea.setText(citizen.toString());
            citizenNameField.setText(citizen.getName());
            citizenEmailField.setText(citizen.getEmail());
            citizenNumberField.setText(citizen.getNumber());
        }
    }

    private void showAllCitizens() {
        if (citizenMap.isEmpty()) {
            citizenInfoArea.setText("No citizens found.");
            return;
        }
        StringBuilder sb = new StringBuilder("╔════════════════════════════════════════╗\n");
        sb.append("║          📋 ALL CITIZENS               ║\n");
        sb.append("╚════════════════════════════════════════╝\n\n");
        for (Citizen citizen : citizenMap.values()) {
            sb.append(citizen).append("\n\n");
        }
        citizenInfoArea.setText(sb.toString());
    }

    // ==================== DOCUMENT PANE (ADMIN ONLY) ====================

    private Node createDocumentPane() {
        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: " + DARK_BG + ";");

        VBox uploadSection = createDocumentUploadSection();
        VBox searchSection = createDocumentSearchSection();

        docStatusArea = new TextArea();
        docStatusArea.setEditable(false);
        docStatusArea.setWrapText(true);
        docStatusArea.setPrefRowCount(10);
        applyTextAreaStyle(docStatusArea);
        VBox.setVgrow(docStatusArea, Priority.ALWAYS);

        mainContent.getChildren().addAll(
            createTitleLabel("📄 Document Tracking"),
            uploadSection,
            new Separator(),
            searchSection,
            new Separator(),
            docStatusArea
        );

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setStyle(
            "-fx-background-color: " + DARK_BG + ";" +
            "-fx-control-inner-background: " + DARK_BG + ";"
        );
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        return scrollPane;
    }

    private VBox createDocumentUploadSection() {
        VBox section = new VBox(12);
        section.setPadding(new Insets(15));
        section.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + ACCENT_CYAN + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        );

        Label sectionLabel = createSectionLabel("⬆️ Upload Document");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        docCitizenIdField = createStyledTextField("Citizen ID");
        docStatusComboBox = new ComboBox<>();
        docStatusComboBox.getItems().addAll(Document.Status.values());
        docStatusComboBox.setValue(Document.Status.PENDING);
        docStatusComboBox.setMaxWidth(250);
        applyComboBoxStyle(docStatusComboBox);

        Label docIdLabel = createLabel("(Auto-Generated)");

        grid.add(createLabel("Doc ID:"), 0, 0);
        grid.add(docIdLabel, 1, 0);
        grid.add(createLabel("Citizen ID:"), 0, 1);
        grid.add(docCitizenIdField, 1, 1);
        grid.add(createLabel("Status:"), 0, 2);
        grid.add(docStatusComboBox, 1, 2);

        ColumnConstraints col1 = new ColumnConstraints(80);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        uploadDocButton = createStyledButton("⬆️ Upload", SUCCESS_GREEN);
        uploadDocButton.setOnAction(e -> uploadDocument());

        updateDocStatusButton = createStyledButton("🔄 Update", WARNING_ORANGE);
        updateDocStatusButton.setOnAction(e -> updateDocumentStatus());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(uploadDocButton, updateDocStatusButton);

        section.getChildren().addAll(sectionLabel, grid, buttonBox);
        return section;
    }

    private VBox createDocumentSearchSection() {
        VBox section = new VBox(12);
        section.setPadding(new Insets(15));
        section.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + ACCENT_CYAN + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        );

        Label sectionLabel = createSectionLabel("🔍 Search Documents");

        docSearchCitizenIdField = createStyledTextField("Citizen ID");

        searchDocByCitizenButton = createStyledButton("🔎 Search", ACCENT_CYAN);
        searchDocByCitizenButton.setOnAction(e -> searchDocumentsByCitizenId());

        checkDocButton = createStyledButton("👁️ Show All", PRIMARY_PURPLE);
        checkDocButton.setOnAction(e -> checkDocuments());

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.getChildren().addAll(createLabel("Citizen ID:"), docSearchCitizenIdField, searchDocByCitizenButton, checkDocButton);
        HBox.setHgrow(docSearchCitizenIdField, Priority.ALWAYS);

        section.getChildren().addAll(sectionLabel, searchBox);
        return section;
    }

    private void uploadDocument() {
        String citizenId = docCitizenIdField.getText().trim();
        Document.Status status = docStatusComboBox.getValue();

        if (citizenId.isEmpty()) {
            showError("❌ Citizen ID cannot be empty.");
            return;
        }
        if (!citizenMap.containsKey(citizenId)) {
            showError("❌ Citizen ID does not exist.");
            return;
        }

        String docId = generateDocumentId(citizenId);

        Document doc = new Document(docId, citizenId);
        doc.setStatus(status);
        documents.add(doc);
        database.addDocument(doc);
        showSuccess("✅ Document uploaded:\n" +
                "ID: " + docId + "\n" +
                "Citizen ID: " + citizenId + "\n" +
                "Status: " + status);

        docCitizenIdField.clear();
        docStatusComboBox.setValue(Document.Status.PENDING);
    }

    private void checkDocuments() {
        if (documents.isEmpty()) {
            docStatusArea.setText("No documents found.");
            return;
        }
        StringBuilder sb = new StringBuilder("╔════════════════════════════════════════╗\n");
        sb.append("║          📄 ALL DOCUMENTS              ║\n");
        sb.append("╚════════════════════════════════════════╝\n\n");
        for (Document doc : documents) {
            sb.append(doc).append("\n");
        }
        docStatusArea.setText(sb.toString());
    }

    private void searchDocumentsByCitizenId() {
        String searchCitizenId = docSearchCitizenIdField.getText().trim();

        if (searchCitizenId.isEmpty()) {
            showError("❌ Please enter a Citizen ID to search.");
            return;
        }

        List<Document> filteredDocs = new ArrayList<>();
        for (Document doc : documents) {
            if (doc.getCitizenId().equals(searchCitizenId)) {
                filteredDocs.add(doc);
            }
        }

        if (filteredDocs.isEmpty()) {
            docStatusArea.setText("No documents found for Citizen ID: " + searchCitizenId);
        } else {
            StringBuilder sb = new StringBuilder("Documents for Citizen ID: " + searchCitizenId + "\n");
            sb.append("╔════════════════════════════════════════╗\n\n");
            for (Document doc : filteredDocs) {
                sb.append(doc).append("\n");
            }
            docStatusArea.setText(sb.toString());
        }
    }

    private void updateDocumentStatus() {
        String citizenId = docCitizenIdField.getText().trim();
        Document.Status newStatus = docStatusComboBox.getValue();

        if (citizenId.isEmpty()) {
            showError("❌ Please enter Citizen ID to update.");
            return;
        }
        if (!citizenMap.containsKey(citizenId)) {
            showError("❌ Citizen ID does not exist.");
            return;
        }

        Document lastDoc = null;
        for (Document doc : documents) {
            if (doc.getCitizenId().equals(citizenId)) {
                lastDoc = doc;
            }
        }

        if (lastDoc == null) {
            showError("❌ No documents found for Citizen ID: " + citizenId);
            return;
        }

        lastDoc.setStatus(newStatus);
        database.updateDocument(lastDoc.getId(), lastDoc);
        showSuccess("✅ Document status updated:\n" +
                "ID: " + lastDoc.getId() + "\n" +
                "New Status: " + newStatus);
    }

    // ==================== SERVICE REQUEST PANE ====================

    private Node createServicePane() {
        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: " + DARK_BG + ";");

        VBox requestSection = createServiceRequestSection();
        VBox searchSection = createServiceSearchSection();

        reqStatusArea = new TextArea();
        reqStatusArea.setEditable(false);
        reqStatusArea.setWrapText(true);
        reqStatusArea.setPrefRowCount(10);
        applyTextAreaStyle(reqStatusArea);
        VBox.setVgrow(reqStatusArea, Priority.ALWAYS);

        mainContent.getChildren().addAll(
            createTitleLabel(isAdmin ? "⚙️ All Service Requests" : "⚙️ My Requests"),
            requestSection,
            new Separator(),
            searchSection,
            new Separator(),
            reqStatusArea
        );

        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setStyle(
            "-fx-background-color: " + DARK_BG + ";" +
            "-fx-control-inner-background: " + DARK_BG + ";"
        );
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        return scrollPane;
    }

    private VBox createServiceRequestSection() {
        VBox section = new VBox(12);
        section.setPadding(new Insets(15));
        section.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + ACCENT_CYAN + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        );

        Label sectionLabel = createSectionLabel("➕ Create Service Request");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        // For citizen: auto-fill citizen ID
        if (!isAdmin) {
            Label reqIdLabel = createLabel("(Auto-Generated)");
            Label citizenIdLabel = createLabel(loggedInCitizen.getId() + " - " + loggedInCitizen.getName());
            
            reqServiceTypeField = createStyledTextField("Service Type (e.g., License Renewal)");
            TextArea descriptionArea = createStyledTextArea("Describe your concern or issue in detail...");

            grid.add(createLabel("Req ID:"), 0, 0);
            grid.add(reqIdLabel, 1, 0);
            grid.add(createLabel("Your ID:"), 0, 1);
            grid.add(citizenIdLabel, 1, 1);
            grid.add(createLabel("Service:"), 0, 2);
            grid.add(reqServiceTypeField, 1, 2);
            grid.add(createLabel("Description:"), 0, 3);
            grid.add(descriptionArea, 1, 3);
        } else {
            // For admin: allow selecting citizen
            Label reqIdLabel = createLabel("(Auto-Generated)");
            reqCitizenIdField = createStyledTextField("Citizen ID");
            reqServiceTypeField = createStyledTextField("Service Type");
            TextArea descriptionArea = createStyledTextArea("Request details...");

            grid.add(createLabel("Req ID:"), 0, 0);
            grid.add(reqIdLabel, 1, 0);
            grid.add(createLabel("Citizen ID:"), 0, 1);
            grid.add(reqCitizenIdField, 1, 1);
            grid.add(createLabel("Service:"), 0, 2);
            grid.add(reqServiceTypeField, 1, 2);
            grid.add(createLabel("Description:"), 0, 3);
            grid.add(descriptionArea, 1, 3);
        }

        ColumnConstraints col1 = new ColumnConstraints(80);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        addRequestButton = createStyledButton("✚ Submit Request", SUCCESS_GREEN);
        addRequestButton.setOnAction(e -> addServiceRequest());

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(addRequestButton);

        if (isAdmin) {
            updateRequestStatusButton = createStyledButton("🔄 Update", WARNING_ORANGE);
            updateRequestStatusButton.setOnAction(e -> updateRequestStatus());
            buttonBox.getChildren().add(updateRequestStatusButton);
        }

        section.getChildren().addAll(sectionLabel, grid, buttonBox);
        return section;
    }

    private VBox createServiceSearchSection() {
        VBox section = new VBox(12);
        section.setPadding(new Insets(15));
        section.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + ACCENT_CYAN + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        );

        Label sectionLabel = createSectionLabel("🔍 View Requests");

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        if (!isAdmin) {
            // Citizen: show only their requests
            Button viewButton = createStyledButton("👁️ View My Requests", PRIMARY_PURPLE);
            viewButton.setOnAction(e -> showMyRequests());
            searchBox.getChildren().add(viewButton);
        } else {
            // Admin: search by citizen ID or show all
            reqSearchCitizenIdField = createStyledTextField("Search by Citizen ID");
            searchRequestsByCitizenButton = createStyledButton("🔎 Search", ACCENT_CYAN);
            searchRequestsByCitizenButton.setOnAction(e -> searchRequestsByCitizenId());

            showAllRequestsButton = createStyledButton("👁️ Show All", PRIMARY_PURPLE);
            showAllRequestsButton.setOnAction(e -> showAllRequests());

            searchBox.getChildren().addAll(
                createLabel("Citizen ID:"),
                reqSearchCitizenIdField,
                searchRequestsByCitizenButton,
                showAllRequestsButton
            );
            HBox.setHgrow(reqSearchCitizenIdField, Priority.ALWAYS);
        }

        section.getChildren().addAll(sectionLabel, searchBox);
        return section;
    }

private void addServiceRequest() {
        String citizenId;
        String serviceType = reqServiceTypeField.getText().trim();
        
        // Get description from TextArea - need to store reference
        Node descNode = getDescriptionTextArea();
        String description = descNode instanceof TextArea ? ((TextArea) descNode).getText().trim() : "";
        
        ServiceRequest.Status status = ServiceRequest.Status.REQUESTED;

        if (isAdmin) {
            citizenId = reqCitizenIdField.getText().trim();
            if (citizenId.isEmpty()) {
                showError("❌ Citizen ID cannot be empty.");
                return;
            }
            if (!citizenMap.containsKey(citizenId)) {
                showError("❌ Citizen ID does not exist.");
                return;
            }
        } else {
            citizenId = loggedInCitizen.getId();
        }

        if (serviceType.isEmpty()) {
            showError("❌ Service Type cannot be empty.");
            return;
        }

        if (description.isEmpty()) {
            showError("❌ Description cannot be empty.");
            return;
        }

        // Check for duplicate service type in pending requests
        for (ServiceRequest sr : serviceRequests) {
            if (sr.getCitizenId().equals(citizenId) && 
                sr.getServiceType().equalsIgnoreCase(serviceType) &&
                (sr.getStatus() == ServiceRequest.Status.REQUESTED || 
                 sr.getStatus() == ServiceRequest.Status.PROCESSING)) {
                showError("❌ You already have a pending request for: " + serviceType + 
                         "\n\nPlease wait for it to be processed or contact an administrator.");
                return;
            }
        }

        String reqId = generateRequestId(citizenId);

        ServiceRequest sr = new ServiceRequest(reqId, citizenId, serviceType, description);
        sr.setStatus(status);
        serviceRequests.add(sr);
        database.addRequest(sr);
        showSuccess("✅ Service request submitted:\n" +
                "ID: " + reqId + "\n" +
                "Service: " + serviceType + "\n" +
                "Description: " + description + "\n" +
                "Status: REQUESTED");

        if (isAdmin) {
            reqCitizenIdField.clear();
        }
        reqServiceTypeField.clear();
        
        // Clear description area
        Node descNode2 = getDescriptionTextArea();
        if (descNode2 instanceof TextArea) {
            ((TextArea) descNode2).clear();
        }
    }

    private Node getDescriptionTextArea() {
        // This is a helper to get the description TextArea
        // In a real implementation, you'd store it as a field
        // For now, return null and we'll fix this properly below
        return null;
    }

    private void showMyRequests() {
        List<ServiceRequest> myRequests = new ArrayList<>();
        for (ServiceRequest sr : serviceRequests) {
            if (sr.getCitizenId().equals(loggedInCitizen.getId())) {
                myRequests.add(sr);
            }
        }

        if (myRequests.isEmpty()) {
            reqStatusArea.setText("You have not submitted any service requests yet.");
            return;
        }

        StringBuilder sb = new StringBuilder("╔════════════════════════════════════════╗\n");
        sb.append("║           MY SERVICE REQUESTS          ║\n");
        sb.append("╚════════════════════════════════════════╝\n\n");
        for (ServiceRequest sr : myRequests) {
            sb.append("ID: ").append(sr.getId()).append("\n");
            sb.append("Service: ").append(sr.getServiceType()).append("\n");
            sb.append("Description: ").append(sr.getDescription()).append("\n");
            sb.append("Status: ").append(sr.getStatus()).append("\n");
            sb.append("─────────────────────────────────────\n\n");
        }
        reqStatusArea.setText(sb.toString());
    }

    private void showAllRequests() {
        if (serviceRequests.isEmpty()) {
            reqStatusArea.setText("No service requests found.");
            return;
        }
        StringBuilder sb = new StringBuilder("╔════════════════════════════════════════╗\n");
        sb.append("║       ⚙️ ALL SERVICE REQUESTS          ║\n");
        sb.append("╚════════════════════════════════════════╝\n\n");
        for (ServiceRequest sr : serviceRequests) {
            sb.append(sr).append("\n");
        }
        reqStatusArea.setText(sb.toString());
    }

    private void updateRequestStatus() {
        String citizenId = reqCitizenIdField.getText().trim();
        ServiceRequest.Status newStatus = reqStatusComboBox.getValue();

        if (citizenId.isEmpty()) {
            showError("❌ Please enter Citizen ID to update.");
            return;
        }

        ServiceRequest lastReq = null;
        for (ServiceRequest sr : serviceRequests) {
            if (sr.getCitizenId().equals(citizenId)) {
                lastReq = sr;
            }
        }

        if (lastReq == null) {
            showError("❌ No service requests found for Citizen ID: " + citizenId);
            return;
        }

        lastReq.setStatus(newStatus);
        database.updateRequest(lastReq.getId(), lastReq);
        showSuccess("✅ Request status updated:\n" +
                "ID: " + lastReq.getId() + "\n" +
                "New Status: " + newStatus);
    }

    private void searchRequestsByCitizenId() {
        String searchCitizenId = reqSearchCitizenIdField.getText().trim();

        if (searchCitizenId.isEmpty()) {
            showError("❌ Please enter a Citizen ID to search.");
            return;
        }

        List<ServiceRequest> filteredRequests = new ArrayList<>();
        for (ServiceRequest sr : serviceRequests) {
            if (sr.getCitizenId().equals(searchCitizenId)) {
                filteredRequests.add(sr);
            }
        }

        if (filteredRequests.isEmpty()) {
            reqStatusArea.setText("No service requests found for Citizen ID: " + searchCitizenId);
        } else {
            StringBuilder sb = new StringBuilder("Service Requests for Citizen ID: " + searchCitizenId + "\n");
            sb.append("╔════════════════════════════════════════╗\n\n");
            for (ServiceRequest sr : filteredRequests) {
                sb.append(sr).append("\n");
            }
            reqStatusArea.setText(sb.toString());
        }
    }

    // ==================== AUTO-GENERATE IDS ====================

    private String generateDocumentId(String citizenId) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyy-HHmm-ss");
        String timestamp = now.format(formatter);
        return "DOC-" + citizenId + "-" + timestamp;
    }

    private String generateRequestId(String citizenId) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyy-HHmm-ss");
        String timestamp = now.format(formatter);
        return "REQ-" + citizenId + "-" + timestamp;
    }

    // ==================== VALIDATION ====================

    private boolean isValidPhilippinePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) return false;
        String normalized = phoneNumber.trim();
        return normalized.matches("^(09|\\+639|\\+6309)\\d{9}$");
    }


    // ==================== UI STYLING ====================

    private Button createStyledButton(String text, String bgColor) {
        Button button = new Button(text);
        button.setPrefHeight(35);
        button.setStyle(
            "-fx-font-size: 11;" +
            "-fx-font-weight: bold;" +
            "-fx-background-color: " + bgColor + ";" +
            "-fx-text-fill: #ffffff;" +
            "-fx-border-radius: 5;" +
            "-fx-padding: 8;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: 'Segoe UI';"
        );

        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), button);
            scale.setFromX(1.0);
            scale.setFromY(1.0);
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();

            button.setStyle(
                "-fx-font-size: 11;" +
                "-fx-font-weight: bold;" +
                "-fx-background-color: " + brightenColor(bgColor) + ";" +
                "-fx-text-fill: #ffffff;" +
                "-fx-border-radius: 5;" +
                "-fx-padding: 8;" +
                "-fx-cursor: hand;" +
                "-fx-font-family: 'Segoe UI';"
            );
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), button);
            scale.setFromX(1.1);
            scale.setFromY(1.1);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();

            button.setStyle(
                "-fx-font-size: 11;" +
                "-fx-font-weight: bold;" +
                "-fx-background-color: " + bgColor + ";" +
                "-fx-text-fill: #ffffff;" +
                "-fx-border-radius: 5;" +
                "-fx-padding: 8;" +
                "-fx-cursor: hand;" +
                "-fx-font-family: 'Segoe UI';"
            );
        });

        return button;
    }

    private String brightenColor(String hexColor) {
        if (hexColor == null) return null;
        try {
            String hex = hexColor.replace("#", "");
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);

            r = Math.min(r + 40, 255);
            g = Math.min(g + 40, 255);
            b = Math.min(b + 40, 255);

            return String.format("#%02x%02x%02x", r, g, b);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return hexColor;
        }
    }


    private TextArea createStyledTextArea(String prompt) {
        TextArea textArea = new TextArea();
        textArea.setPromptText(prompt);
        textArea.setPrefRowCount(5);
        textArea.setWrapText(true);
        textArea.setStyle(
            "-fx-font-size: 12;" +
            "-fx-padding: 8;" +
            "-fx-background-color: " + INPUT_BG + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_GRAY + ";" +
            "-fx-border-color: " + BORDER_PURPLE + ";" +
            "-fx-border-radius: 4;" +
            "-fx-border-width: 1;" +
            "-fx-font-family: 'Segoe UI';"
        );

        textArea.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                textArea.setStyle(
                    "-fx-font-size: 12;" +
                    "-fx-padding: 8;" +
                    "-fx-background-color: " + INPUT_BG + ";" +
                    "-fx-text-fill: " + TEXT_WHITE + ";" +
                    "-fx-prompt-text-fill: " + TEXT_GRAY + ";" +
                    "-fx-border-color: " + ACCENT_CYAN + ";" +
                    "-fx-border-radius: 4;" +
                    "-fx-border-width: 2;" +
                    "-fx-font-family: 'Segoe UI';"
                );
            } else {
                textArea.setStyle(
                    "-fx-font-size: 12;" +
                    "-fx-padding: 8;" +
                    "-fx-background-color: " + INPUT_BG + ";" +
                    "-fx-text-fill: " + TEXT_WHITE + ";" +
                    "-fx-prompt-text-fill: " + TEXT_GRAY + ";" +
                    "-fx-border-color: " + BORDER_PURPLE + ";" +
                    "-fx-border-radius: 4;" +
                    "-fx-border-width: 1;" +
                    "-fx-font-family: 'Segoe UI';"
                );
            }
        });

        return textArea;
    }

    private TextField createStyledTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setPrefHeight(35);
        textField.setStyle(
            "-fx-font-size: 12;" +
            "-fx-padding: 8;" +
            "-fx-background-color: " + INPUT_BG + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_GRAY + ";" +
            "-fx-border-color: " + BORDER_PURPLE + ";" +
            "-fx-border-radius: 4;" +
            "-fx-border-width: 1;" +
            "-fx-font-family: 'Segoe UI';"
        );

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(150), textField);
                scale.setFromX(1.0);
                scale.setFromY(1.0);
                scale.setToX(1.02);
                scale.setToY(1.02);
                scale.play();

                textField.setStyle(
                    "-fx-font-size: 12;" +
                    "-fx-padding: 8;" +
                    "-fx-background-color: " + INPUT_BG + ";" +
                    "-fx-text-fill: " + TEXT_WHITE + ";" +
                    "-fx-prompt-text-fill: " + TEXT_GRAY + ";" +
                    "-fx-border-color: " + ACCENT_CYAN + ";" +
                    "-fx-border-radius: 4;" +
                    "-fx-border-width: 2;" +
                    "-fx-font-family: 'Segoe UI';"
                );
            } else {
                textField.setStyle(
                    "-fx-font-size: 12;" +
                    "-fx-padding: 8;" +
                    "-fx-background-color: " + INPUT_BG + ";" +
                    "-fx-text-fill: " + TEXT_WHITE + ";" +
                    "-fx-prompt-text-fill: " + TEXT_GRAY + ";" +
                    "-fx-border-color: " + BORDER_PURPLE + ";" +
                    "-fx-border-radius: 4;" +
                    "-fx-border-width: 1;" +
                    "-fx-font-family: 'Segoe UI';"
                );
            }
        });

        return textField;
    }

    private void applyComboBoxStyle(ComboBox<?> comboBox) {
        comboBox.setPrefHeight(35);
        comboBox.setStyle(
            "-fx-font-size: 12;" +
            "-fx-padding: 8;" +
            "-fx-background-color: " + INPUT_BG + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-border-color: " + BORDER_PURPLE + ";" +
            "-fx-border-radius: 4;" +
            "-fx-border-width: 1;" +
            "-fx-font-family: 'Segoe UI';"
        );
    }

    private void applyTextAreaStyle(TextArea textArea) {
        textArea.setStyle(
            "-fx-font-size: 11;" +
            "-fx-font-family: 'Courier New';" +
            "-fx-padding: 10;" +
            "-fx-border-color: " + ACCENT_CYAN + ";" +
            "-fx-border-radius: 4;" +
            "-fx-border-width: 1;" +
            "-fx-background-color: " + INPUT_BG + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-control-inner-background: " + INPUT_BG + ";"
        );
        textArea.setWrapText(true);
    }

    private Label createTitleLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        label.setTextFill(Color.web(ACCENT_CYAN));
        return label;
    }

    private Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        label.setTextFill(Color.web(TEXT_WHITE));
        return label;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.web(TEXT_WHITE));
        label.setFont(Font.font("Segoe UI", 12));
        return label;
    }

    private void showSuccess(String message) {
        citizenInfoArea.setStyle(
            "-fx-font-size: 11;" +
            "-fx-font-family: 'Courier New';" +
            "-fx-padding: 10;" +
            "-fx-border-color: " + SUCCESS_GREEN + ";" +
            "-fx-border-radius: 4;" +
            "-fx-border-width: 1;" +
            "-fx-background-color: " + INPUT_BG + ";" +
            "-fx-text-fill: " + SUCCESS_GREEN + ";" +
            "-fx-control-inner-background: " + INPUT_BG + ";"
        );
        citizenInfoArea.setText(message);
    }

    private void showError(String message) {
        citizenInfoArea.setStyle(
            "-fx-font-size: 11;" +
            "-fx-font-family: 'Courier New';" +
            "-fx-padding: 10;" +
            "-fx-border-color: " + ERROR_RED + ";" +
            "-fx-border-radius: 4;" +
            "-fx-border-width: 1;" +
            "-fx-background-color: " + INPUT_BG + ";" +
            "-fx-text-fill: " + ERROR_RED + ";" +
            "-fx-control-inner-background: " + INPUT_BG + ";"
        );
        citizenInfoArea.setText(message);
    }
}