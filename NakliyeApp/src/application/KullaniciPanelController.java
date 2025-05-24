package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;

public class KullaniciPanelController {
	
	@FXML private Button ASayfaButton;   
	@FXML private Pane AdminPane;
    @FXML private TextField Aemail_tf;
    @FXML private TextField Aisim_tf;
    @FXML private Button AkayitButton;
    @FXML private PasswordField Asifre_pf;
    @FXML private TextField Asoyisim_tf;
    @FXML private TextField Atc_tf;
    @FXML private Button AresimEkleButton;
    @FXML private ImageView imageW;
    @FXML
    private TextField ID_tf;

    private File secilenResim;
    
    @FXML
    void ASayfaButton_click(ActionEvent event) {
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
    void AresimEkleButton_click(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Resim Seç");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Resim Dosyaları", "*.png", "*.jpg", "*.jpeg"));

        File dosya = fileChooser.showOpenDialog(null);
        if (dosya != null) { 
            secilenResim = dosya;
            imageW.setImage(new Image(dosya.toURI().toString())); // imageView'e göster
        }
    }

    @FXML
    void AkayitButton_click(ActionEvent event) {
        try {
            String id = ID_tf.getText().trim();
            String isim = Aisim_tf.getText().trim();
            String soyisim = Asoyisim_tf.getText().trim();
            String email = Aemail_tf.getText().trim();
            String sifre = Asifre_pf.getText().trim();
            String tc = Atc_tf.getText().trim();

            if (id.isEmpty() || isim.isEmpty() || soyisim.isEmpty() || email.isEmpty() || sifre.isEmpty() || tc.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Lütfen tüm alanları doldurun.");
                return;
            }

            // TC kimlik numarası 11 karakter mi kontrolü s
            if (tc.length() != 11) {
                JOptionPane.showMessageDialog(null, "TC kimlik numarası 11 haneli olmalıdır.");
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(null, "Lütfen geçerli bir e-posta adresi girin!");
                return;
            }

            if (secilenResim == null) {
                JOptionPane.showMessageDialog(null, "Lütfen bir resim seçin.");
                return;
            }

            VeriTabani vt = new VeriTabani();
            Connection conn = vt.baglan();

            // Önce aynı email var mı diye kontrol et
            String kontrolSorgu = "SELECT * FROM kullanicilar WHERE email = ?";
            PreparedStatement kontrolPS = conn.prepareStatement(kontrolSorgu);
            kontrolPS.setString(1, email);
            ResultSet rs = kontrolPS.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Bu e-posta zaten kayıtlı.");
                rs.close();
                kontrolPS.close();
                vt.kapat();
                return;
            }

            rs.close();
            kontrolPS.close();

            // Kayıt işlemi
            String resimAdi = tc + ".png"; 
            File hedef = new File("src/resimler/" + resimAdi);

            if (!hedef.getParentFile().exists()) {
                hedef.getParentFile().mkdirs();
            }

            Files.copy(secilenResim.toPath(), hedef.toPath(), StandardCopyOption.REPLACE_EXISTING);

            String sorgu = "INSERT INTO kullanicilar (id, isim, soyisim, email, sifre, tc, resim) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sorgu);
            ps.setString(1, id);
            ps.setString(2, isim);
            ps.setString(3, soyisim);
            ps.setString(4, email);
            ps.setString(5, sifre);
            ps.setString(6, tc);
            ps.setString(7, resimAdi);

            int sonuc = ps.executeUpdate();

            if (sonuc > 0) {
                JOptionPane.showMessageDialog(null, "Admin kaydı başarıyla eklendi.");
                temizle();
            } else {
                JOptionPane.showMessageDialog(null, "Kayıt başarısız.");
            }

            ps.close();
            vt.kapat();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Veritabanı hatası: " + e.getMessage());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Dosya hatası: " + e.getMessage());
        }
    }
private void temizle() {
		// TODO Auto-generated method stub
		
	}
@FXML  
void initialize() {
	Image image = new Image(getClass().getResource("/pexels-pixabay-235985.jpg").toExternalForm());
    BackgroundImage bgImage = new BackgroundImage(image,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));

    AdminPane.setBackground(new Background(bgImage));
}

}