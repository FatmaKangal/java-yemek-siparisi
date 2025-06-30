package uygulama;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.*;

public class HesapSayfasi {

    private Stage stage;
    private int userId;
    private String userName;
    private String userBolum;

    private TextField adField;
    private TextField soyadField;
    private TextField telefonField;
    private TextField emailField;
    private TextArea adresArea;

    private Button duzenleBtn;
    private Button kaydetBtn;
    private Button iptalBtn;
    private Button geriDonBtn;

    private String originalAd, originalSoyad, originalTelefon, originalEmail, originalAdres;

    private final String DB_URL = "jdbc:mysql://localhost:3306/foodordersystem";
    private final String DB_USERNAME = "root";
    private final String DB_PASSWORD = "";

    public HesapSayfasi(Stage stage, int userId, String userName, String userBolum) {
        this.stage = stage;
        this.userId = userId;
        this.userName = userName;
        this.userBolum = userBolum;
    }

    public void show() {
        VBox root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #D4F0F0, #86C5C3);");

        Label titleLabel = new Label("Hesap Bilgilerim");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: 900; -fx-text-fill: #0B525B; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4,0,0,2);");

        GridPane formGrid = new GridPane();
        formGrid.setVgap(15);
        formGrid.setHgap(20);
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setMaxWidth(360);

        // Label ve inputları oluştur ve stil ver
        Label adLabel = createFormLabel("Ad:");
        adField = new TextField();
        adField.setPromptText("Adınızı girin");
        adField.setPrefWidth(250);
        adField.setStyle("-fx-text-overrun: clip;");

        Label soyadLabel = createFormLabel("Soyad:");
        soyadField = new TextField();
        soyadField.setPromptText("Soyadınızı girin");
        soyadField.setPrefWidth(250);
        soyadField.setStyle("-fx-text-overrun: clip;");

        Label telefonLabel = createFormLabel("Telefon:");
        telefonField = new TextField();
        telefonField.setPromptText("05xx xxx xx xx");
        telefonField.setPrefWidth(250);
        telefonField.setStyle("-fx-text-overrun: clip;");

        Label emailLabel = createFormLabel("E-mail:");
        emailField = new TextField();
        emailField.setPromptText("ornek@mail.com");
        emailField.setPrefWidth(250);
        emailField.setStyle("-fx-text-overrun: clip;");

        Label adresLabel = createFormLabel("Adres:");
        adresArea = new TextArea();
        adresArea.setPromptText("Adresinizi girin");
        adresArea.setPrefRowCount(4);
        adresArea.setWrapText(true);
        adresArea.setPrefWidth(250);
        adresArea.setStyle("-fx-text-overrun: clip;");

        // GridPane'e yerleştir
        formGrid.add(adLabel, 0, 0);
        formGrid.add(adField, 1, 0);
        formGrid.add(soyadLabel, 0, 1);
        formGrid.add(soyadField, 1, 1);
        formGrid.add(telefonLabel, 0, 2);
        formGrid.add(telefonField, 1, 2);
        formGrid.add(emailLabel, 0, 3);
        formGrid.add(emailField, 1, 3);
        formGrid.add(adresLabel, 0, 4);
        formGrid.add(adresArea, 1, 4);

        // Butonlar
        duzenleBtn = new Button("Düzenle");
        kaydetBtn = new Button("Kaydet");
        iptalBtn = new Button("İptal");
        geriDonBtn = new Button("◀ Geri Dön");

        styleButton(duzenleBtn, "#1F8A70", "#145D48");
        styleButton(kaydetBtn, "#388E3C", "#2E7D32");
        styleButton(iptalBtn, "#D32F2F", "#B71C1C");
        styleButton(geriDonBtn, "#0B525B", "#073737");

        kaydetBtn.setVisible(false);
        iptalBtn.setVisible(false);

        HBox actionButtons = new HBox(15, duzenleBtn, kaydetBtn, iptalBtn);
        actionButtons.setAlignment(Pos.CENTER);

        HBox backButtonBox = new HBox(geriDonBtn);
        backButtonBox.setAlignment(Pos.CENTER_LEFT);

        root.getChildren().addAll(titleLabel, formGrid, actionButtons, backButtonBox);

        duzenleBtn.setOnAction(e -> {
            setFieldsEditable(true);
            duzenleBtn.setVisible(false);
            kaydetBtn.setVisible(true);
            iptalBtn.setVisible(true);
        });

        iptalBtn.setOnAction(e -> {
            restoreOriginalValues();
            setFieldsEditable(false);
            duzenleBtn.setVisible(true);
            kaydetBtn.setVisible(false);
            iptalBtn.setVisible(false);
        });

        kaydetBtn.setOnAction(e -> {
            if (validateInputs()) {
                if (updateUserData()) {
                    setFieldsEditable(false);
                    duzenleBtn.setVisible(true);
                    kaydetBtn.setVisible(false);
                    iptalBtn.setVisible(false);
                    saveOriginalValues();
                    showAlert(Alert.AlertType.INFORMATION, "Bilgiler başarıyla güncellendi.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Güncelleme sırasında hata oluştu!");
                }
            }
        });

        geriDonBtn.setOnAction(e -> {
            LokantaSecimPage lokantaSecimPage = new LokantaSecimPage(stage, userId, userName, userBolum);
            lokantaSecimPage.show();
        });

        loadUserData();

        Scene scene = new Scene(root, 450, 560);
        stage.setScene(scene);
        stage.setTitle("Hesap Sayfası");
        stage.show();
    }

