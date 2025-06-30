package uygulama;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.regex.Pattern;

public class RegisterPage {

    private byte[] imageBytes = null;

    public void start(Stage primaryStage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #E0F7FA;"); // Canlı açık mavi arka plan

        Label title = new Label("Yeni Kullanıcı Kaydı");
        title.setFont(Font.font("Arial", 28));
        title.setTextFill(Color.web("#00796B"));
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);

        // Form alanları GridPane ile hizalanacak
        GridPane formGrid = new GridPane();
        formGrid.setVgap(15);
        formGrid.setHgap(15);
        formGrid.setAlignment(Pos.CENTER);

        // Label ve TextField'lar
        Label usernameLabel = new Label("Ad:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Adınızı girin");

        Label lastNameLabel = new Label("Soyad:");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Soyadınızı girin");

        Label phoneLabel = new Label("Telefon Numarası:");
        TextField phoneField = new TextField();
        phoneField.setPromptText("05XXXXXXXXX");

        Label addressLabel = new Label("Adres:");
        TextField addressField = new TextField();
        addressField.setPromptText("Adresinizi girin");

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Email adresinizi girin");

        Label passwordLabel = new Label("Şifre:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Şifrenizi girin");

        // Stil verelim label'lara
        for (Label lbl : new Label[]{usernameLabel, lastNameLabel, phoneLabel, addressLabel, emailLabel, passwordLabel}) {
            lbl.setFont(Font.font("Arial", 14));
            lbl.setTextFill(Color.web("#004D40"));
        }

        // Form alanlarını grid'e ekle
        formGrid.add(usernameLabel, 0, 0);
        formGrid.add(usernameField, 1, 0);
        formGrid.add(lastNameLabel, 0, 1);
        formGrid.add(lastNameField, 1, 1);
        formGrid.add(phoneLabel, 0, 2);
        formGrid.add(phoneField, 1, 2);
        formGrid.add(addressLabel, 0, 3);
        formGrid.add(addressField, 1, 3);
        formGrid.add(emailLabel, 0, 4);
        formGrid.add(emailField, 1, 4);
        formGrid.add(passwordLabel, 0, 5);
        formGrid.add(passwordField, 1, 5);

        // Fotoğraf seçme bölümü
        Button selectImageButton = new Button("Profil Fotoğrafı Seç");
        selectImageButton.setStyle("-fx-background-color: #00796B; -fx-text-fill: white; -fx-font-weight: bold;");
        ImageView imageView = new ImageView();
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        HBox imageBox = new HBox(15, selectImageButton, imageView);
        imageBox.setAlignment(Pos.CENTER_LEFT);
        imageBox.setPadding(new Insets(10, 0, 10, 0));

        selectImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Profil Fotoğrafı Seç");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Resim Dosyaları", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );

            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                try {
                    imageBytes = Files.readAllBytes(selectedFile.toPath());
                    Image image = new Image(new FileInputStream(selectedFile));
                    imageView.setImage(image);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Fotoğraf yüklenirken hata oluştu.").showAndWait();
                }
            }
        });

        // Kayıt ve Geri Dön butonları
        Button registerButton = new Button("Kayıt Ol");
        registerButton.setStyle("-fx-background-color: #00796B; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");
        registerButton.setPrefWidth(150);

        Button backButton = new Button("Geri Dön");
        backButton.setStyle("-fx-background-color: #004D40; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");
        backButton.setPrefWidth(150);

        HBox buttonBox = new HBox(20, registerButton, backButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Label infoLabel = new Label();
        infoLabel.setTextFill(Color.RED);
        infoLabel.setWrapText(true);

        // Register butonu işlemi
        registerButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || lastName.isEmpty() || phone.isEmpty() || address.isEmpty()
                    || email.isEmpty() || password.isEmpty()) {
                infoLabel.setTextFill(Color.RED);
                infoLabel.setText("Lütfen tüm alanları doldurun.");
                return;
            }

            if (!isValidEmail(email)) {
                infoLabel.setTextFill(Color.RED);
                infoLabel.setText("Geçerli bir email adresi giriniz.");
                return;
            }

            if (!isValidPhoneNumber(phone)) {
                infoLabel.setTextFill(Color.RED);
                infoLabel.setText("Telefon numarası 11 haneli olmalı ve 05 ile başlamalıdır.");
                return;
            }

            if (imageBytes == null) {
                infoLabel.setTextFill(Color.RED);
                infoLabel.setText("Lütfen profil fotoğrafı seçin.");
                return;
            }

            // Veritabanına kayıt
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/foodordersystem", "root", "");
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO users (username, last_name, phone, address, email, password, profile_image) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?)")) {

                stmt.setString(1, username);
                stmt.setString(2, lastName);
                stmt.setString(3, phone);
                stmt.setString(4, address);
                stmt.setString(5, email);
                stmt.setString(6, password);
                stmt.setBytes(7, imageBytes);

                int affectedRows = stmt.executeUpdate();

                if (affectedRows > 0) {
                    infoLabel.setTextFill(Color.GREEN);
                    infoLabel.setText("Kayıt başarılı! Giriş yapabilirsiniz.");
                    new LoginPage().start(primaryStage);
                } else {
                    infoLabel.setTextFill(Color.RED);
                    infoLabel.setText("Kayıt yapılamadı, lütfen tekrar deneyin.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                infoLabel.setTextFill(Color.RED);
                infoLabel.setText("Veritabanı hatası: " + ex.getMessage());
            }
        });

        backButton.setOnAction(e -> {
            try {
                new LoginPage().start(primaryStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        root.getChildren().addAll(title, formGrid, imageBox, buttonBox, infoLabel);

        Scene scene = new Scene(root, 420, 700);
        primaryStage.setTitle("Kayıt Ol");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private boolean isValidEmail(String email) {
        String regex = "^[\\w-\\.]+@[\\w-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(regex, email);
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("05\\d{9}");
    }
}
