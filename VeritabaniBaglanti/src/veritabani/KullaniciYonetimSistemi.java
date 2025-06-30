package veritabani;

import java.sql.*;
import java.util.Scanner;

public class KullaniciYonetimSistemi {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/foodordersystem"; // Veritabanı adı
        String dbUsername = "root"; // MySQL kullanıcı adı
        String dbPassword = ""; // MySQL şifre

        Scanner scanner = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            System.out.println("Veritabanı bağlantısı başarılı!");
            int secim;

            do {
                System.out.println("\n--- KULLANICI YÖNETİM SİSTEMİ ---");
                System.out.println("1 - Yeni Kayıt Ekle");
                System.out.println("2 - Kayıtları Listele");
                System.out.println("3 - Kayıt Güncelle");
                System.out.println("4 - Kayıt Sil");
                System.out.println("5 - Çıkış");
                System.out.print("Seçiminiz: ");
                secim = Integer.parseInt(scanner.nextLine());

                switch (secim) {
                    case 1:
                        System.out.print("Kullanıcı Adı: ");
                        String username = scanner.nextLine();
                        System.out.print("Şifre: ");
                        String password = scanner.nextLine();
                        System.out.print("Telefon: ");
                        String phone = scanner.nextLine();

                        // Kullanıcı ekleme
                        String insertSql = "INSERT INTO users (username, password, phone) VALUES (?, ?, ?)";
                        PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                        insertStmt.setString(1, username);
                        insertStmt.setString(2, password);
                        insertStmt.setString(3, phone);
                        insertStmt.executeUpdate();
                        insertStmt.close();
                        System.out.println("Kayıt eklendi.");
                        break;

                    case 2:
                        // Kayıtları listeleme
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT * FROM users");
                        System.out.println("\n--- Kayıtlar ---");
                        while (rs.next()) {
                            System.out.println(rs.getInt("user_id") + " - " +
                                               rs.getString("username") + " - " +
                                               rs.getString("phone"));
                        }
                        rs.close();
                        stmt.close();
                        break;

                    case 3:
                        System.out.print("Güncellenecek Kullanıcı ID: ");
                        int guncelleId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Yeni Kullanıcı Adı: ");
                        String yeniUsername = scanner.nextLine();
                        System.out.print("Yeni Şifre: ");
                        String yeniPassword = scanner.nextLine();
                        System.out.print("Yeni Telefon: ");
                        String yeniPhone = scanner.nextLine();

                        // Kullanıcı güncelleme
                        String updateSql = "UPDATE users SET username = ?, password = ?, phone = ? WHERE user_id = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setString(1, yeniUsername);
                        updateStmt.setString(2, yeniPassword);
                        updateStmt.setString(3, yeniPhone);
                        updateStmt.setInt(4, guncelleId);
                        int guncellenen = updateStmt.executeUpdate();
                        updateStmt.close();

                        if (guncellenen > 0) {
                            System.out.println("Kayıt güncellendi.");
                        } else {
                            System.out.println("ID bulunamadı.");
                        }
                        break;

                    case 4:
                        System.out.print("Silinecek Kullanıcı ID: ");
                        int silId = Integer.parseInt(scanner.nextLine());

                        // Kullanıcı silme
                        String deleteSql = "DELETE FROM users WHERE user_id = ?";
                        PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                        deleteStmt.setInt(1, silId);
                        int silinen = deleteStmt.executeUpdate();
                        deleteStmt.close();

                        if (silinen > 0) {
                            System.out.println("Kayıt silindi.");
                        } else {
                            System.out.println("ID bulunamadı.");
                        }
                        break;

                    case 5:
                        System.out.println("Programdan çıkılıyor...");
                        break;

                    default:
                        System.out.println("Geçersiz seçim!");
                        break;
                }

            } while (secim != 5);

        } catch (SQLException e) {
            System.out.println("Veritabanı hatası: " + e.getMessage());
        }

        scanner.close();
    }
}