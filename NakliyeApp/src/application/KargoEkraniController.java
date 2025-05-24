package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

public class KargoEkraniController {

    @FXML private ResourceBundle resources;
    @FXML private URL location;

    
    @FXML private ImageView KargoResmiView;
    @FXML private Pane Panepan;
    @FXML private TableView<Kargo> TableKargoPanel;
    @FXML private Button TakipnogosterButton;
    @FXML private Button anaSayfaButton;
    
    @FXML
    private Button DeleteButton;
    @FXML
    private TextField Durum_tf;
    @FXML
    private Button DuzenleButton;
    @FXML
    private Button searchButton;
    @FXML
    private TextField Search_tf;


    @FXML private TextField adres_tf;
    @FXML private TextField alici_tf;
    @FXML private TableColumn<Kargo, String> clm_TakipNo;
    @FXML private TableColumn<Kargo, String> clm_adres;
    @FXML private TableColumn<Kargo, String> clm_alici;
    @FXML private TableColumn<Kargo, String> clm_durum;
    @FXML private TableColumn<Kargo, String> clm_gonderici;
    @FXML private TableColumn<Kargo, String> clm_kargotipi;
    @FXML private TableColumn<Kargo, String> clm_tasimaturu;

    @FXML private TextField gonderici_tf;
    @FXML private Button kargoResmiButton;

    @FXML private ContextMenu kargotipi;
    @FXML private TextField kargotipi_tf;

    @FXML private Label takipNoLabel;
    @FXML private TextField takipNo_tf;
    @FXML private TextField takipnogoster_tf;

    @FXML private ContextMenu tasimaturu;
    @FXML private TextField tasimaturu_tf;

    @FXML private Button yenikargoEkleButton;

    private final String DB_URL = "jdbc:mysql://localhost:3306/nakliye2";
    private final String DB_USER = "root";
    private final String DB_PASS = "";

