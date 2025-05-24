package application;

import java.sql.*;
import javax.swing.JOptionPane;

public class VeriTabani {
    public Connection baglanti;
    private String databaseismi = "nakliye2";
    private String kullaniciadi = "root";
    private String kullanicisifre = "";
    
    public Connection baglan() {
        String url = "jdbc:mysql://localhost:3306/" + databaseismi;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            baglanti = DriverManager.getConnection(url, kullaniciadi, kullanicisifre);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Bağlantı hatası: " + e.getMessage());
        }
        return baglanti;
    }
    
    // Yeni eklenen kayıt metodu
    public boolean kullaniciKayitEkle(String isim, String soyisim, String email, String sifre, String tc) {
        String sorgu = "INSERT INTO kullanicilar (isim, soyisim, email, sifre, tc) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = baglanti.prepareStatement(sorgu)) {
            ps.setString(1, isim);
            ps.setString(2, soyisim);
            ps.setString(3, email);
            ps.setString(4, sifre);
            ps.setString(5, tc);
             
            int etkilenenSatir = ps.executeUpdate();
            return etkilenenSatir > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Kayıt hatası: " + e.getMessage());
            return false;
        }
    }
    
    public void kapat() {
        try {
            if (baglanti != null && !baglanti.isClosed()) {
                baglanti.close();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Kapatma hatası: " + e.getMessage());
        }
    }
}