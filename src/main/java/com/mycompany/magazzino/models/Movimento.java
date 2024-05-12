package com.mycompany.magazzino.models;

import javafx.beans.property.*;

public class Movimento {
    private StringProperty data;
    private StringProperty articolo;
    private IntegerProperty quantita;
    private DoubleProperty costo;
    private StringProperty tipo;

    public Movimento(String data, String articolo, int quantita, double costo, String tipo) {
        this.data = new SimpleStringProperty(data);
        this.articolo = new SimpleStringProperty(articolo);
        this.quantita = new SimpleIntegerProperty(quantita);
        this.costo = new SimpleDoubleProperty(costo);
        this.tipo = new SimpleStringProperty(tipo);
    }

    // Getters and setters
    public StringProperty dataProperty() {
        return data;
    }

    public StringProperty articoloProperty() {
        return articolo;
    }

    public IntegerProperty quantitaProperty() {
        return quantita;
    }

    public DoubleProperty costoProperty() {
        return costo;
    }

    public StringProperty tipoProperty() {
        return tipo;
    }
}
