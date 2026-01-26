# Analýza ceny zlata

## Popis projektu
Tento projekt je konzolová aplikácia v jazyku Java, ktorá slúži na analýzu historických cien zlata.
Aplikácia pracuje s dátami uloženými v CSV súbore a umožňuje používateľovi vykonávať rôzne analýzy
pomocou textového menu.

## Použité technológie
- Java
- IntelliJ IDEA
- Git a GitHub
- CSV súbor
- Mermaid (UML diagram, Flowchart)

## Funkcionality programu
Program umožňuje:
- načítať historické ceny zlata z CSV súboru
- vypísať mesačné ceny zlata v zadanom roku
- vypočítať priemer a medián cien v zadanom roku
- nájsť maximálnu cenu zlata a jej dátum
- vypočítať hodnotu majetku ku dnešnému dňu na základe nákupu zlata

Ovládanie programu prebieha pomocou textového menu v konzole.


## UML Diagram tried

```mermaid
classDiagram
    class GoldPrice {
        - LocalDate date
        - double pricePerOunce
        + getDate()
        + getPricePerOunce()
    }

    class FileReader {
        + readGoldPricesFromCSV(String filePath) List~GoldPrice~
    }

    class PriceAnalyzer {
        - List~GoldPrice~ goldPrices
        + calculateAveragePrice() double
        + calculateMedianPriceForYear(int year) double
        + findMaxPrice() GoldPrice
        + printMonthlyPrices(int year, boolean eur, double kurz, boolean gramy)
    }

    class Main {
        + main(String[] args)
    }

    FileReader --> GoldPrice
    PriceAnalyzer --> GoldPrice
    Main --> FileReader
```
# FLOWCHART – VÝPOČET HODNOTY ZLATA

## Vývojový diagram výpočtu hodnoty zlata

```mermaid
flowchart TD
    A[Začiatok] --> B[Načítanie dát z CSV]
    B --> C[Zadanie roku a množstva zlata]
    C --> D{Existujú dáta pre rok?}
    D -- Nie --> E[Vypíše sa chyba]
    D -- Áno --> F[Získanie ceny zlata]
    F --> G[Prepočet meny a jednotiek]
    G --> H[Výpis výslednej hodnoty]
    H --> I[Koniec]
    Main --> PriceAnalyzer
```
