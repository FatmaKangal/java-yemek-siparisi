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

public class MenuPage {

    private Stage stage;
    private Map<String, SepetUrun> sepet;
    private Label toplamTutarLabel;

    private int userId;
    private String userName;
    private String userBolum;

    public MenuPage(Stage stage) {
        this.stage = stage;
        this.sepet = new HashMap<>();
    }

    public void setUserId(int userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setUserBolum(String userBolum) { this.userBolum = userBolum; }

    public void show() {
        VBox root = new VBox();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #fdf6e3;");  // Açık krem

        GridPane menuGrid = new GridPane();
        menuGrid.setHgap(25);
        menuGrid.setVgap(25);
        menuGrid.setAlignment(Pos.CENTER);

        Map<String, Double> urunler = Map.ofEntries(
            Map.entry("ayran", 40.0),
            Map.entry("baklava", 250.0),
            Map.entry("çoban_salata", 100.0),
            Map.entry("çay", 20.0),
            Map.entry("döner", 150.0),
            Map.entry("et_döner", 170.0),
            Map.entry("hamburger", 180.0),
            Map.entry("kızartma", 60.0),
            Map.entry("kola", 40.0),
            Map.entry("künefe", 200.0),
            Map.entry("limonata", 50.0),
            Map.entry("mercimek_çorbası", 100.0),
            Map.entry("meyve_suyu", 25.0),
            Map.entry("sezar", 250.0),
            Map.entry("soğan_halkası", 80.0),
            Map.entry("sütlaç", 150.0),
            Map.entry("türk_kahvesi", 60.0),
            Map.entry("yayla_çorbası", 100.0)
        );

        Map<String, String> imageMap = Map.ofEntries(
            Map.entry("ayran", "ayran.jpeg"),
            Map.entry("baklava", "baklava.jpeg"),
            Map.entry("çoban_salata", "coban_salatasi.jpeg"),
            Map.entry("çay", "cay.jpeg"),
            Map.entry("döner", "doner.jpeg"),
            Map.entry("et_döner", "et_doner.jpeg"),
            Map.entry("hamburger", "hamburger.jpeg"),
            Map.entry("kızartma", "kizartma.jpeg"),
            Map.entry("kola", "kola.jpeg"),
            Map.entry("künefe", "kunefe.jpeg"),
            Map.entry("limonata", "limonata.jpeg"),
            Map.entry("mercimek_çorbası", "mercimek_corbasi.jpeg"),
            Map.entry("meyve_suyu", "meyve_suyu.jpeg"),
            Map.entry("sezar", "sezar.jpeg"),
            Map.entry("soğan_halkası", "sogan_halkasi.jpeg"),
            Map.entry("sütlaç", "sutlac.jpeg"),
            Map.entry("türk_kahvesi", "turk_kahvesi.jpeg"),
            Map.entry("yayla_çorbası", "yayla_corbasi.jpeg")
        );

        int row = 0, col = 0;
        for (String key : urunler.keySet()) {
            VBox urunKutu = new VBox(8);
            urunKutu.setAlignment(Pos.CENTER);
            urunKutu.setPadding(new Insets(12));
            urunKutu.setPrefWidth(250);
            urunKutu.setStyle(
                "-fx-background-color: #fff8dc;" +  // çok açık krem-beyaz
                "-fx-border-radius: 15; " +
                "-fx-background-radius: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 3);"
            );

            String imagePath = "/images/" + imageMap.getOrDefault(key, "default.jpg");
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
            imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");

            // Ürün ismini kullanıcıya gösterilecek şekilde düzenle
            String displayName = prettifyName(key);
            Label nameLabel = new Label(displayName);
            nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 600; -fx-text-fill: #3e2723;");

            double price = urunler.get(key);
            Label priceLabel = new Label(String.format("%.2f ₺", price));
            priceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #6d4c41;");

            Button addToCartBtn = new Button("Sepete Ekle");
            addToCartBtn.setStyle(
                "-fx-background-color: #8bc34a; " +  // yeşil ton
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 15; " +
                "-fx-padding: 6 20;"
            );

            Button removeFromCartBtn = new Button("Çıkar");
            removeFromCartBtn.setStyle(
                "-fx-background-color: #e57373; " +  // kırmızı ton
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 15; " +
                "-fx-padding: 6 20;"
            );
            removeFromCartBtn.setVisible(false);

            HBox adetBox = new HBox(8);
            Button minusBtn = new Button("-");
            minusBtn.setStyle(buttonSmallStyle());
            Label adetLabel = new Label("1");
            adetLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            Button plusBtn = new Button("+");
            plusBtn.setStyle(buttonSmallStyle());

            adetBox.getChildren().addAll(minusBtn, adetLabel, plusBtn);
            adetBox.setAlignment(Pos.CENTER);
            adetBox.setVisible(false);
            minusBtn.setDisable(true);

            addToCartBtn.setOnAction(e -> {
                sepet.put(key, new SepetUrun(key, price, 1));
                adetLabel.setText("1");
                addToCartBtn.setVisible(false);
                adetBox.setVisible(true);
                removeFromCartBtn.setVisible(true);
                minusBtn.setDisable(true);
                updateToplamTutar();
            });

            plusBtn.setOnAction(e -> {
                SepetUrun urun = sepet.get(key);
                if (urun != null) {
                    urun.setAdet(urun.getAdet() + 1);
                    adetLabel.setText(String.valueOf(urun.getAdet()));
                    minusBtn.setDisable(false);
                    updateToplamTutar();
                }
            });

            minusBtn.setOnAction(e -> {
                SepetUrun urun = sepet.get(key);
                if (urun != null && urun.getAdet() > 1) {
                    urun.setAdet(urun.getAdet() - 1);
                    adetLabel.setText(String.valueOf(urun.getAdet()));
                    if (urun.getAdet() == 1) {
                        minusBtn.setDisable(true);
                    }
                    updateToplamTutar();
                }
            });

            removeFromCartBtn.setOnAction(e -> {
                sepet.remove(key);
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

        ScrollPane scrollPane = new ScrollPane(menuGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setPadding(new Insets(0, 0, 30, 0));

        toplamTutarLabel = new Label("Toplam Tutar: 0.00 ₺");
        toplamTutarLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #4e342e;");
        toplamTutarLabel.setAlignment(Pos.CENTER);

        Button sepeteGitBtn = new Button("Sepete Git");
        sepeteGitBtn.setStyle(
            "-fx-background-color: #6d4c41; " +  // koyu kahverengi
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-padding: 10 35; " +
            "-fx-background-radius: 20;"
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
            "-fx-background-color: #b39d8d; " +  // açık kahverengi
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-padding: 10 35; " +
            "-fx-background-radius: 20;"
        );
        geriDonBtn.setOnAction(e -> {
            LokantaSecimPage lokantaSecimPage = new LokantaSecimPage(stage, userId, userName, userBolum);
            lokantaSecimPage.show();
        });

        HBox buttonBox = new HBox(30, geriDonBtn, sepeteGitBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox toplamTutarBox = new VBox(15, toplamTutarLabel, buttonBox);
        toplamTutarBox.setAlignment(Pos.CENTER);
        toplamTutarBox.setPadding(new Insets(15, 0, 10, 0));

        VBox content = new VBox(35, scrollPane, toplamTutarBox);
        content.setAlignment(Pos.TOP_CENTER);

        root.getChildren().add(content);

        Scene scene = new Scene(root, 940, 820);
        stage.setScene(scene);
        stage.setTitle("Yemek Menüsü");
        stage.show();
    }

    private void updateToplamTutar() {
        double toplam = 0.0;
        for (SepetUrun urun : sepet.values()) {
            toplam += urun.getFiyat() * urun.getAdet();
        }
        toplamTutarLabel.setText(String.format("Toplam Tutar: %.2f ₺", toplam));
    }

    private String prettifyName(String rawName) {
        // Alt çizgileri boşluk yap, kelimelerin baş harfini büyük yap
        String[] parts = rawName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) sb.append(part.substring(1));
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    private String buttonSmallStyle() {
        return "-fx-background-color: #a1887f; -fx-text-fill: white; -fx-font-weight: bold; " +
               "-fx-background-radius: 10; -fx-padding: 4 12; -fx-font-size: 14;";
    }
}