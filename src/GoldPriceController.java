import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.List;

public class GoldPriceController {

    @FXML
    private CheckBox showChartCheckBox;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private TextField yearField;

    @FXML
    private TextField exchangeRateField;

    @FXML
    private TextField purchaseDateField;

    @FXML
    private TextField ouncesField;

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

        currencyComboBox.getItems().addAll("USD", "EUR");
        currencyComboBox.setValue("USD");

        unitComboBox.getItems().addAll("oz", "g");
        unitComboBox.setValue("oz");

        if (goldPrices.isEmpty()) {
            outputArea.setText(
                    "CHYBA\n\n" +
                            "Dáta sa nepodarilo načítať.\n" +
                            "Skontroluj, či existuje súbor:\n\n" +
                            "data/gold_prices.csv"
            );
            showError("Dáta sa nepodarilo načítať. Skontroluj súbor data/gold_prices.csv");
            return;
        }

        analyzer = new PriceAnalyzer(goldPrices);

        outputArea.setText(
                "DÁTA BOLI ÚSPEŠNE NAČÍTANÉ\n" +
                        "========================================\n\n" +
                        "Program slúži na analýzu ceny zlata z CSV súboru.\n\n" +
                        "Funkcionality:\n" +
                        "- výpis mesačných cien v danom roku,\n" +
                        "- výpočet priemernej ceny v danom roku,\n" +
                        "- výpočet mediánu v danom roku,\n" +
                        "- nájdenie maxima v celom datasete,\n" +
                        "- nájdenie maxima v danom roku,\n" +
                        "- výpočet hodnoty majetku podľa dátumu nákupu,\n" +
                        "- voliteľné zobrazenie grafu ceny zlata za daný rok.\n\n" +
                        "Pri výpočte majetku zadaj napríklad:\n" +
                        "Dátum nákupu: 2010-10\n" +
                        "Množstvo zlata: 10"
        );

        lineChart.setTitle("Graf ceny zlata");
        lineChart.setLegendVisible(false);

