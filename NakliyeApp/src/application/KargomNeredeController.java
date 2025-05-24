package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;

public class KargomNeredeController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label AdresL;

    @FXML
    private Label AliciL;

    @FXML
    private Label DurumL;

    @FXML
    private Label KargoNoL;
    
    @FXML
    private Pane KargoPane;


    @FXML
    private Button KargoTakipNoButton;

    @FXML
    private TextField KargoTakipNo_tf;

    @FXML
    private ProgressBar PBkargodurum;

    @FXML
    private Label TeslimatL;

    @FXML
    private Label gondericiL;

    @FXML
    private Label kargoturuL;

    public void setTakipNo(String takipNo) {
        KargoTakipNo_tf.setText(takipNo);
        sorgulaVeGoster(takipNo); 
    }

    @FXML
    void KargoTakipNoButton_click(ActionEvent event) {
        String takipNo = KargoTakipNo_tf.getText().trim();
        sorgulaVeGoster(takipNo);
    }

    private void sorgulaVeGoster(String takipNo) {
        if (takipNo.isEmpty()) {
            KargoNoL.setText("Takip numarası giriniz!");
            temizleLabeller();
            PBkargodurum.setProgress(0);
            PBkargodurum.setStyle("-fx-accent: gray;");
            return;
        }

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nakliye2", "root", "");
            String sql = "SELECT * FROM kargo WHERE takipNo = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, takipNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String gonderici = rs.getString("gonderici");
                String alici = rs.getString("alici");
                String adres = rs.getString("adres");
                String kargoTipi = rs.getString("kargoTipi");
                String tasimaTuru = rs.getString("tasimaTuru");
                String durum = rs.getString("durum");

                KargoNoL.setText("Takip No: " + takipNo);
                gondericiL.setText("Gönderici: " + gonderici);
                AliciL.setText("Alıcı: " + alici);
                AdresL.setText("Adres: " + adres);
                kargoturuL.setText("Kargo Türü: " + kargoTipi);
                TeslimatL.setText("Teslimat: " + tasimaTuru);
                DurumL.setText("Durum: " + durum);

                // Duruma göre ilerleme ve renk
                switch (durum.toLowerCase()) {
                    case "hazırlanıyor":
                        PBkargodurum.setProgress(0.25);
                        PBkargodurum.setStyle("-fx-accent: gray;");
                        break;
                    case "yolda":
                        PBkargodurum.setProgress(0.5);
                        PBkargodurum.setStyle("-fx-accent: orange;");
                        break;
                    case "teslim edildi":
                        PBkargodurum.setProgress(1.0);
                        PBkargodurum.setStyle("-fx-accent: green;");
                        break;
                    default:
                        PBkargodurum.setProgress(0.0);
                        PBkargodurum.setStyle("-fx-accent: gray;");
                        break;
                }

            } else {
                KargoNoL.setText("Kargo bulunamadı!");
                temizleLabeller();
                PBkargodurum.setProgress(0);
                PBkargodurum.setStyle("-fx-accent: gray;");
            }
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            KargoNoL.setText("Hata: " + e.getMessage());
            temizleLabeller();
            PBkargodurum.setProgress(0);
            PBkargodurum.setStyle("-fx-accent: gray;");
        }
    }

    private void temizleLabeller() {
        gondericiL.setText("");
        AliciL.setText("");
        AdresL.setText("");
        kargoturuL.setText("");
        TeslimatL.setText("");
        DurumL.setText("");
    }

    @FXML
    void initialize() {
    	Image image = new Image(getClass().getResource("/pexels-ketut-subiyanto-4246120.jpg").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));

        KargoPane.setBackground(new Background(bgImage));
        PBkargodurum.setProgress(0);
        PBkargodurum.setStyle("-fx-accent: gray;");
    }
}