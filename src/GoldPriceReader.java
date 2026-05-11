import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GoldPriceReader {

    private static final DateTimeFormatter FORMAT_DNA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMAT_MESIACA = DateTimeFormatter.ofPattern("yyyy-MM");

    public static List<GoldPrice> readGoldPricesFromCSV(String filePath) {
        List<GoldPrice> goldPrices = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (line.toLowerCase().startsWith("date")) {
                    continue;
                }

                String[] values = line.split(",");

                if (values.length < 2) {
                    continue;
                }

                String datumText = values[0].trim();
                String cenaText = values[1].trim();

                if (datumText.isEmpty() || cenaText.isEmpty()) {
                    continue;
                }

                try {
                    LocalDate date = parseDatum(datumText);
                    double price = Double.parseDouble(cenaText);

                    goldPrices.add(new GoldPrice(date, price));
                } catch (Exception e) {
                    // Chybné riadky ignorujeme, aby program nespadol.
                }
            }

        } catch (IOException e) {
            System.err.println("Chyba pri načítaní súboru '" + filePath + "': " + e.getMessage());
        }

        goldPrices.sort(Comparator.comparing(GoldPrice::getDate));

        return goldPrices;
    }

    private static LocalDate parseDatum(String text) {
        if (text.length() == 10) {
            return LocalDate.parse(text, FORMAT_DNA);
        }

        if (text.length() == 7) {
            return YearMonth.parse(text, FORMAT_MESIACA).atDay(1);
        }

        throw new IllegalArgumentException("Neznámy formát dátumu: " + text);
    }
}