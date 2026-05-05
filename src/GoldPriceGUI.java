import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class GoldPriceGUI extends JFrame {

    private final PriceAnalyzer analyzer;

    private JTextField yearField;
    private JTextField exchangeRateField;
    private JComboBox<String> currencyBox;
    private JComboBox<String> unitBox;
    private JTextArea outputArea;

    public GoldPriceGUI(List<GoldPrice> goldPrices) {
        this.analyzer = new PriceAnalyzer(goldPrices);

        setTitle("Analýza ceny zlata");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Hlavný panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        // Horná časť
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Stredná časť
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(45, 45, 45));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("ANALÝZA CENY ZLATA", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(212, 175, 55)); // zlatá farba

        panel.add(titleLabel, BorderLayout.NORTH);

        // Obrázok
        JLabel imageLabel;
        try {
            ImageIcon icon = new ImageIcon("images/gold.jpg"); // sem dáš svoj obrázok
            Image scaled = icon.getImage().getScaledInstance(220, 120, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(scaled));
        } catch (Exception e) {
            imageLabel = new JLabel("Obrázok zlata sa nepodarilo načítať", SwingConstants.CENTER);
            imageLabel.setForeground(Color.WHITE);
        }

        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        panel.add(imageLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(30, 30, 30));

        JPanel controlsPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        controlsPanel.setBackground(new Color(30, 30, 30));

        JLabel yearLabel = createLabel("Rok:");
        yearField = new JTextField("2024");

        JLabel currencyLabel = createLabel("Mena:");
        currencyBox = new JComboBox<>(new String[]{"USD", "EUR"});

        JLabel unitLabel = createLabel("Jednotka:");
        unitBox = new JComboBox<>(new String[]{"oz", "g"});

        JLabel rateLabel = createLabel("Kurz USD → EUR:");
        exchangeRateField = new JTextField("0.92");

        controlsPanel.add(yearLabel);
        controlsPanel.add(yearField);
        controlsPanel.add(currencyLabel);
        controlsPanel.add(currencyBox);
        controlsPanel.add(unitLabel);
        controlsPanel.add(unitBox);
        controlsPanel.add(rateLabel);
        controlsPanel.add(exchangeRateField);

        JButton btnYearPrices = createButton("Výpis cien za rok");
        JButton btnAverage = createButton("Priemerná cena");
        JButton btnMedian = createButton("Medián za rok");
        JButton btnMax = createButton("Maximum za rok");

        controlsPanel.add(btnYearPrices);
        controlsPanel.add(btnAverage);
        controlsPanel.add(btnMedian);
        controlsPanel.add(btnMax);

        panel.add(controlsPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(20, 20, 20));
        outputArea.setForeground(Color.WHITE);
        outputArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // EVENTY
        btnYearPrices.addActionListener(e -> showYearPrices());
        btnAverage.addActionListener(e -> showAverage());
        btnMedian.addActionListener(e -> showMedian());
        btnMax.addActionListener(e -> showMaximum());

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        return label;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(212, 175, 55));
        button.setForeground(Color.BLACK);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        return button;
    }

    private int getYear() {
        try {
            return Integer.parseInt(yearField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Zadaj platný rok.", "Chyba", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    private double getExchangeRate() {
        try {
            double rate = Double.parseDouble(exchangeRateField.getText().trim());
            if (rate <= 0) throw new NumberFormatException();
            return rate;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Zadaj platný kurz USD → EUR.", "Chyba", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    private boolean isEUR() {
        return currencyBox.getSelectedItem().equals("EUR");
    }

    private boolean isGram() {
        return unitBox.getSelectedItem().equals("g");
    }

    private void showYearPrices() {
        int year = getYear();
        if (year == -1) return;

        double rate = getExchangeRate();
        if (rate == -1) return;

        String result = analyzer.getMonthlyPricesAsText(year, isEUR(), rate, isGram());
        outputArea.setText(result);
    }

    private void showAverage() {
        double rate = getExchangeRate();
        if (rate == -1) return;

        double avgUsdZaOz = analyzer.calculateAveragePrice();
        double cenaUsd = isGram() ? avgUsdZaOz / 31.1034768 : avgUsdZaOz;
        double cenaFinal = isEUR() ? cenaUsd * rate : cenaUsd;

        outputArea.setText("Priemerná cena za celý dataset:\n\n" +
                String.format("%.2f", cenaFinal) + " " +
                (isEUR() ? "EUR" : "USD") + "/" +
                (isGram() ? "g" : "oz"));
    }

    private void showMedian() {
        int year = getYear();
        if (year == -1) return;

        double rate = getExchangeRate();
        if (rate == -1) return;

        double medianUsdZaOz = analyzer.calculateMedianPriceForYear(year);

        if (medianUsdZaOz == 0.0) {
            outputArea.setText("Pre rok " + year + " nebol nájdený medián.");
            return;
        }

        double cenaUsd = isGram() ? medianUsdZaOz / 31.1034768 : medianUsdZaOz;
        double cenaFinal = isEUR() ? cenaUsd * rate : cenaUsd;

        outputArea.setText("Medián pre rok " + year + ":\n\n" +
                String.format("%.2f", cenaFinal) + " " +
                (isEUR() ? "EUR" : "USD") + "/" +
                (isGram() ? "g" : "oz"));
    }

    private void showMaximum() {
        int year = getYear();
        if (year == -1) return;

        double rate = getExchangeRate();
        if (rate == -1) return;

        GoldPrice max = analyzer.findMaxPriceForYear(year);

        if (max == null) {
            outputArea.setText("Maximum sa pre rok " + year + " nenašlo.");
            return;
        }

        double maxUsdZaOz = max.getPricePerOunce();
        double cenaUsd = isGram() ? maxUsdZaOz / 31.1034768 : maxUsdZaOz;
        double cenaFinal = isEUR() ? cenaUsd * rate : cenaUsd;

        outputArea.setText("Maximum pre rok " + year + ":\n\n" +
                String.format("%.2f", cenaFinal) + " " +
                (isEUR() ? "EUR" : "USD") + "/" +
                (isGram() ? "g" : "oz") +
                "\nDátum: " + max.getDate());
    }
}