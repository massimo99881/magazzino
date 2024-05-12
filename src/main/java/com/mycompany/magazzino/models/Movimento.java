package com.mycompany.magazzino.models;

import java.time.LocalDateTime;
import javafx.beans.property.*;

public class Movimento {
    private ObjectProperty<LocalDateTime> dataOra;
    private StringProperty articolo;
    private IntegerProperty quantita;
    private DoubleProperty costo;
    private StringProperty tipo;

    public Movimento(LocalDateTime dataOra, String articolo, int quantita, double costo, String tipo) {
        this.dataOra = new SimpleObjectProperty<>(dataOra);
        this.articolo = new SimpleStringProperty(articolo);
        this.quantita = new SimpleIntegerProperty(quantita);
        this.costo = new SimpleDoubleProperty(costo);
        this.tipo = new SimpleStringProperty(tipo);
    }

    // Getters and setters
    public ObjectProperty<LocalDateTime> dataOraProperty() {
        return dataOra;
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
