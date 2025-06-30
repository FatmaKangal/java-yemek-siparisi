package uygulama;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FastFoodPage {

    private Stage stage;
    private int userId;
    private String userName;
    private Map<String, SepetUrun> sepet;
    private Label toplamTutarLabel;

    public FastFoodPage(Stage stage, int userId, String userName) {
        this.stage = stage;
        this.userId = userId;
        this.userName = userName;
        this.sepet = new HashMap<>();
    }

    public void show() {
        VBox root = new VBox();
        root.setPadding(new Insets(20));
        root.setSpacing(15);
        root.setStyle("-fx-background-color: #fff8e7;");

        // Sadece kullanıcı adı gösteriliyor, bölüm kısmı kaldırıldı
        Label kullaniciBilgiLabel = new Label("Hoşgeldiniz, " + userName + "!");
        kullaniciBilgiLabel.setStyle(
            "-fx-font-size: 22px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #333;"
        );
        kullaniciBilgiLabel.setAlignment(Pos.CENTER);
        kullaniciBilgiLabel.setMaxWidth(Double.MAX_VALUE);
        
        GridPane menuGrid = new GridPane();
        menuGrid.setHgap(20);
        menuGrid.setVgap(20);
        menuGrid.setAlignment(Pos.CENTER);

        Map<String, Double> urunler = Map.of(
                "patates_kızartması", 70.0,
                "patso", 120.0,
                "kızartılmış_tavuk", 130.0,
                "kumpir", 140.0,
                "hamburger", 170.0,
                "pizza", 200.0
        );

        Map<String, String> imageMap = Map.of(
                "patates_kızartması", "potato.jpeg",
                "patso", "patso.jpeg",
                "kızartılmış_tavuk", "kizartilmistavuk.jpeg",
                "kumpir", "kumpir.jpeg",
                "hamburger", "hamburgerr.jpeg",
                "pizza", "pizza.jpeg"
        );

        int row = 0, col = 0;

        for (String name : urunler.keySet()) {
            VBox urunKutu = new VBox(8);
            urunKutu.setAlignment(Pos.CENTER);
            urunKutu.setPadding(new Insets(15));
            urunKutu.setStyle(
                    "-fx-background-color: #fafafa; " +
                    "-fx-border-color: #ddd; " +
                    "-fx-border-radius: 12; " +
                    "-fx-background-radius: 12; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2);"
            );

            String imagePath = "/images/" + imageMap.getOrDefault(name, "default.jpg");
            URL imageUrl = getClass().getResource(imagePath);
            Image image = (imageUrl != null) ? new Image(imageUrl.toExternalForm()) : null;
            if (image == null) {
                URL defaultUrl = getClass().getResource("/images/default.jpg");
                if (defaultUrl != null) {
                    image = new Image(defaultUrl.toExternalForm());
                }
            }

            ImageView imageView = (image != null) ? new ImageView(image) : new ImageView();
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);

            Label nameLabel = new Label(name.replace("_", " ").toUpperCase());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #222;");

            double price = urunler.get(name);
            Label priceLabel = new Label(price + " ₺");
            priceLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");

            Button addToCartBtn = new Button("Sepete Ekle");
            addToCartBtn.setStyle("-fx-background-color: #ff6f00; -fx-text-fill: white; -fx-font-weight: bold;");
            Button removeFromCartBtn = new Button("Sepetten Çıkar");
            removeFromCartBtn.setVisible(false);
            removeFromCartBtn.setStyle("-fx-background-color: #c62828; -fx-text-fill: white; -fx-font-weight: bold;");

            HBox adetBox = new HBox(5);
            Button minusBtn = new Button("-");
            Label adetLabel = new Label("1");
            Button plusBtn = new Button("+");

            adetBox.getChildren().addAll(minusBtn, adetLabel, plusBtn);
            adetBox.setAlignment(Pos.CENTER);
            adetBox.setVisible(false);
            minusBtn.setDisable(true);

            // Sepete Ekle butonu tıklanınca
            addToCartBtn.setOnAction(e -> {
                sepet.put(name, new SepetUrun(name, price, 1));
                adetLabel.setText("1");
                addToCartBtn.setVisible(false);
                adetBox.setVisible(true);
                removeFromCartBtn.setVisible(true);
                minusBtn.setDisable(true);
                updateToplamTutar();
            });

            // "+" butonu ile adet artırma
            plusBtn.setOnAction(e -> {
                SepetUrun urun = sepet.get(name);
                if (urun != null) {
                    urun.setAdet(urun.getAdet() + 1);
                    adetLabel.setText(String.valueOf(urun.getAdet()));
                    if (urun.getAdet() > 1) {
                        minusBtn.setDisable(false);
                    }
                    updateToplamTutar();
                }
            });

            // "-" butonu ile adet azaltma
            minusBtn.setOnAction(e -> {
                SepetUrun urun = sepet.get(name);
                if (urun != null && urun.getAdet() > 1) {
                    urun.setAdet(urun.getAdet() - 1);
                    adetLabel.setText(String.valueOf(urun.getAdet()));
                    if (urun.getAdet() == 1) {
                        minusBtn.setDisable(true);
                    }
                    updateToplamTutar();
                }
            });

            // Sepetten çıkar butonu tıklanınca
            removeFromCartBtn.setOnAction(e -> {
                sepet.remove(name);
                adetLabel.setText("0");
                adetBox.setVisible(false);
                addToCartBtn.setVisible(true);
                removeFromCartBtn.setVisible(false);
                minusBtn.setDisable(true);
                updateToplamTutar();
            });

            urunKutu.getChildren().addAll(imageView, nameLabel, priceLabel, addToCartBtn, removeFromCartBtn, adetBox);
            menuGrid.add(urunKutu, col, row);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }

        toplamTutarLabel = new Label("Toplam Tutar: 0.00 ₺");
        toplamTutarLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #444;");
        toplamTutarLabel.setAlignment(Pos.CENTER);
        toplamTutarLabel.setMaxWidth(Double.MAX_VALUE);

        Button sepeteGitBtn = new Button("Sepete Git");
        sepeteGitBtn.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-padding: 10px 30px; " +
            "-fx-background-color: #1976d2; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold;"
        );
        sepeteGitBtn.setOnAction(e -> {
            if (sepet.isEmpty()) return;

            SepetPage sepetPage = new SepetPage(userId, sepet);
            Stage sepetStage = new Stage();
            try {
                sepetPage.start(sepetStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button geriDonBtn = new Button("Geri Dön");
        geriDonBtn.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-padding: 10px 30px; " +
            "-fx-background-color: #757575; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold;"
        );
        geriDonBtn.setOnAction(e -> {
            new LokantaSecimPage(stage, userId, userName, null).show(); // Bölüm null olarak geçiliyor
        });

        VBox toplamTutarBox = new VBox(10, toplamTutarLabel, sepeteGitBtn, geriDonBtn);
        toplamTutarBox.setAlignment(Pos.CENTER);
        toplamTutarBox.setPadding(new Insets(20, 0, 10, 0));

        ScrollPane scrollPane = new ScrollPane(menuGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(600);

        root.getChildren().addAll(kullaniciBilgiLabel, scrollPane, toplamTutarBox);

        Scene scene = new Scene(root, 900, 800);
        stage.setScene(scene);
        stage.setTitle("Çıtır Fast Food");
        stage.show();
    }

    private void updateToplamTutar() {
        double toplam = 0.0;
        for (SepetUrun urun : sepet.values()) {
            toplam += urun.getFiyat() * urun.getAdet();
        }
        toplamTutarLabel.setText(String.format("Toplam Tutar: %.2f ₺", toplam));
    }
}