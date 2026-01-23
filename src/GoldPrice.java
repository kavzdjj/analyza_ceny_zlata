import java.time.LocalDate;

public class GoldPrice {
    private LocalDate date;
    private double pricePerOunce;

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
        return "datum: " + date + ", cena: " + pricePerOunce;
    }
}