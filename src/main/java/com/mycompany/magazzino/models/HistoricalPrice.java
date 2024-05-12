package com.mycompany.magazzino.models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDate;

public class HistoricalPrice {
    private SimpleStringProperty codiceArticolo;
    private SimpleStringProperty dataInizio;
    private SimpleStringProperty dataFine;
    private SimpleIntegerProperty qtaGiacenza;
    private SimpleDoubleProperty valoreStorico;
    private SimpleDoubleProperty valoreMercato;

    public HistoricalPrice(String codiceArticolo, String dataInizio, String dataFine, int qtaGiacenza, double valoreStorico, double valoreMercato) {
        this.codiceArticolo = new SimpleStringProperty(codiceArticolo);
        this.dataInizio = new SimpleStringProperty(dataInizio);
        this.dataFine = new SimpleStringProperty(dataFine);
        this.qtaGiacenza = new SimpleIntegerProperty(qtaGiacenza);
        this.valoreStorico = new SimpleDoubleProperty(valoreStorico);
        this.valoreMercato = new SimpleDoubleProperty(valoreMercato);
    }

    // Getters and setters for each property

    public String getCodiceArticolo() {
        return codiceArticolo.get();
    }

    public void setCodiceArticolo(String codiceArticolo) {
        this.codiceArticolo.set(codiceArticolo);
    }

    public String getDataInizio() {
        return dataInizio.get();
    }

    public void setDataInizio(String dataInizio) {
        this.dataInizio.set(dataInizio);
    }

    public String getDataFine() {
        return dataFine.get();
    }

    public void setDataFine(String dataFine) {
        this.dataFine.set(dataFine);
    }

    public int getQtaGiacenza() {
        return qtaGiacenza.get();
    }

    public void setQtaGiacenza(int qtaGiacenza) {
        this.qtaGiacenza.set(qtaGiacenza);
    }

    public double getValoreStorico() {
        return valoreStorico.get();
    }

    public void setValoreStorico(double valoreStorico) {
        this.valoreStorico.set(valoreStorico);
    }

    public double getValoreMercato() {
        return valoreMercato.get();
    }

    public void setValoreMercato(double valoreMercato) {
        this.valoreMercato.set(valoreMercato);
    }
}
