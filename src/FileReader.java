import java.io.;
import java.nio.file.;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileReader {
    public static List<GoldPrice> readGoldPricesFromCSV(String filePath) {
        List<GoldPrice> goldPrices = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                LocalDate date = LocalDate.parse(values[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                double price = Double.parseDouble(values[1]);
                goldPrices.add(new GoldPrice(date, price));
            }
        } catch (IOException e) {
            System.err.println("Error pri načítaní súboru: " + e.getMessage());
        }
        return goldPrices;
    }
}