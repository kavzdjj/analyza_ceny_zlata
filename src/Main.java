import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<GoldPrice> goldPrices = FileReader.readGoldPricesFromCSV("data/gold_prices.csv");

        PriceAnalyzer analyzer = new PriceAnalyzer(goldPrices);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Zobraziť mesačné ceny zlata za rok");
            System.out.println("2. Zobraziť priemernú cenu zlata");
            System.out.println("3. Zobraziť medián ceny zlata");
            System.out.println("4. Zobraziť maximálnu cenu zlata a dátum");
            System.out.println("5. Ukončiť program");

            System.out.print("Vyberte možnosť: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Zadajte rok: ");
                    int year = scanner.nextInt();
                    analyzer.printMonthlyPrices(year);
                    break;
                case 2:
                    System.out.println("Priemerná cena zlata: " + analyzer.calculateAveragePrice());
                    break;
                case 3:
                    System.out.println("Medián ceny zlata: " + analyzer.calculateMedianPrice());
                    break;
                case 4:
                    GoldPrice maxPrice = analyzer.findMaxPrice();
                    if (maxPrice != null) {
                        System.out.println("Maximálna cena zlata: " + maxPrice.getPricePerOunce() + " na dátum " + maxPrice.getDate());
                    }
                    break;
                case 5:
                    System.out.println("Program sa ukončil.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Neplatná voľba.");
            }
        }
    }
}