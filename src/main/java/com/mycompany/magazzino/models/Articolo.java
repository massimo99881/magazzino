/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.magazzino.models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Articolo {
        private final String codice;
        private int quantita;
        private final String descrizione;
        private final double prezzo;
        private final String unitaMisura;

        public Articolo(String codice, int quantita, String descrizione, double prezzo, String unitaMisura) {
            this.codice = codice;
            this.quantita = quantita;
            this.descrizione = descrizione;
            this.prezzo = prezzo;
            this.unitaMisura = unitaMisura;
        }

        public String getCodice() {
            return codice;
        }

        public int getQuantita() {
            return quantita;
        }

        public String getDescrizione() {
            return descrizione;
        }

        public double getPrezzo() {
            return prezzo;
        }

        public String getUnitaMisura() {
            return unitaMisura;
        }

        public StringProperty codiceProperty() {
            return new SimpleStringProperty(codice);
        }

        public IntegerProperty quantitaProperty() {
            return new SimpleIntegerProperty(quantita);
        }

        public StringProperty descrizioneProperty() {
            return new SimpleStringProperty(descrizione);
        }

        public DoubleProperty prezzoProperty() {
            return new SimpleDoubleProperty(prezzo);
        }

        public StringProperty unitaMisuraProperty() {
            return new SimpleStringProperty(unitaMisura);
        }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }
    }