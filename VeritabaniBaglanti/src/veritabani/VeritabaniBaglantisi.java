package veritabani;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class VeritabaniBaglantisi {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/foodordersystem";
        String kullanici = "root";
        String sifre = "";

        try {
            Connection conn = DriverManager.getConnection(url, kullanici, sifre);
            System.out.println("Bağlantı başarılı!");
            
            // Veri Ekleme
            Statement stmt = conn.createStatement();
            String sqlEkle = "INSERT INTO users (username, password, phone) VALUES ('Selin', '1234', '5551234567')";
            stmt.executeUpdate(sqlEkle);
            System.out.println("Kayıt eklendi.");
            
            // Veri Okuma
            String sqlOku = "SELECT * FROM users";
            ResultSet rs = stmt.executeQuery(sqlOku);
            
            while (rs.next()) {
                // 'user_id' yerine doğru sütun adını kullandığınızdan emin olun
                int id = rs.getInt("user_id");  // 'id' yerine 'user_id' kullanılıyor
                String username = rs.getString("username");
                String password = rs.getString("password");
                String phone = rs.getString("phone");
                System.out.println(id + " - " + username + " - " + password + " - " + phone);
            }

            conn.close();
        } catch (SQLException e) {
            System.out.println("Hata oluştu!");
            e.printStackTrace();
        }
    }
}