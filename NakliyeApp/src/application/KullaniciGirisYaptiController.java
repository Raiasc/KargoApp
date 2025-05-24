package application;

import javafx.scene.control.PasswordField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import javax.swing.JOptionPane;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
//import javafx.fxml.FXMLLoader;

public class KullaniciGirisYaptiController {
	@FXML
    private Button AdminKayıtlarıbutton;

    @FXML
    private Button Ekanasayfabutton;
    @FXML
    void AdminKayıtlarıbutton_click(ActionEvent event) { 

    }

  //kullanici giris yani anasayfaya gitme için!!!!!!!!!!!!!!!!!!!!!
    @FXML 
    void Ekanasayfabutton_click(ActionEvent event) {
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
    public static class MusteriModel {
        private String tc, ad, soyad, eposta, password, resim;
  
        public String getTc() { return tc; }
        public void setTc(String tc) { this.tc = tc; }
        public String getAd() { return ad; }
        public void setAd(String ad) { this.ad = ad; }
        public String getSoyad() { return soyad; }
        public void setSoyad(String soyad) { this.soyad = soyad; }
        public String getEposta() { return eposta; }
        public void setEposta(String eposta) { this.eposta = eposta; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getResim() { return resim; }
        public void setResim(String resim) { this.resim = resim; }
    }

    @FXML private Button btara, btekle, btguncelle, btresimekle, btsil;
    @FXML private TableColumn<MusteriModel, String> clm_Ad, clm_Soyad, clm_Eposta, clm_TC, clm_Password, clm_resim;
    @FXML private PasswordField pfPassword;
    @FXML private ImageView resim;
    @FXML private TableView<MusteriModel> tablo;
    @FXML private TextField tfAd, tfSoyad, tfEposta, tfTC, tfara;
    @FXML private Label welcomeLabel;

    private String tiklananTC, resimDosyaYolu;
    private VeriTabani vTabani = new VeriTabani();
    private PreparedStatement pStatement;
    private ResultSet rSet;

    @FXML
    void initialize() {
        clm_Ad.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getAd()));
        clm_Soyad.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getSoyad()));
        clm_Eposta.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEposta()));
        clm_TC.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTc()));
        clm_Password.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPassword()));
        clm_resim.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getResim()));
        tabloVeriYukle();
    }

    @FXML
    void btara_click(ActionEvent event) {
        String aranan = tfara.getText().trim().toLowerCase();
        if (aranan.isEmpty()) {
            tabloVeriYukle();
            return;
        }

        ObservableList<MusteriModel> filtre = FXCollections.observableArrayList();
        for (MusteriModel m : tablo.getItems()) {
            if (m.getAd().toLowerCase().contains(aranan) ||
                m.getSoyad().toLowerCase().contains(aranan) ||
                m.getEposta().toLowerCase().contains(aranan) ||
                m.getTc().toLowerCase().contains(aranan)) {
                filtre.add(m);
            }
        }
        tablo.setItems(filtre);
    } 

    @FXML
    void btekle_click(ActionEvent event) {
        try {
            String ad = tfAd.getText().trim();
            String soyad = tfSoyad.getText().trim();
            String eposta = tfEposta.getText().trim();
            String tc = tfTC.getText().trim();
            String password = pfPassword.getText().trim();

            if (ad.isEmpty() || soyad.isEmpty() || eposta.isEmpty() || tc.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Tüm alanlar zorunludur.");
                return;
            }

            if (resimDosyaYolu == null) {
                JOptionPane.showMessageDialog(null, "Lütfen bir resim seçin.");
                return;
            }

            String resimAdi = tc + ".png";
            File hedef = new File("src/resimler/" + resimAdi);
            hedef.getParentFile().mkdirs();
            Files.copy(new File(resimDosyaYolu).toPath(), hedef.toPath(), StandardCopyOption.REPLACE_EXISTING);

            vTabani.baglan();
            String sql = "INSERT INTO musteriler (ad, soyad, eposta, TC, password, resim) VALUES (?, ?, ?, ?, ?, ?)";
            pStatement = vTabani.baglanti.prepareStatement(sql);
            pStatement.setString(1, ad);
            pStatement.setString(2, soyad);
            pStatement.setString(3, eposta);
            pStatement.setString(4, tc);
            pStatement.setString(5, password);
            pStatement.setString(6, resimAdi);

            if (pStatement.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Kayıt eklendi.");
                temizle();
                tabloVeriYukle();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Hata: " + e.getMessage());
        }
    }

    @FXML
    void btguncelle_click(ActionEvent event) {
        try {
            String ad = tfAd.getText().trim();
            String soyad = tfSoyad.getText().trim();
            String eposta = tfEposta.getText().trim();
            String tc = tfTC.getText().trim();
            String password = pfPassword.getText().trim();
            String resimAdi = tc + ".png";

            if (resimDosyaYolu != null) {
                File hedef = new File("src/resimler/" + resimAdi);
                hedef.getParentFile().mkdirs();
                Files.copy(new File(resimDosyaYolu).toPath(), hedef.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            vTabani.baglan();
            String sql = "UPDATE musteriler SET ad=?, soyad=?, eposta=?, TC=?, password=?, resim=? WHERE TC=?";
            pStatement = vTabani.baglanti.prepareStatement(sql);
            pStatement.setString(1, ad);
            pStatement.setString(2, soyad);
            pStatement.setString(3, eposta);
            pStatement.setString(4, tc);
            pStatement.setString(5, password);
            pStatement.setString(6, resimAdi);
            pStatement.setString(7, tiklananTC);

            if (pStatement.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Güncelleme başarılı.");
                temizle();
                tabloVeriYukle();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btsil_click(ActionEvent event) {
        try {
            vTabani.baglan();
            String sql = "DELETE FROM musteriler WHERE TC=?";
            pStatement = vTabani.baglanti.prepareStatement(sql);
            pStatement.setString(1, tiklananTC);

            if (pStatement.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Silme başarılı.");
                temizle();
                tabloVeriYukle();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btresimekle_click(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Resim Dosyaları", "*.jpg", "*.png"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            resimDosyaYolu = file.getAbsolutePath();
            resim.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    void tablo_click(MouseEvent event) {
        MusteriModel m = tablo.getSelectionModel().getSelectedItem();
        if (m != null) {
            tfAd.setText(m.getAd());
            tfSoyad.setText(m.getSoyad());
            tfEposta.setText(m.getEposta());
            tfTC.setText(m.getTc());
            pfPassword.setText(m.getPassword());
            tiklananTC = m.getTc();
            try {
                if (m.getResim() != null) {
                    Image image = new Image("file:src/resimler/" + m.getResim());
                    resim.setImage(image);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } 
    }

    private void tabloVeriYukle() {
        ObservableList<MusteriModel> liste = FXCollections.observableArrayList();
        try {
            vTabani.baglan();
            Statement stmt = vTabani.baglanti.createStatement();
            rSet = stmt.executeQuery("SELECT * FROM musteriler");

            while (rSet.next()) {
                MusteriModel m = new MusteriModel();
                m.setAd(rSet.getString("ad"));
                m.setSoyad(rSet.getString("soyad"));
                m.setEposta(rSet.getString("eposta"));
                m.setTc(rSet.getString("TC"));
                m.setPassword(rSet.getString("password"));
                m.setResim(rSet.getString("resim"));
                liste.add(m); 
            }
            tablo.setItems(liste);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void temizle() {
        tfAd.clear();
        tfSoyad.clear();
        tfEposta.clear();
        tfTC.clear();
        pfPassword.clear();
        resim.setImage(null);
        resimDosyaYolu = null;
    }

	public void setUserData(String email) {
		// TODO Auto-generated method stub
		
	}
}
