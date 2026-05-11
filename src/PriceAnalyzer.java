import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PriceAnalyzer {

    private final List<GoldPrice> goldPrices;

    private static final double GRAMS_IN_OUNCE = 31.1034768;

    public PriceAnalyzer(List<GoldPrice> goldPrices) {
        this.goldPrices = goldPrices;
    }

    public List<GoldPrice> getGoldPrices() {
        return goldPrices;
    }

    public double calculateAveragePriceForYear(int year) {
        return goldPrices.stream()
                .filter(gp -> gp.getDate().getYear() == year)
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

        if (size == 0) {
            return 0.0;
        }

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

    public GoldPrice findMaxPriceForYear(int year) {
        return goldPrices.stream()
                .filter(gp -> gp.getDate().getYear() == year)
                .max(Comparator.comparingDouble(GoldPrice::getPricePerOunce))
                .orElse(null);
    }

    public GoldPrice findPriceByYearMonth(String yearMonthText) {
        try {
            YearMonth selectedMonth = YearMonth.parse(
                    yearMonthText,
                    DateTimeFormatter.ofPattern("yyyy-MM")
            );

            return goldPrices.stream()
                    .filter(gp -> YearMonth.from(gp.getDate()).equals(selectedMonth))
                    .findFirst()
                    .orElse(null);

        } catch (Exception e) {
            return null;
        }
    }

    public GoldPrice findLatestPrice() {
        return goldPrices.stream()
                .max(Comparator.comparing(GoldPrice::getDate))
                .orElse(null);
    }

    public String getMonthlyPricesAsText(int year, boolean eur, double kurzUsdEur, boolean gramy) {
        StringBuilder sb = new StringBuilder();

        String mena = eur ? "EUR" : "USD";
        String jednotka = gramy ? "g" : "oz";

        boolean found = false;

        sb.append("MESAČNÉ CENY ZLATA V ROKU ").append(year).append("\n");
        sb.append("========================================\n\n");

        for (GoldPrice gp : goldPrices) {
            if (gp.getDate().getYear() != year) {
                continue;
            }

            found = true;

            double cenaFinal = convertPrice(gp.getPricePerOunce(), eur, kurzUsdEur, gramy);

            sb.append(gp.getDate())
                    .append(" -> ")
                    .append(String.format("%.2f", cenaFinal))
                    .append(" ")
                    .append(mena)
                    .append("/")
                    .append(jednotka)
                    .append("\n");
        }

        if (!found) {
            return "Pre rok " + year + " sa nenašli žiadne dáta.";
        }

        return sb.toString();
    }

    public double convertPrice(double usdPerOz, boolean eur, double kurzUsdEur, boolean gramy) {
        double price = usdPerOz;

        if (gramy) {
            price = price / GRAMS_IN_OUNCE;
        }

        if (eur) {
            price = price * kurzUsdEur;
        }

        return price;
    }
}