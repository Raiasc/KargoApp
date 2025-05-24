package application;

import java.io.IOException;
import java.net.URL;
//import java.sql.Connection;
//import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.util.Duration;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class KullaniciGirisController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button GirisButton;

    @FXML
    private Button GirisButton1;

    @FXML
    private Button KayitOl1Button;

    @FXML
    private Button KayitOlButton;

    @FXML
    private Pane backPanel;

    @FXML
    private TabPane tabPane;
    
    @FXML
    private TextField email1_tf;

    @FXML
    private TextField email_tf;

    @FXML
    private PasswordField sifre1_pf;

    @FXML
    private PasswordField sifre_pf;

    @FXML
    private TextField id_tf;
    
    @FXML
    private Button KargomNeredeButton;

    @FXML
    void KargomNeredeButton_click(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/KargomNerede.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Kargom Nerede?");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText("Sayfa açılamadı.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    @FXML 
    void GirisButton1_click(ActionEvent event) {
        String eposta = email1_tf.getText().trim();
        String password = sifre1_pf.getText().trim();

        if (eposta.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Lütfen tüm alanları doldurun");
            return;
        }

        // E-posta geçerlilik kontrolü
        if (!eposta.contains("@") || !eposta.contains(".")) { 
            JOptionPane.showMessageDialog(null, "Lütfen geçerli bir e-posta adresi girin!");
            return;
        }

        try {
            VeriTabani vt = new VeriTabani();
            vt.baglan();

            String sorgu = "SELECT * FROM musteriler WHERE eposta = ? AND password = ?";
            PreparedStatement ps = vt.baglanti.prepareStatement(sorgu);
            ps.setString(1, eposta);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("KargoEkraniController.fxml"));
                Parent root = loader.load();

                KargoEkraniController controller = loader.getController();
                controller.setUserData(eposta);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Kullanıcı Paneli");
                stage.show();

                ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
            } else {
                JOptionPane.showMessageDialog(null, "Email veya şifre hatalı!");
            }

            rs.close();
            ps.close();
            vt.kapat();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Hata: " + e.getMessage());
        }
    }
    @FXML
    void GirisButton_click(ActionEvent event) {
        String email = email_tf.getText().trim();
        String sifre = sifre_pf.getText().trim();

        if (email.isEmpty() || sifre.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Lütfen tüm alanları doldurun");
            return;
        }

        // E-posta kontrolü
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(null, "Lütfen geçerli bir e-posta adresi girin!");
            return;
        }

        try {
            VeriTabani vt = new VeriTabani();
            vt.baglan();

            String sorgu = "SELECT * FROM kullanicilar WHERE email = ? AND sifre = ?";
            PreparedStatement ps = vt.baglanti.prepareStatement(sorgu);
            ps.setString(1, email);
            ps.setString(2, sifre);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("KullaniciGirisYapti.fxml"));
                Parent root = loader.load();

                KullaniciGirisYaptiController controller = loader.getController();
                controller.setUserData(email);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Kullanıcı Paneli");
                stage.show();

                ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
            } else {
                JOptionPane.showMessageDialog(null, "Email veya şifre hatalı!");
            }

            rs.close();
            ps.close();
            vt.kapat();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Hata: " + e.getMessage());
        }
    }

    @FXML
    void KayitOl1Button_click(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("KullaniciPanel2.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Kullanıcı Kayıt Paneli");
            stage.setScene(new Scene(root));
            stage.show();

            // Eğer mevcut sahneyi kapatmak istersen:
            ((Node) event.getSource()).getScene().getWindow().hide();

        } catch (IOException e) {
            e.printStackTrace();
        }
    } 

    @FXML
    void KayitOlButton_click(ActionEvent event) {
        try {
            // Yeni sayfayı yükle (KullaniciPanel.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("KullaniciPanel.fxml"));
            Parent root = loader.load();
            
            // Yeni sahneyi oluştur
            Scene scene = new Scene(root);
            
            // Mevcut stage (pencere)yi al ve yeni sahneyi yerleştir
            Stage stage = (Stage) KayitOlButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Sayfa yüklenirken hata oluştu: " + e.getMessage());
        }
    }
  
    @FXML
    void initialize() {
    	Image image = new Image(getClass().getResource("/navigation-1048294_1280.jpg").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));

        backPanel.setBackground(new Background(bgImage));
        Timeline blinkTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> KargomNeredeButton.setStyle("-fx-background-color: red;")),
                new KeyFrame(Duration.seconds(2), e -> KargomNeredeButton.setStyle("-fx-background-color: transparent;"))
            );
            blinkTimeline.setCycleCount(Animation.INDEFINITE);
            blinkTimeline.play();
    }

}