    private Label createFormLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: 700; -fx-text-fill: #074B4B; -fx-font-size: 14px;");
        return label;
    }

    private void styleButton(Button button, String baseColor, String hoverColor) {
        button.setStyle("-fx-background-color: " + baseColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 22;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + hoverColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 22;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + baseColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 22;"));
        button.setCursor(javafx.scene.Cursor.HAND);
        button.setEffect(new DropShadow(5, Color.rgb(0,0,0,0.2)));
    }

    private void setFieldsEditable(boolean editable) {
        adField.setEditable(editable);
        soyadField.setEditable(editable);
        telefonField.setEditable(editable);
        emailField.setEditable(editable);
        adresArea.setEditable(editable);

        String bgColor = editable ? "white" : "#ECECEC";
        adField.setStyle("-fx-background-color: " + bgColor + "; -fx-border-radius: 6; -fx-background-radius: 6;");
        soyadField.setStyle("-fx-background-color: " + bgColor + "; -fx-border-radius: 6; -fx-background-radius: 6;");
        telefonField.setStyle("-fx-background-color: " + bgColor + "; -fx-border-radius: 6; -fx-background-radius: 6;");
        emailField.setStyle("-fx-background-color: " + bgColor + "; -fx-border-radius: 6; -fx-background-radius: 6;");
        adresArea.setStyle("-fx-background-color: " + bgColor + "; -fx-border-radius: 6; -fx-background-radius: 6;");
    }

    private void saveOriginalValues() {
        originalAd = adField.getText();
        originalSoyad = soyadField.getText();
        originalTelefon = telefonField.getText();
        originalEmail = emailField.getText();
        originalAdres = adresArea.getText();
    }

    private void restoreOriginalValues() {
        adField.setText(originalAd);
        soyadField.setText(originalSoyad);
        telefonField.setText(originalTelefon);
        emailField.setText(originalEmail);
        adresArea.setText(originalAdres);
    }

    private void loadUserData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "SELECT username, last_name, phone, email, address FROM users WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                adField.setText(rs.getString("username"));
                soyadField.setText(rs.getString("last_name"));
                telefonField.setText(rs.getString("phone"));
                emailField.setText(rs.getString("email"));
                adresArea.setText(rs.getString("address"));

                saveOriginalValues();
            } else {
                showAlert(Alert.AlertType.WARNING, "Kullanıcı bulunamadı!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Veritabanı hatası oluştu!");
        }
    }

    private boolean updateUserData() {
        String yeniAd = adField.getText().trim();
        String yeniSoyad = soyadField.getText().trim();
        String yeniTelefon = telefonField.getText().trim();
        String yeniEmail = emailField.getText().trim();
        String yeniAdres = adresArea.getText().trim();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            String sql = "UPDATE users SET username = ?, last_name = ?, phone = ?, email = ?, address = ? WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, yeniAd);
            ps.setString(2, yeniSoyad);
            ps.setString(3, yeniTelefon);
            ps.setString(4, yeniEmail);
            ps.setString(5, yeniAdres);
            ps.setInt(6, userId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean validateInputs() {
        if (adField.getText().trim().isEmpty() || soyadField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Ad ve Soyad boş bırakılamaz!");
            return false;
        }
        if (!emailField.getText().trim().isEmpty() && !emailField.getText().contains("@")) {
            showAlert(Alert.AlertType.WARNING, "Geçerli bir e-mail adresi giriniz!");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
