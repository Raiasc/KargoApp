package application;

public class Kargo {
    private String takipNo, gonderici, alici, adres, kargoTipi, tasimaTuru, durum;

    public Kargo(String takipNo, String gonderici, String alici, String adres, String kargoTipi, String tasimaTuru, String durum) {
        this.takipNo = takipNo;
        this.gonderici = gonderici;
        this.alici = alici;
        this.adres = adres;
        this.kargoTipi = kargoTipi;
        this.tasimaTuru = tasimaTuru;
        this.durum = durum;
    }

    public String getTakipNo() { return takipNo; }
    public String getGonderici() { return gonderici; }
    public String getAlici() { return alici; }
    public String getAdres() { return adres; }
    public String getKargoTipi() { return kargoTipi; }
    public String getTasimaTuru() { return tasimaTuru; }
    public String getDurum() { return durum; }
}