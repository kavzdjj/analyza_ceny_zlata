import java.time.LocalDate;

public class GoldPrice {

    private final LocalDate date;
    private final double pricePerOunce;

    public GoldPrice(LocalDate date, double pricePerOunce) {
        this.date = date;
        this.pricePerOunce = pricePerOunce;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getPricePerOunce() {
        return pricePerOunce;
    }

    @Override
    public String toString() {
        return "Dátum: " + date + ", cena USD/oz: " + String.format("%.2f", pricePerOunce);
    }
}