    @FXML
    void initialize() {
        // Arka plan resmi
        Image image = new Image(getClass().getResource("/pexels-tima-miroshnichenko-6169056.jpg").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
        Panepan.setBackground(new Background(bgImage));

        // Table column'ları için veri bağlama
        clm_TakipNo.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTakipNo()));
        clm_gonderici.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getGonderici()));
        clm_alici.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getAlici()));
        clm_adres.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getAdres()));
        clm_kargotipi.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getKargoTipi()));
        clm_tasimaturu.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTasimaTuru()));
        clm_durum.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getDurum()));

        // Kargo tipi seçimleri
        MenuItem tip1 = new MenuItem("Standart");
        MenuItem tip2 = new MenuItem("Hassas");
        tip1.setOnAction(e -> kargotipi_tf.setText("Standart"));
        tip2.setOnAction(e -> kargotipi_tf.setText("Hassas"));
        kargotipi.getItems().addAll(tip1, tip2);

        // Taşıma türü seçimleri 
        MenuItem tasima1 = new MenuItem("Kücük araba");
        MenuItem tasima2 = new MenuItem("transit");

        tasima1.setOnAction(e -> tasimaturu_tf.setText("Kücük araba"));
        tasima2.setOnAction(e -> tasimaturu_tf.setText("transit"));
       
        tasimaturu.getItems().addAll(tasima1, tasima2);

        // Sağ tıklama ile ContextMenu açma
        kargotipi_tf.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                kargotipi.show(kargotipi_tf, e.getScreenX(), e.getScreenY());
            } 
        });
        tasimaturu_tf.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                tasimaturu.show(tasimaturu_tf, e.getScreenX(), e.getScreenY());
            }
        });

        // Verileri yükleme işlemi
        verileriYukle();
        TableKargoPanel.setOnMouseClicked(event -> {
            Kargo secili = TableKargoPanel.getSelectionModel().getSelectedItem();
            if (secili != null) {
                takipNo_tf.setText(secili.getTakipNo());
                gonderici_tf.setText(secili.getGonderici());
                alici_tf.setText(secili.getAlici());
                adres_tf.setText(secili.getAdres());
                kargotipi_tf.setText(secili.getKargoTipi());
                tasimaturu_tf.setText(secili.getTasimaTuru());
                Durum_tf.setText(secili.getDurum());
            }
        });
    }
    @FXML
    void TableKargoPanel_click(MouseEvent event) {
        Kargo secili = TableKargoPanel.getSelectionModel().getSelectedItem();
        if (secili != null) {
            takipNo_tf.setText(secili.getTakipNo()); 
            gonderici_tf.setText(secili.getGonderici());
            alici_tf.setText(secili.getAlici());
            adres_tf.setText(secili.getAdres());
            kargotipi_tf.setText(secili.getKargoTipi());
            tasimaturu_tf.setText(secili.getTasimaTuru());
            Durum_tf.setText(secili.getDurum());
        }
    }
    @FXML
    void DeleteButton_click(ActionEvent event) {
        Kargo seciliKargo = TableKargoPanel.getSelectionModel().getSelectedItem();
        if (seciliKargo == null) {
            gosterUyari("Hata", "Lütfen silmek için bir kayıt seçin.", AlertType.WARNING);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "DELETE FROM kargo WHERE takipNo = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, seciliKargo.getTakipNo());
            ps.executeUpdate();
 
            TableKargoPanel.getItems().remove(seciliKargo);
            gosterUyari("Başarılı", "Kargo başarıyla silindi.", AlertType.INFORMATION);
            temizleAlanlar();
        } catch (SQLException e) {
            e.printStackTrace();
            gosterUyari("Hata", "Silme işlemi sırasında hata oluştu:\n" + e.getMessage(), AlertType.ERROR);
        }
    }
    @FXML
    void DuzenleButton_click(ActionEvent event) {
        Kargo seciliKargo = TableKargoPanel.getSelectionModel().getSelectedItem();
        if (seciliKargo == null) {
            gosterUyari("Hata", "Lütfen düzenlemek için bir kayıt seçin.", AlertType.WARNING);
            return;
        } 

        String gonderici = gonderici_tf.getText().trim();
        String alici = alici_tf.getText().trim();
        String adres = adres_tf.getText().trim();
        String kargoTipi = kargotipi_tf.getText().trim();
        String tasimaTuru = tasimaturu_tf.getText().trim();
        String durum = Durum_tf.getText().trim();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "UPDATE kargo SET gonderici=?, alici=?, adres=?, kargoTipi=?, tasimaTuru=?, durum=? WHERE takipNo=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, gonderici);
            ps.setString(2, alici);
            ps.setString(3, adres);
            ps.setString(4, kargoTipi);
            ps.setString(5, tasimaTuru);
            ps.setString(6, durum);
            ps.setString(7, seciliKargo.getTakipNo());

            ps.executeUpdate();
            verileriYukle();         // Tabloyu tazele
            temizleAlanlar();        // Alanları temizle
            gosterUyari("Başarılı", "Kargo bilgileri güncellendi.", AlertType.INFORMATION);
        } catch (SQLException e) {
            e.printStackTrace();
            gosterUyari("Hata", "Düzenleme sırasında hata oluştu:\n" + e.getMessage(), AlertType.ERROR);
        }
    }
    @FXML
    void searchButton_click(ActionEvent event) {
        String aranan = Search_tf.getText().trim();
        if (aranan.isEmpty()) {
            verileriYukle(); // tüm kargo verilerini yeniden yükle
            return;
        }

        ObservableList<Kargo> liste = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM kargo WHERE " +
                         "LOWER(takipNo) LIKE ? OR LOWER(gonderici) LIKE ? OR " +
                         "LOWER(alici) LIKE ? OR LOWER(adres) LIKE ? OR " +
                         "LOWER(durum) LIKE ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            String query = "%" + aranan.toLowerCase() + "%";
            for (int i = 1; i <= 5; i++) {
                ps.setString(i, query);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(new Kargo(
                        rs.getString("takipNo"),
                        rs.getString("gonderici"),
                        rs.getString("alici"),
                        rs.getString("adres"),
                        rs.getString("kargoTipi"),
                        rs.getString("tasimaTuru"),
                        rs.getString("durum")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        TableKargoPanel.setItems(liste);
    }
    private void verileriYukle() {
        ObservableList<Kargo> liste = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM kargo";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                liste.add(new Kargo(
                        rs.getString("takipNo"),
                        rs.getString("gonderici"),
                        rs.getString("alici"),
                        rs.getString("adres"),
                        rs.getString("kargoTipi"),
                        rs.getString("tasimaTuru"),
                        rs.getString("durum")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TableKargoPanel.setItems(liste);
    } 

    @FXML
    void yenikargoEkleButton_click(ActionEvent event) {
        String gonderici = gonderici_tf.getText().trim();
        String alici = alici_tf.getText().trim();
        String adres = adres_tf.getText().trim();
        String kargoTipi = kargotipi_tf.getText().trim();
        String tasimaTuru = tasimaturu_tf.getText().trim();
        String durum = "Hazırlanıyor";

        if (gonderici.isEmpty() || alici.isEmpty() || adres.isEmpty() || kargoTipi.isEmpty() || tasimaTuru.isEmpty()) {
            gosterUyari("Hata", "Lütfen tüm alanları doldurun!", AlertType.WARNING);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String takipNo = rastgeleTakipNoUret(conn);

            String sql = "INSERT INTO kargo (takipNo, gonderici, alici, adres, kargoTipi, tasimaTuru, durum) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, takipNo);
            ps.setString(2, gonderici);
            ps.setString(3, alici);
            ps.setString(4, adres);
            ps.setString(5, kargoTipi);
            ps.setString(6, tasimaTuru);
            ps.setString(7, durum);
            ps.executeUpdate();

            // Takip numarasını alert ile göster
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Kargo Başarıyla Eklendi");
            alert.setHeaderText("Yeni Takip Numaranız");
            alert.setContentText("Takip Numaranız: " + takipNo);
            alert.showAndWait();

            // 1 saniye gecikmeli tabloya ekleme
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // 1 saniye bekle
                } catch (InterruptedException ignored) {}

                javafx.application.Platform.runLater(() -> {
                    TableKargoPanel.getItems().add(new Kargo(takipNo, gonderici, alici, adres, kargoTipi, tasimaTuru, durum));
                });
            }).start();

            temizleAlanlar();
            gosterUyari("Başarılı", "Kargo başarıyla eklendi!\nTakip No: " + takipNo, AlertType.INFORMATION);

        } catch (SQLException e) {
            e.printStackTrace();
            gosterUyari("Veritabanı Hatası", "Kargo eklenirken bir hata oluştu:\n" + e.getMessage(), AlertType.ERROR);
        }
    }

    private void temizleAlanlar() {
    	gonderici_tf.clear();
        alici_tf.clear();
        adres_tf.clear();
        kargotipi_tf.clear();
        tasimaturu_tf.clear();
        Durum_tf.clear();
        takipNo_tf.clear();
    }

    private void gosterUyari(String baslik, String mesaj, AlertType tip) {
        Alert alert = new Alert(tip);
        alert.setTitle(baslik);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.showAndWait();
    }

    private String rastgeleTakipNoUret(Connection conn) {
        String takipNo;
        boolean benzersiz = false;

        do {
            takipNo = String.valueOf((long) (Math.random() * 1_000_000_0000L)); // 10 haneli sayı üret
            try {
                String sql = "SELECT COUNT(*) FROM kargo WHERE takipNo = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, takipNo);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    benzersiz = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                break;
            }
        } while (!benzersiz);

        return takipNo;
    }

    
    @FXML
    void anaSayfaButton_click(ActionEvent event) {
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
    void kargoResmi_click(ActionEvent event) {
        // Boş - istenirse doldurulur
    }

    @FXML
    void Sorgula_click(MouseEvent event) {
        // Boş - istenirse doldurulur
    }

    public void setUserData(String eposta) {
        // Boş - istenirse doldurulur
    }
}