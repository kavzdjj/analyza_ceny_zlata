import java.util.*;
import java.util.stream.*;

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

    public void printMonthlyPrices(int year) {
        goldPrices.stream()
                .filter(g -> g.getDate().getYear() == year)
                .forEach(System.out::println);
    }
}
