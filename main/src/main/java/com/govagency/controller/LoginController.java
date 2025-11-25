package com.govagency.controller;

import java.util.Map;

import com.govagency.MainApp;
import com.govagency.model.Citizen;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class LoginController {

    private final StackPane root;
    private final MainApp mainApp;
    private final Map<String, Citizen> citizenMap;

    // Enhanced Dark Theme Colors
    private static final String DARK_BG = "#0d1117";
    private static final String CARD_BG = "#161b22";
    private static final String PRIMARY_PURPLE = "#6e40aa";
    private static final String ACCENT_CYAN = "#58a6ff";
    private static final String SUCCESS_GREEN = "#3fb950";
    private static final String ERROR_RED = "#f85149";
    private static final String TEXT_WHITE = "#c9d1d9";
    private static final String TEXT_GRAY = "#8b949e";
    private static final String INPUT_BG = "#0d1117";
    private static final String BORDER_PURPLE = "#30363d";

    private Text messageText;
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;

    public LoginController(MainApp mainApp, Map<String, Citizen> citizenMap) {
        this.mainApp = mainApp;
        this.citizenMap = citizenMap;

        // Background - Full Screen
        root = new StackPane();
        root.setStyle("-fx-background-color: " + DARK_BG + ";");
        root.setPrefSize(1000, 700);

        // Main container - Centered
        VBox mainContainer = createMainContainer();
        
        root.getChildren().add(mainContainer);
    }

    private VBox createMainContainer() {
        VBox mainContainer = new VBox();
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: " + DARK_BG + ";");
        VBox.setVgrow(mainContainer, Priority.ALWAYS);

        // Card container - will be centered with max width
        VBox cardContainer = new VBox();
        cardContainer.setAlignment(Pos.CENTER);
        cardContainer.setMaxWidth(500);
        cardContainer.setStyle("-fx-background-color: " + DARK_BG + ";");

        // Card background
        VBox card = new VBox(30);
        card.setPadding(new Insets(60, 50, 60, 50));
        card.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + ACCENT_CYAN + ";" +
            "-fx-border-radius: 15;" +
            "-fx-border-width: 2;"
        );
        card.setAlignment(Pos.TOP_CENTER);

        // Apply fade-in animation to card
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), card);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        // Header section
        VBox headerSection = createHeaderSection();

        // Form section
        VBox formSection = createFormSection();

        // Info section
        VBox infoSection = createInfoSection();

        // Add to card
        card.getChildren().addAll(
            headerSection,
            formSection,
            loginButton,
            messageText,
            infoSection
        );

        cardContainer.getChildren().add(card);
        mainContainer.getChildren().add(cardContainer);

        return mainContainer;
    }

    private VBox createHeaderSection() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);

        // Main title
        Label titleLabel = new Label("🏛️ GOVERNMENT AGENCY");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web(ACCENT_CYAN));

        // Accent line
        Label accentLine = new Label("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        accentLine.setFont(Font.font("Courier New", 10));
        accentLine.setTextFill(Color.web(PRIMARY_PURPLE));

        // Subtitle
        Label subtitleLabel = new Label("PORTAL");
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        subtitleLabel.setTextFill(Color.web(PRIMARY_PURPLE));

        // Tagline
        Label taglineLabel = new Label("Sign in to your account");
        taglineLabel.setFont(Font.font("Segoe UI", 13));
        taglineLabel.setTextFill(Color.web(TEXT_GRAY));

        header.getChildren().addAll(titleLabel, accentLine, subtitleLabel, taglineLabel);
        return header;
    }

    private VBox createFormSection() {
        VBox form = new VBox(18);

        // Username/Email label
        Label userLabel = new Label("📧 EMAIL ADDRESS");
        userLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        userLabel.setTextFill(Color.web(ACCENT_CYAN));

        usernameField = createStyledTextField("Enter your email address");

        // Password label
        Label passLabel = new Label("🔐 PASSWORD (PHONE NUMBER)");
        passLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        passLabel.setTextFill(Color.web(ACCENT_CYAN));

        passwordField = createStyledPasswordField("Enter your phone number");

        // Login button
        loginButton = createLoginButton();

        // Message text
        messageText = new Text();
        messageText.setFont(Font.font("Segoe UI", 12));
        messageText.setFill(Color.web(ERROR_RED));

        // Add Enter key support to both fields
        usernameField.setOnKeyPressed(this::handleKeyPress);
        passwordField.setOnKeyPressed(this::handleKeyPress);

        form.getChildren().addAll(
            userLabel,
            usernameField,
            passLabel,
            passwordField
        );

        return form;
    }

    private VBox createInfoSection() {
        VBox infoBox = new VBox(12);
        infoBox.setPadding(new Insets(20));
        infoBox.setStyle(
            "-fx-background-color: " + PRIMARY_PURPLE + "22;" +
            "-fx-border-color: " + PRIMARY_PURPLE + ";" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        );

        Label infoTitle = new Label("ℹ️  HOW TO LOGIN");
        infoTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        infoTitle.setTextFill(Color.web(ACCENT_CYAN));

        Label adminInfo = new Label("👨‍💼 ADMIN");
        adminInfo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        adminInfo.setTextFill(Color.web(SUCCESS_GREEN));

        Label adminCred = new Label("Email: admin | Password: 123");
        adminCred.setFont(Font.font("Segoe UI", 10));
        adminCred.setTextFill(Color.web(TEXT_GRAY));

        Label citizenInfo = new Label("👤 CITIZEN");
        citizenInfo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        citizenInfo.setTextFill(Color.web(SUCCESS_GREEN));

        Label citizenCred = new Label("Email: Your registered email | Password: Your phone number");
        citizenCred.setFont(Font.font("Segoe UI", 10));
        citizenCred.setTextFill(Color.web(TEXT_GRAY));
        citizenCred.setWrapText(true);

        infoBox.getChildren().addAll(infoTitle, adminInfo, adminCred, new Separator(), citizenInfo, citizenCred);
        return infoBox;
    }

    private TextField createStyledTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setPrefHeight(45);
        textField.setStyle(
            "-fx-font-size: 13;" +
            "-fx-padding: 12;" +
            "-fx-background-color: " + INPUT_BG + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_GRAY + ";" +
            "-fx-border-color: " + BORDER_PURPLE + ";" +
            "-fx-border-radius: 6;" +
            "-fx-border-width: 1;" +
            "-fx-font-family: 'Segoe UI';"
        );

        // Focus effect
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), textField);
                scale.setFromX(1.0);
                scale.setFromY(1.0);
                scale.setToX(1.02);
                scale.setToY(1.02);
                scale.play();

                textField.setStyle(
                    "-fx-font-size: 13;" +
                    "-fx-padding: 12;" +
                    "-fx-background-color: " + INPUT_BG + ";" +
                    "-fx-text-fill: " + TEXT_WHITE + ";" +
                    "-fx-prompt-text-fill: " + TEXT_GRAY + ";" +
                    "-fx-border-color: " + ACCENT_CYAN + ";" +
                    "-fx-border-radius: 6;" +
                    "-fx-border-width: 2;" +
                    "-fx-font-family: 'Segoe UI';"
                );
            } else {
                textField.setStyle(
                    "-fx-font-size: 13;" +
                    "-fx-padding: 12;" +
                    "-fx-background-color: " + INPUT_BG + ";" +
                    "-fx-text-fill: " + TEXT_WHITE + ";" +
                    "-fx-prompt-text-fill: " + TEXT_GRAY + ";" +
                    "-fx-border-color: " + BORDER_PURPLE + ";" +
                    "-fx-border-radius: 6;" +
                    "-fx-border-width: 1;" +
                    "-fx-font-family: 'Segoe UI';"
                );
            }
        });

        return textField;
    }

    private PasswordField createStyledPasswordField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setPrefHeight(45);

        String baseStyle = 
            "-fx-font-size: 13;" +
            "-fx-padding: 12;" +
            "-fx-background-color: " + INPUT_BG + ";" +
            "-fx-text-fill: " + TEXT_WHITE + ";" +
            "-fx-prompt-text-fill: " + TEXT_GRAY + ";" +
            "-fx-border-radius: 6;" +
            "-fx-font-family: 'Segoe UI';";

        pf.setStyle(baseStyle + "-fx-border-color: " + BORDER_PURPLE + ";" + "-fx-border-width: 1;");

        pf.focusedProperty().addListener((obs, oldVal, newVal) -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), pf);
            scale.setFromX(1.0);
            scale.setFromY(1.0);
            scale.setToX(newVal ? 1.02 : 1.0);
            scale.setToY(newVal ? 1.02 : 1.0);
            scale.play();

            pf.setStyle(baseStyle + 
                "-fx-border-color: " + (newVal ? ACCENT_CYAN : BORDER_PURPLE) + ";" +
                "-fx-border-width: " + (newVal ? 2 : 1) + ";"
            );
        });

        return pf;
    }


    private Button createLoginButton() {
        Button button = new Button("🔓 SIGN IN");
        button.setPrefHeight(48);
        button.setPrefWidth(Double.MAX_VALUE);
        button.setStyle(
            "-fx-font-size: 14;" +
            "-fx-font-weight: bold;" +
            "-fx-background-color: " + ACCENT_CYAN + ";" +
            "-fx-text-fill: #000000;" +
            "-fx-border-radius: 6;" +
            "-fx-padding: 12;" +
            "-fx-cursor: hand;" +
            "-fx-font-family: 'Segoe UI';"
        );

        // Hover effect
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setFromX(1.0);
            scale.setFromY(1.0);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();

            button.setStyle(
                "-fx-font-size: 14;" +
                "-fx-font-weight: bold;" +
                "-fx-background-color: " + "#7ac7ff" + ";" +
                "-fx-text-fill: #000000;" +
                "-fx-border-radius: 6;" +
                "-fx-padding: 12;" +
                "-fx-cursor: hand;" +
                "-fx-font-family: 'Segoe UI';"
            );
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setFromX(1.05);
            scale.setFromY(1.05);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();

            button.setStyle(
                "-fx-font-size: 14;" +
                "-fx-font-weight: bold;" +
                "-fx-background-color: " + ACCENT_CYAN + ";" +
                "-fx-text-fill: #000000;" +
                "-fx-border-radius: 6;" +
                "-fx-padding: 12;" +
                "-fx-cursor: hand;" +
                "-fx-font-family: 'Segoe UI';"
            );
        });

        // Login action
        button.setOnAction(e -> handleLogin());

        return button;
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
            event.consume();
        }
    }

    private void handleLogin() {
        String email = usernameField.getText().trim().toLowerCase();  // Convert to lowercase
        String phonePassword = passwordField.getText().trim();

        if (email.isEmpty() || phonePassword.isEmpty()) {
            showError("⚠️  Please enter both email and password.");
            return;
        }

        // Admin login
        if ("admin".equalsIgnoreCase(email)) {
            if ("123".equals(phonePassword)) {
                showSuccess("✅ Admin login successful!");
                mainApp.showMainApp(true, null);
            } else {
                showError("❌ Invalid admin password.");
                passwordField.clear();
            }
        }
        // Citizen login - email and phone number
        else {
            // Debug: Print all citizens in the map
            System.out.println("Total citizens in map: " + citizenMap.size());
            for (Citizen c : citizenMap.values()) {
                System.out.println("Citizen: " + c.getEmail() + " (ID: " + c.getId() + ")");
            }
            System.out.println("Looking for email: " + email);

            Citizen citizen = null;
            
            // Search for citizen by email (case-insensitive)
            for (Citizen c : citizenMap.values()) {
                if (c.getEmail().trim().toLowerCase().equals(email)) {
                    citizen = c;
                    break;
                }
            }

            if (citizen != null) {
                System.out.println("Found citizen: " + citizen.getName());
                System.out.println("Stored phone: '" + citizen.getNumber() + "'");
                System.out.println("Entered phone: '" + phonePassword + "'");
                
                // Check if the entered password matches the citizen's phone number
                if (citizen.getNumber().trim().equals(phonePassword)) {
                    showSuccess("✅ Welcome, " + citizen.getName() + "!");
                    mainApp.showMainApp(false, citizen);
                } else {
                    showError("❌ Invalid phone number for this email.\n\nExpected: " + citizen.getNumber() + "\nYou entered: " + phonePassword);
                    passwordField.clear();
                }
            } else {
                showError("❌ Email not found in system.\n\nPlease check your email address.");
                usernameField.clear();
                passwordField.clear();
            }
        }
    }

    private void showError(String message) {
        messageText.setText(message);
        messageText.setFill(Color.web(ERROR_RED));

        FadeTransition fade = new FadeTransition(Duration.millis(300), messageText);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    private void showSuccess(String message) {
        messageText.setText(message);
        messageText.setFill(Color.web(SUCCESS_GREEN));

        FadeTransition fade = new FadeTransition(Duration.millis(300), messageText);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    public StackPane getView() {
        return root;
    }
}