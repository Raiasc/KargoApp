package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class KullaniciPanelController2 {

    @FXML
    private ResourceBundle resources;
    
    @FXML
    private Pane KKpane;

    @FXML
    private URL location;

    @FXML
    private Button KAnasayfa;

    @FXML
    private ImageView KFoto;
    
    @FXML
    private Button KKayitolButton;

    @FXML
    private Button KResimEkle;

    @FXML
    private TextField email1_tf;

    @FXML
    private TextField isim1_tf;

    @FXML
    private PasswordField sifre1_pf;

    @FXML
    private TextField soyisim1_tf;

    @FXML
    private TextField TC_tf;
    

    @FXML
    void KAnasayfa_click(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/KullaniciGiris.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Sayfa yüklenirken hata oluştu: " + e.getMessage());
        }
    } 

    @FXML
    void KKayitolButton_click(ActionEvent event) {
        try { 
            String ad = isim1_tf.getText().trim();
            String soyad = soyisim1_tf.getText().trim();
            String eposta = email1_tf.getText().trim();
            String password = sifre1_pf.getText().trim(); 
            String TC = TC_tf.getText().trim();

            // 1. Boş alan kontrolü
            if (ad.isEmpty() || soyad.isEmpty() || eposta.isEmpty() || password.isEmpty() || TC.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Lütfen tüm alanları doldurun.");
                return;
            }

            // ✅ TC uzunluğu kontrolü eklendi
            if (TC.length() != 11) {
                JOptionPane.showMessageDialog(null, "TC kimlik numarası 11 haneli olmalıdır.");
                return;
            }

            // 2. E-posta formatı kontrolü
            if (!eposta.contains("@") || !eposta.contains(".")) {
                JOptionPane.showMessageDialog(null, "Lütfen geçerli bir e-posta adresi girin.");
                return;
            }

            // 3. Resim kontrolü 
            if (KFoto.getImage() == null) {
                JOptionPane.showMessageDialog(null, "Lütfen bir resim ekleyin.");
                return;
            }

            // 4. Veritabanına bağlanma
            VeriTabani vt = new VeriTabani(); 
            Connection conn = vt.baglan();

            // 5. Aynı TC veya e-posta kayıtlı mı?
            String kontrolSorgu = "SELECT * FROM musteriler WHERE eposta = ? OR TC = ?";
            PreparedStatement kontrolPs = conn.prepareStatement(kontrolSorgu);
            kontrolPs.setString(1, eposta);
            kontrolPs.setString(2, TC);
            ResultSet rs = kontrolPs.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Bu e-posta veya TC zaten kayıtlı.");
                rs.close();
                kontrolPs.close();
                vt.kapat();
                return;
            }

            rs.close();
            kontrolPs.close();

            // 6. Resim dosya adı (TC'ye göre adlandırıyoruz)
            String resimAdi = TC + ".png";
            File hedef = new File("src/resimler/" + resimAdi);
            if (!hedef.getParentFile().exists()) {
                hedef.getParentFile().mkdirs();
            }

            // 8. Kaydı veritabanına ekle
            String sql = "INSERT INTO musteriler (TC, ad, soyad, eposta, password, resim) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, TC);
            ps.setString(2, ad);
            ps.setString(3, soyad);
            ps.setString(4, eposta);
            ps.setString(5, password); 
            ps.setString(6, resimAdi);

            int sonuc = ps.executeUpdate();
            if (sonuc > 0) {
                JOptionPane.showMessageDialog(null, "Kullanıcı kaydı başarıyla yapıldı.");

                // Formu temizleme 
                isim1_tf.clear();
                soyisim1_tf.clear();
                email1_tf.clear(); 
                sifre1_pf.clear();
                TC_tf.clear();
                KFoto.setImage(null);
            } else {
                JOptionPane.showMessageDialog(null, "Kayıt başarısız.");
            }

            ps.close();
            vt.kapat();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Veritabanı hatası: " + e.getMessage());
        }
    
    
    }    @FXML
    void KResimEkle_click(ActionEvent event) { 
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Resim Seç");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Resim Dosyaları", "*.png", "*.jpg", "*.jpeg"));

        File dosya = fileChooser.showOpenDialog(null);
        if (dosya != null) {
            KFoto.setImage(new Image(dosya.toURI().toString())); // ImageView'a göster
        }
    }
    @FXML
    void initialize() {
    	Image image = new Image(getClass().getResource("/pexels-pixabay-235985.jpg").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));

        KKpane.setBackground(new Background(bgImage));
        //bu kodu pane adi değiştirip fotoğrafı ise isteğe bağlı değiştirip arka plan olarak her stage e ekleyebiliriz.
    }

    }


