package model;

import java.time.LocalDate;

public class GoldPrice {

    private LocalDate datum;
    private double cena;

    public GoldPrice(LocalDate datum, double cena) {
        this.datum = datum;
        this.cena = cena;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public double getCena() {
        return cena;
    }
}
