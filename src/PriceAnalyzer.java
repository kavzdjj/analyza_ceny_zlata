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
            if (gp.getDate().getYear() != year) continue;

            found = true;

            double cenaFinal = convertPrice(
                    gp.getPricePerOunce(),
                    eur,
                    kurzUsdEur,
                    gramy
            );

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

    // 🔥 MAJETOK (ZISK / STRATA)
    public String calculateInvestment(String purchaseDateText, double grams) {

        double buyPrice = 0;
        boolean found = false;

        for (GoldPrice gp : goldPrices) {
            if (gp.getDate().toString().startsWith(purchaseDateText)) {
                buyPrice = gp.getPricePerOunce();
                found = true;
                break;
            }
        }

        if (!found) {
            return "Nenašli sa dáta pre dátum nákupu.";
        }

        GoldPrice latest = findLatestPrice();
        double currentPrice = latest.getPricePerOunce();

        double ounces = grams / GRAMS_IN_OUNCE;

        double invested = ounces * buyPrice;
        double currentValue = ounces * currentPrice;
        double difference = currentValue - invested;

        String status;
        if (difference > 0) status = "ZISK 📈";
        else if (difference < 0) status = "STRATA 📉";
        else status = "BEZ ZMENY";

        return "Dátum nákupu: " + purchaseDateText +
                "\nMnožstvo: " + grams + " g" +
                "\n\nCena pri nákupe: " + String.format("%.2f", buyPrice) + " USD/oz" +
                "\nInvestovaná suma: " + String.format("%.2f", invested) + " USD" +
                "\n\nAktuálna cena: " + String.format("%.2f", currentPrice) + " USD/oz" +
                "\nAktuálna hodnota: " + String.format("%.2f", currentValue) + " USD" +
                "\n\nRozdiel: " + String.format("%.2f", difference) + " USD" +
                "\nStav: " + status;
    }
}