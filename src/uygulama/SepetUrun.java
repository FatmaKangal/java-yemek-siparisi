package uygulama;

public class SepetUrun {
    private String name;
    private double price;
    private int adet;

    public SepetUrun(String name, double price, int adet) {
        this.name = name;
        this.price = price;
        this.adet = adet;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    // getFiyat metodu eklendi — getPrice ile aynı işlevi görür
    public double getFiyat() {
        return price;
    }

    public int getAdet() {
        return adet;
    }

    public void setAdet(int adet) {
        this.adet = adet;
    }
}