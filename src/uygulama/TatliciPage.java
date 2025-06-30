package uygulama;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TatliciPage {

    private Stage stage;
    private int userId;
    private String userName;
    private String userBolum;
    private Map<String, SepetUrun> sepet;
    private Label toplamTutarLabel;

    public TatliciPage(Stage stage, int userId, String userName, String userBolum) {
        this.stage = stage;
        this.userId = userId;
        this.userName = userName;
        this.userBolum = userBolum;
        this.sepet = new HashMap<>();
    }

    public void show() {
        VBox root = new VBox();
        root.setPadding(new Insets(25));
        root.setSpacing(20);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #fff0f5, #ffe4e1);");

        // Kullanıcı bilgisi - bölüm null veya boş ise sadece kullanıcı adı göster
        String kullaniciBilgisi = userName;
        if (userBolum != null && !userBolum.trim().isEmpty()) {
            kullaniciBilgisi += " | Bölüm: " + userBolum;
        }
        Label kullaniciBilgiLabel = new Label(kullaniciBilgisi);
        kullaniciBilgiLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #6b1e4f;");
        kullaniciBilgiLabel.setAlignment(Pos.CENTER);
        kullaniciBilgiLabel.setMaxWidth(Double.MAX_VALUE);

        GridPane menuGrid = new GridPane();
        menuGrid.setHgap(25);
        menuGrid.setVgap(25);
        menuGrid.setAlignment(Pos.CENTER);

        Map<String, Double> urunler = Map.of(
                "baklava", 180.0,
                "sufle", 100.0,
                "sütlaç", 160.0,
                "tiramisu", 130.0,
                "cheesecake", 170.0,
                "spoonful", 170.0
        );

        Map<String, String> imageMap = Map.of(
                "baklava", "baklava.jpeg",
                "sufle", "sufle.jpeg",
                "sütlaç", "sutlac.jpeg",
                "tiramisu", "tiramisu.jpeg",
                "cheesecake", "cheesecake.jpeg",
                "spoonful", "spoonful.jpeg"
        );

        int row = 0, col = 0;

        for (String name : urunler.keySet()) {
            VBox urunKutu = new VBox(8);
            urunKutu.setAlignment(Pos.CENTER);
            urunKutu.setPadding(new Insets(12));
            urunKutu.setStyle(
                    "-fx-background-color: #fff; " +
                    "-fx-border-color: #d1a3c7; " +
                    "-fx-border-radius: 15; " +
                    "-fx-background-radius: 15; " +
                    "-fx-effect: dropshadow(gaussian, rgba(107,30,79,0.25), 6, 0, 0, 3);"
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
            imageView.setFitHeight(110);
            imageView.setFitWidth(110);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(107,30,79,0.15), 8, 0, 0, 2);");

            Label nameLabel = new Label(name.replace("_", " ").toUpperCase());
            nameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 700; -fx-text-fill: #8a2e6e;");

            double price = urunler.get(name);
            Label priceLabel = new Label(String.format("%.2f ₺", price));
            priceLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: #a64c8f;");

            Button addToCartBtn = new Button("Sepete Ekle");
            addToCartBtn.setStyle("-fx-background-color: #b347a3; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            addToCartBtn.setPrefWidth(110);
            addToCartBtn.setPrefHeight(30);

            Button removeFromCartBtn = new Button("Sepetten Çıkar");
            removeFromCartBtn.setStyle("-fx-background-color: #d973bb; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            removeFromCartBtn.setPrefWidth(110);
            removeFromCartBtn.setPrefHeight(30);
            removeFromCartBtn.setVisible(false);

            HBox adetBox = new HBox(8);
            Button minusBtn = new Button("-");
            minusBtn.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
            Label adetLabel = new Label("1");
            adetLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            Button plusBtn = new Button("+");
            plusBtn.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");

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
        toplamTutarLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: 700; -fx-text-fill: #7a2a70;");
        toplamTutarLabel.setAlignment(Pos.CENTER);
        toplamTutarLabel.setMaxWidth(Double.MAX_VALUE);

        Button sepeteGitBtn = new Button("Sepete Git");
        sepeteGitBtn.setStyle(
                "-fx-background-color: #8a3eb3; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12 40 12 40; " +
                "-fx-cursor: hand; " +
                "-fx-background-radius: 25;"
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
                "-fx-background-color: transparent; " +
                "-fx-border-color: #8a3eb3; " +
                "-fx-text-fill: #8a3eb3; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12 40 12 40; " +
                "-fx-background-radius: 25; " +
                "-fx-cursor: hand;"
        );
        geriDonBtn.setOnAction(e -> new LokantaSecimPage(stage, userId, userName, userBolum).show());

        VBox toplamTutarBox = new VBox(15, toplamTutarLabel, sepeteGitBtn, geriDonBtn);
        toplamTutarBox.setAlignment(Pos.CENTER);
        toplamTutarBox.setPadding(new Insets(30, 0, 20, 0));

        ScrollPane scrollPane = new ScrollPane(menuGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(650);
        scrollPane.setStyle("-fx-background-color: transparent;");

        root.getChildren().addAll(kullaniciBilgiLabel, scrollPane, toplamTutarBox);

        Scene scene = new Scene(root, 900, 820);
        stage.setScene(scene);
        stage.setTitle("Ayışığı Tatlı Dükkanı");
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