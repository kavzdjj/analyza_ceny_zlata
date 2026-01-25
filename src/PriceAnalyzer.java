import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PriceAnalyzer {
    private List<GoldPrice> goldPrices;

    public PriceAnalyzer(List<GoldPrice> goldPrices) {
        this.goldPrices = goldPrices;
    }

    public double calculateAveragePrice() {
        return goldPrices.stream()
                .mapToDouble(GoldPrice::getPricePerOunce)
                .average()
                .orElse(0.0);
    }

    public double calculateMedianPrice() {
        List<Double> prices = goldPrices.stream()
                .map(GoldPrice::getPricePerOunce)
                .sorted()
                .collect(Collectors.toList());

        int size = prices.size();
        if (size == 0) return 0.0;

        if (size % 2 == 0) {
            return (prices.get(size / 2 - 1) + prices.get(size / 2)) / 2.0;
        } else {
            return prices.get(size / 2);
        }
    }

    public GoldPrice findMaxPrice() {
        return goldPrices.stream()
                .max(Comparator.comparingDouble(GoldPrice::getPricePerOunce))
                .orElse(null);
    }

    public void printMonthlyPrices(int year, boolean eur, double kurzUsdEur, boolean gramy) {
        String mena = eur ? "EUR" : "USD";
        String jednotka = gramy ? "g" : "oz";

        for (GoldPrice gp : goldPrices) {
            if (gp.getDate().getYear() != year) continue;

            double cenaUsdZaOz = gp.getPricePerOunce();

            double cenaUsd = gramy ? cenaUsdZaOz / 31.1034768 : cenaUsdZaOz;

            double cenaFinal = eur ? cenaUsd * kurzUsdEur : cenaUsd;

            System.out.println(gp.getDate() + " -> " + String.format("%.2f", cenaFinal) + " " + mena + "/" + jednotka);
        }
    }
}
