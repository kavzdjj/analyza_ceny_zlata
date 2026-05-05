import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PriceAnalyzer {
    private final List<GoldPrice> goldPrices;

    public PriceAnalyzer(List<GoldPrice> goldPrices) {
        this.goldPrices = goldPrices;
    }

    public double calculateAveragePrice() {
        return goldPrices.stream()
                .mapToDouble(GoldPrice::getPricePerOunce)
                .average()
                .orElse(0.0);
    }

    public double calculateMedianPriceForYear(int year) {
        List<Double> prices = goldPrices.stream()
                .filter(gp -> gp.getDate().getYear() == year)
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

    public GoldPrice findMaxPriceForYear(int year) {
        return goldPrices.stream()
                .filter(gp -> gp.getDate().getYear() == year)
                .max(Comparator.comparingDouble(GoldPrice::getPricePerOunce))
                .orElse(null);
    }

    public String getMonthlyPricesAsText(int year, boolean eur, double kurzUsdEur, boolean gramy) {
        StringBuilder sb = new StringBuilder();
        String mena = eur ? "EUR" : "USD";
        String jednotka = gramy ? "g" : "oz";

        List<GoldPrice> filtered = goldPrices.stream()
                .filter(gp -> gp.getDate().getYear() == year)
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            return "Pre rok " + year + " sa nenašli žiadne údaje.";
        }

        for (GoldPrice gp : filtered) {
            double cenaUsdZaOz = gp.getPricePerOunce();
            double cenaUsd = gramy ? cenaUsdZaOz / 31.1034768 : cenaUsdZaOz;
            double cenaFinal = eur ? cenaUsd * kurzUsdEur : cenaUsd;

            sb.append(gp.getDate())
                    .append(" -> ")
                    .append(String.format("%.2f", cenaFinal))
                    .append(" ")
                    .append(mena)
                    .append("/")
                    .append(jednotka)
                    .append("\n");
        }

        return sb.toString();
    }
}