        lineChart.setVisible(false);
        lineChart.setManaged(false);
    }

    @FXML
    private void handleToggleChart() {
        boolean show = showChartCheckBox.isSelected();

        lineChart.setVisible(show);
        lineChart.setManaged(show);

        if (show) {
            int year = getYear();
            if (year != -1) {
                showChart(year);
            }
        } else {
            clearChart();
        }
    }

    @FXML
    private void handleShowYearPrices() {
        setDefaultOutputStyle();

        if (analyzer == null) return;

        int year = getYear();
        if (year == -1) return;

        double rate = getExchangeRate();
        if (rate == -1) return;

        outputArea.setText(analyzer.getMonthlyPricesAsText(year, isEUR(), rate, isGram()));

        if (showChartCheckBox.isSelected()) {
            showChart(year);
        }
    }

    @FXML
    private void handleShowAverageForYear() {
        setDefaultOutputStyle();

        if (analyzer == null) return;

        int year = getYear();
        if (year == -1) return;

        double rate = getExchangeRate();
        if (rate == -1) return;

        double averageUsdPerOz = analyzer.calculateAveragePriceForYear(year);

        if (averageUsdPerOz == 0.0) {
            outputArea.setText("Pre rok " + year + " sa nenašli žiadne dáta.");
            clearChart();
            return;
        }

        double converted = analyzer.convertPrice(averageUsdPerOz, isEUR(), rate, isGram());

        outputArea.setText(
                "PRIEMERNÁ CENA ZLATA V ROKU " + year + "\n" +
                        "========================================\n\n" +
                        "Priemer: " + String.format("%.2f", converted) + " " + getCurrency() + "/" + getUnit() + "\n\n" +
                        "Poznámka:\n" +
                        "Priemer je vypočítaný zo všetkých záznamov v datasete, ktoré patria do daného roka."
        );

        if (showChartCheckBox.isSelected()) {
            showChart(year);
        }
    }

    @FXML
    private void handleShowMedian() {
        setDefaultOutputStyle();

        if (analyzer == null) return;

        int year = getYear();
        if (year == -1) return;

        double rate = getExchangeRate();
        if (rate == -1) return;

        double medianUsdPerOz = analyzer.calculateMedianPriceForYear(year);

        if (medianUsdPerOz == 0.0) {
            outputArea.setText("Pre rok " + year + " sa nenašli žiadne dáta.");
            clearChart();
            return;
        }

        double converted = analyzer.convertPrice(medianUsdPerOz, isEUR(), rate, isGram());

        outputArea.setText(
                "MEDIÁN CENY ZLATA V ROKU " + year + "\n" +
                        "========================================\n\n" +
                        "Medián: " + String.format("%.2f", converted) + " " + getCurrency() + "/" + getUnit() + "\n\n" +
                        "Poznámka:\n" +
                        "Medián je stredná hodnota cien po zoradení od najnižšej po najvyššiu."
        );

        if (showChartCheckBox.isSelected()) {
            showChart(year);
        }
    }

    @FXML
    private void handleShowMaximumAll() {
        setDefaultOutputStyle();

        if (analyzer == null) return;

        double rate = getExchangeRate();
        if (rate == -1) return;

        GoldPrice max = analyzer.findMaxPrice();

        if (max == null) {
            outputArea.setText("Maximum sa nenašlo.");
            clearChart();
            return;
        }

        double converted = analyzer.convertPrice(max.getPricePerOunce(), isEUR(), rate, isGram());

        outputArea.setText(
                "MAXIMUM CENY ZLATA V CELOM DATASETE\n" +
                        "========================================\n\n" +
                        "Dátum maxima: " + max.getDate() + "\n" +
                        "Cena: " + String.format("%.2f", converted) + " " + getCurrency() + "/" + getUnit() + "\n\n" +
                        "Pôvodná cena v datasete:\n" +
                        String.format("%.2f", max.getPricePerOunce()) + " USD/oz"
        );

        clearChart();
    }

    @FXML
    private void handleShowMaximumForYear() {
        setDefaultOutputStyle();

        if (analyzer == null) return;

        int year = getYear();
        if (year == -1) return;

        double rate = getExchangeRate();
        if (rate == -1) return;

        GoldPrice max = analyzer.findMaxPriceForYear(year);

        if (max == null) {
            outputArea.setText("Maximum sa pre rok " + year + " nenašlo.");
            clearChart();
            return;
        }

        double converted = analyzer.convertPrice(max.getPricePerOunce(), isEUR(), rate, isGram());

        outputArea.setText(
                "MAXIMUM CENY ZLATA V ROKU " + year + "\n" +
                        "========================================\n\n" +
                        "Dátum maxima: " + max.getDate() + "\n" +
                        "Cena: " + String.format("%.2f", converted) + " " + getCurrency() + "/" + getUnit() + "\n\n" +
                        "Pôvodná cena v datasete:\n" +
                        String.format("%.2f", max.getPricePerOunce()) + " USD/oz"
        );

        if (showChartCheckBox.isSelected()) {
            showChart(year);
        }
    }

    @FXML
    private void handleShowChart() {
        setDefaultOutputStyle();

        if (analyzer == null) return;

        int year = getYear();
        if (year == -1) return;

        showChartCheckBox.setSelected(true);
        lineChart.setVisible(true);
        lineChart.setManaged(true);

        showChart(year);

        outputArea.setText(
                "GRAF CENY ZLATA\n" +
                        "========================================\n\n" +
                        "Zobrazený graf pre rok: " + year + "\n\n" +
                        "Graf zobrazuje vývoj ceny zlata podľa dátumov v datasete.\n" +
                        "Cena v grafe je v pôvodnej jednotke datasetu: USD/oz."
        );
    }

    @FXML
    private void handleCalculateAssetValue() {
        if (analyzer == null) return;

        double rate = getExchangeRate();
        if (rate == -1) return;

        String purchaseDate = purchaseDateField.getText().trim();
        double ounces = getOunces();

        if (ounces <= 0) return;

        GoldPrice purchasePrice = analyzer.findPriceByYearMonth(purchaseDate);
        GoldPrice latestPrice = analyzer.findLatestPrice();

        if (purchasePrice == null) {
            setDefaultOutputStyle();

            outputArea.setText(
                    "CHYBA PRI VÝPOČTE MAJETKU\n" +
                            "========================================\n\n" +
                            "Pre dátum nákupu '" + purchaseDate + "' sa nenašla cena.\n\n" +
                            "Použi formát yyyy-MM, napríklad:\n" +
                            "2010-10\n\n" +
                            "Skontroluj tiež, či sa tento mesiac nachádza v CSV súbore."
            );

            clearChart();
            return;
        }

        if (latestPrice == null) {
            setDefaultOutputStyle();
            outputArea.setText("Nepodarilo sa nájsť najnovšiu cenu v datasete.");
            clearChart();
            return;
        }

        double purchasePriceUsdPerOz = purchasePrice.getPricePerOunce();
        double latestPriceUsdPerOz = latestPrice.getPricePerOunce();

        double purchaseValueUsd = purchasePriceUsdPerOz * ounces;
        double currentValueUsd = latestPriceUsdPerOz * ounces;

        double profitUsd = currentValueUsd - purchaseValueUsd;

        double percentageChange = 0.0;
        if (purchaseValueUsd != 0) {
            percentageChange = (profitUsd / purchaseValueUsd) * 100.0;
        }

        double purchasePriceFinalPerOz = isEUR() ? purchasePriceUsdPerOz * rate : purchasePriceUsdPerOz;
        double latestPriceFinalPerOz = isEUR() ? latestPriceUsdPerOz * rate : latestPriceUsdPerOz;

        double purchaseValueFinal = isEUR() ? purchaseValueUsd * rate : purchaseValueUsd;
        double currentValueFinal = isEUR() ? currentValueUsd * rate : currentValueUsd;
        double profitFinal = isEUR() ? profitUsd * rate : profitUsd;

        double differencePerOzFinal = latestPriceFinalPerOz - purchasePriceFinalPerOz;

        String status;
        String statusDescription;
        String recommendation;

        if (profitFinal > 0) {
            status = "ZISK";
            statusDescription = "Investícia je momentálne v zisku.";
            recommendation = "Hodnota zlata je vyššia ako v čase nákupu.";
            setProfitOutputStyle();
        } else if (profitFinal < 0) {
            status = "STRATA";
            statusDescription = "Investícia je momentálne v strate.";
            recommendation = "Hodnota zlata je nižšia ako v čase nákupu.";
            setLossOutputStyle();
        } else {
            status = "BEZ ZMENY";
            statusDescription = "Investícia je približne na rovnakej hodnote ako pri nákupe.";
            recommendation = "Rozdiel medzi nákupnou a aktuálnou hodnotou je nulový alebo zanedbateľný.";
            setNeutralOutputStyle();
        }

        String currency = getCurrency();

        outputArea.setText(
                "VÝPOČET HODNOTY MAJETKU\n" +
                        "========================================\n\n" +

                        "STAV INVESTÍCIE: " + status + "\n" +
                        statusDescription + "\n" +
                        recommendation + "\n\n" +

                        "ZÁKLADNÉ ÚDAJE\n" +
                        "----------------------------------------\n" +
                        "Zadaný dátum nákupu: " + purchaseDate + "\n" +
                        "Dátum ceny pri nákupe v datasete: " + purchasePrice.getDate() + "\n" +
                        "Najnovší dátum v datasete: " + latestPrice.getDate() + "\n" +
                        "Množstvo zlata: " + String.format("%.4f", ounces) + " oz\n" +
                        "Použitá mena: " + currency + "\n" +
                        "Použitý kurz USD → EUR: " + String.format("%.4f", rate) + "\n\n" +

                        "CENA ZA 1 OZ\n" +
                        "----------------------------------------\n" +
                        "Cena pri nákupe: " + String.format("%.2f", purchasePriceFinalPerOz) + " " + currency + "/oz\n" +
                        "Najnovšia cena: " + String.format("%.2f", latestPriceFinalPerOz) + " " + currency + "/oz\n" +
                        "Rozdiel na 1 oz: " + String.format("%.2f", differencePerOzFinal) + " " + currency + "/oz\n\n" +

                        "HODNOTA MAJETKU\n" +
                        "----------------------------------------\n" +
                        "Hodnota pri nákupe: " + String.format("%.2f", purchaseValueFinal) + " " + currency + "\n" +
                        "Aktuálna hodnota podľa datasetu: " + String.format("%.2f", currentValueFinal) + " " + currency + "\n" +
                        "Rozdiel: " + String.format("%.2f", profitFinal) + " " + currency + "\n" +
                        "Percentuálna zmena: " + String.format("%.2f", percentageChange) + " %\n\n" +

                        "KONTROLNÝ VÝPOČET V USD\n" +
                        "----------------------------------------\n" +
                        "Cena pri nákupe: " + String.format("%.2f", purchasePriceUsdPerOz) + " USD/oz\n" +
                        "Najnovšia cena: " + String.format("%.2f", latestPriceUsdPerOz) + " USD/oz\n" +
                        "Hodnota pri nákupe: " + String.format("%.2f", purchaseValueUsd) + " USD\n" +
                        "Aktuálna hodnota: " + String.format("%.2f", currentValueUsd) + " USD\n" +
                        "Rozdiel: " + String.format("%.2f", profitUsd) + " USD\n\n" +

                        "POZNÁMKA\n" +
                        "----------------------------------------\n" +
                        "Výpočet používa najnovšiu cenu dostupnú v CSV datasete.\n" +
                        "Nemusí ísť o reálnu cenu zlata k dnešnému dňu, ak dataset nie je aktuálny."
        );

        if (showChartCheckBox.isSelected()) {
            showChart(purchasePrice.getDate().getYear());
        }
    }

    private void showChart(int year) {
        if (lineChart == null || analyzer == null) {
            return;
        }

        lineChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Cena zlata v roku " + year);

        boolean found = false;

        for (GoldPrice gp : analyzer.getGoldPrices()) {
            if (gp.getDate().getYear() != year) {
                continue;
            }

            found = true;

            String date = gp.getDate().toString();
            double price = gp.getPricePerOunce();

            series.getData().add(new XYChart.Data<>(date, price));
        }

        if (!found) {
            lineChart.setTitle("Pre rok " + year + " nie sú dostupné dáta");
            return;
        }

        lineChart.setTitle("Vývoj ceny zlata v roku " + year + " (USD/oz)");
        lineChart.getData().add(series);
    }

    private void clearChart() {
        if (lineChart != null) {
            lineChart.getData().clear();
            lineChart.setTitle("Graf ceny zlata");
        }
    }

    private int getYear() {
        try {
            int year = Integer.parseInt(yearField.getText().trim());

            if (year < 1800 || year > 3000) {
                showError("Zadaj realistický rok, napríklad 2024.");
                return -1;
            }

            return year;
        } catch (NumberFormatException e) {
            showError("Zadaj platný rok, napríklad 2024.");
            return -1;
        }
    }

    private double getExchangeRate() {
        try {
            double rate = Double.parseDouble(exchangeRateField.getText().trim().replace(",", "."));

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

    private double getOunces() {
        try {
            double ounces = Double.parseDouble(ouncesField.getText().trim().replace(",", "."));

            if (ounces <= 0) {
                showError("Množstvo zlata musí byť väčšie ako 0.");
                return -1;
            }

            return ounces;
        } catch (NumberFormatException e) {
            showError("Zadaj platné množstvo zlata v oz, napríklad 10.");
            return -1;
        }
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

    private void setDefaultOutputStyle() {
        outputArea.setStyle("-fx-control-inner-background: #141414; " +
                "-fx-text-fill: white; " +
                "-fx-font-family: 'Consolas'; " +
                "-fx-font-size: 14px; " +
                "-fx-border-color: #D4AF37; " +
                "-fx-border-width: 1;");
    }

    private void setProfitOutputStyle() {
        outputArea.setStyle("-fx-control-inner-background: #102a18; " +
                "-fx-text-fill: white; " +
                "-fx-font-family: 'Consolas'; " +
                "-fx-font-size: 14px; " +
                "-fx-border-color: #2ecc71; " +
                "-fx-border-width: 2;");
    }

    private void setLossOutputStyle() {
        outputArea.setStyle("-fx-control-inner-background: #2a1010; " +
                "-fx-text-fill: white; " +
                "-fx-font-family: 'Consolas'; " +
                "-fx-font-size: 14px; " +
                "-fx-border-color: #e74c3c; " +
                "-fx-border-width: 2;");
    }

    private void setNeutralOutputStyle() {
        outputArea.setStyle("-fx-control-inner-background: #1c1c1c; " +
                "-fx-text-fill: white; " +
                "-fx-font-family: 'Consolas'; " +
                "-fx-font-size: 14px; " +
                "-fx-border-color: #D4AF37; " +
                "-fx-border-width: 2;");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Chyba");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}