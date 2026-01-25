import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class HlavnyProgram {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        List<CenaZlata> cenyZlata = CitajSubor.citajDaneZoSuboru("data/gold_prices.csv");
        AnalyzerCien analyzer = new AnalyzerCien(cenyZlata);

        while (true) {
            vypisMenu();
            int volba = nacitajCeleCislo("Vyberte možnosť: ");

            switch (volba) {
                case 1:
                    int rok = nacitajCeleCislo("Zadajte rok: ");
                    analyzer.vypisCenyZaMesiac(rok);
                    break;

                case 2:
                    System.out.println("Priemerná cena zlata: " + analyzer.vypocitajPriemernuCenu());
                    break;

                case 3:
                    System.out.println("Medián ceny zlata: " + analyzer.vypocitajMedian());
                    break;

                case 4:
                    CenaZlata maxCena = analyzer.najdiMaximalnuCenu();
                    if (maxCena != null) {
                        System.out.println(
                                "Maximálna cena zlata: " +
                                        maxCena.getCenaZaUncu() +
                                        " na dátum " +
                                        maxCena.getDatum()
                        );
                    }
                    break;

                case 5:
                    System.out.println("Program sa ukončil.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Neplatná voľba, skúste znova.");
            }
        }
    }

    private static void vypisMenu() {
        System.out.println();
        System.out.println("1. Zobraziť mesačné ceny zlata za rok");
        System.out.println("2. Zobraziť priemernú cenu zlata");
        System.out.println("3. Zobraziť medián ceny zlata");
        System.out.println("4. Zobraziť maximálnu cenu zlata a dátum");
        System.out.println("5. Ukončiť program");
    }

    private static int nacitajCeleCislo(String sprava) {
        while (true) {
            try {
                System.out.print(sprava);
                int cislo = scanner.nextInt();
                scanner.nextLine();
                return cislo;
            } catch (InputMismatchException e) {
                System.out.println("Zadajte platné celé číslo!");
                scanner.nextLine();
            }
        }
    }
}
