import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

import java.util.List;

public class GoldPriceController {

    @FXML
    private TextField yearField;

    @FXML
    private TextField exchangeRateField;

    @FXML
    private ComboBox<String> currencyComboBox;

    @FXML
    private ComboBox<String> unitComboBox;

    @FXML
    private TextArea outputArea;

    private PriceAnalyzer analyzer;

    @FXML
    public void initialize() {
        List<GoldPrice> goldPrices = GoldPriceReader.readGoldPricesFromCSV("data/gold_prices.csv");

        if (goldPrices.isEmpty()) {
            showError("Dáta sa nepodarilo načítať. Skontroluj súbor data/gold_prices.csv");
            return;
        }

        analyzer = new PriceAnalyzer(goldPrices);

        currencyComboBox.getItems().addAll("USD", "EUR");
        currencyComboBox.setValue("USD");

        unitComboBox.getItems().addAll("oz", "g");
        unitComboBox.setValue("oz");

        outputArea.setText("Dáta boli úspešne načítané.\nVyber akciu pomocou tlačidiel.");
    }

    @FXML
    private void handleShowYearPrices() {
        if (analyzer == null) return;

        int year = getYear();
        if (year == -1) return;

        double rate = getExchangeRate();
        if (rate == -1) return;

        boolean eur = isEUR();
        boolean grams = isGram();

        String result = analyzer.getMonthlyPricesAsText(year, eur, rate, grams);
        outputArea.setText(result);
    }

    @FXML
    private void handleShowAverage() {
        if (analyzer == null) return;

        double rate = getExchangeRate();
        if (rate == -1) return;

        double avgUsdPerOz = analyzer.calculateAveragePrice();
        double converted = convertPrice(avgUsdPerOz, rate);

        outputArea.setText(
                "PRIEMERNÁ CENA ZA CELÝ DATASET\n\n" +
                        String.format("%.2f", converted) + " " + getCurrency() + "/" + getUnit()
        );
    }

    @FXML
    private void handleShowMedian() {
        if (analyzer == null) return;

        int year = getYear();
        if (year == -1) return;

        double rate = getExchangeRate();
        if (rate == -1) return;

        double medianUsdPerOz = analyzer.calculateMedianPriceForYear(year);

        if (medianUsdPerOz == 0.0) {
            outputArea.setText("Pre rok " + year + " sa nenašli žiadne dáta.");
            return;
        }

        double converted = convertPrice(medianUsdPerOz, rate);

        outputArea.setText(
                "MEDIÁN CENY ZLATA PRE ROK " + year + "\n\n" +
                        String.format("%.2f", converted) + " " + getCurrency() + "/" + getUnit()
        );
    }

    @FXML
    private void handleShowMaximum() {
        if (analyzer == null) return;

        int year = getYear();
        if (year == -1) return;

        double rate = getExchangeRate();
        if (rate == -1) return;

        GoldPrice max = analyzer.findMaxPriceForYear(year);

        if (max == null) {
            outputArea.setText("Maximum sa pre rok " + year + " nenašlo.");
            return;
        }

        double converted = convertPrice(max.getPricePerOunce(), rate);

        outputArea.setText(
                "MAXIMUM CENY ZLATA PRE ROK " + year + "\n\n" +
                        "Cena: " + String.format("%.2f", converted) + " " + getCurrency() + "/" + getUnit() + "\n" +
                        "Dátum: " + max.getDate()
        );
    }

    private int getYear() {
        try {
            return Integer.parseInt(yearField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Zadaj platný rok, napríklad 2024.");
            return -1;
        }
    }

    private double getExchangeRate() {
        try {
            double rate = Double.parseDouble(exchangeRateField.getText().trim());

            if (rate <= 0) {
                showError("Kurz musí byť väčší ako 0.");
                return -1;
            }

            return rate;
        } catch (NumberFormatException e) {
            showError("Zadaj platný kurz, napríklad 0.92.");
            return -1;
        }
    }

    private double convertPrice(double usdPerOz, double rate) {
        double price = usdPerOz;

        if (isGram()) {
            price = price / 31.1034768;
        }

        if (isEUR()) {
            price = price * rate;
        }

        return price;
    }

    private boolean isEUR() {
        return "EUR".equals(currencyComboBox.getValue());
    }

    private boolean isGram() {
        return "g".equals(unitComboBox.getValue());
    }

    private String getCurrency() {
        return currencyComboBox.getValue();
    }

    private String getUnit() {
        return unitComboBox.getValue();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Chyba");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}