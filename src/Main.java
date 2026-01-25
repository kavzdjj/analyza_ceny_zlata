import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        List<GoldPrice> goldPrices = FileReader.readGoldPricesFromCSV("data/gold_prices.csv");

        if (goldPrices.isEmpty()) {
            System.out.println("Nepodarilo sa načítať dáta. Skontroluj data/gold_prices.csv");
            return;
        }

        PriceAnalyzer analyzer = new PriceAnalyzer(goldPrices);
        Scanner scanner = new Scanner(System.in);

        double kurzUsdEur = 0.92;
        boolean zobrazujVEUR = false;

        boolean zobrazujVGramocha = false;

        while (true) {
            System.out.println("\n--- ANALÝZA CENY ZLATA ---");
            System.out.println("Mena: " + (zobrazujVEUR ? "EUR" : "USD") +
                    " | Jednotka: " + (zobrazujVGramocha ? "g" : "oz") +
                    " | Kurz USD→EUR: " + kurzUsdEur);

            System.out.println("1. Výpis cien za rok");
            System.out.println("2. Priemerná cena (celý dataset)");
            System.out.println("3. Medián (celý dataset)");
            System.out.println("4. Maximum (celý dataset)");
            System.out.println("5. Ukončiť program");
            System.out.println("6. Nastaviť kurz USD→EUR");
            System.out.println("7. Prepínať USD/EUR");
            System.out.println("8. Prepínať oz/g");

            System.out.print("Vyberte možnosť: ");
            int choice = scanner.nextInt();

            switch (choice) {

                case 1: {
                    System.out.print("Zadajte rok: ");
                    int year = scanner.nextInt();
                    analyzer.printMonthlyPrices(year, zobrazujVEUR, kurzUsdEur, zobrazujVGramocha);
                    break;
                }

                case 2: {
                    double avgUsdZaOz = analyzer.calculateAveragePrice();
                    double cenaUsd = zobrazujVGramocha ? avgUsdZaOz / 31.1034768 : avgUsdZaOz;
                    double cenaFinal = zobrazujVEUR ? cenaUsd * kurzUsdEur : cenaUsd;

                    System.out.println("Priemer: " + String.format("%.2f", cenaFinal) +
                            " " + (zobrazujVEUR ? "EUR" : "USD") + "/" + (zobrazujVGramocha ? "g" : "oz"));
                    break;
                }

                case 3: {
                    double medianUsdZaOz = analyzer.calculateMedianPrice();
                    double cenaUsd = zobrazujVGramocha ? medianUsdZaOz / 31.1034768 : medianUsdZaOz;
                    double cenaFinal = zobrazujVEUR ? cenaUsd * kurzUsdEur : cenaUsd;

                    System.out.println("Medián: " + String.format("%.2f", cenaFinal) +
                            " " + (zobrazujVEUR ? "EUR" : "USD") + "/" + (zobrazujVGramocha ? "g" : "oz"));
                    break;
                }

                case 4: {
                    GoldPrice max = analyzer.findMaxPrice();
                    if (max == null) {
                        System.out.println("Maximum sa nenašlo.");
                        break;
                    }

                    double maxUsdZaOz = max.getPricePerOunce();
                    double cenaUsd = zobrazujVGramocha ? maxUsdZaOz / 31.1034768 : maxUsdZaOz;
                    double cenaFinal = zobrazujVEUR ? cenaUsd * kurzUsdEur : cenaUsd;

                    System.out.println("Maximum: " + String.format("%.2f", cenaFinal) +
                            " " + (zobrazujVEUR ? "EUR" : "USD") + "/" + (zobrazujVGramocha ? "g" : "oz") +
                            " | Dátum: " + max.getDate());
                    break;
                }

                case 5:
                    System.out.println("Program sa ukončil.");
                    scanner.close();
                    return;

                case 6: {
                    System.out.print("Zadajte kurz USD→EUR (napr. 0.92): ");
                    double novyKurz = scanner.nextDouble();
                    if (novyKurz > 0) {
                        kurzUsdEur = novyKurz;
                        System.out.println("Kurz nastavený na: " + kurzUsdEur);
                    } else {
                        System.out.println("Neplatný kurz.");
                    }
                    break;
                }

                case 7:
                    zobrazujVEUR = !zobrazujVEUR;
                    System.out.println("Prepnuté na: " + (zobrazujVEUR ? "EUR" : "USD"));
                    break;

                case 8:
                    zobrazujVGramocha = !zobrazujVGramocha;
                    System.out.println("Prepnuté jednotky na: " + (zobrazujVGramocha ? "g" : "oz"));
                    break;

                default:
                    System.out.println("Neplatná voľba.");
            }
        }
    }
}
