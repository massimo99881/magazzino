package com.mycompany.magazzino;

import com.mycompany.magazzino.models.Articolo;
import com.mycompany.magazzino.models.Movimento;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;


public class MagazzinoApp extends Application {

    private Stage primaryStage; // Definizione come membro della classe
    private TableView<Articolo> tableViewMagazzino;
    private TableView<Movimento> tableViewMovimenti;
    private ObservableList<Articolo> articoli = FXCollections.observableArrayList();
    private ObservableList<Movimento> movimenti = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        
        this.primaryStage = primaryStage;
        
        // Leggi i dati dal file CSV
        leggiDatiCSV("magazzino.csv");

        // Creazione della tabella
        tableViewMagazzino = new TableView<>();
        tableViewMagazzino.setItems(articoli);

        TableColumn<Articolo, String> codiceCol = new TableColumn<>("Codice");
        codiceCol.setCellValueFactory(cellData -> cellData.getValue().codiceProperty());

        TableColumn<Articolo, Number> quantitaCol = new TableColumn<>("Quantita");
        quantitaCol.setCellValueFactory(cellData -> cellData.getValue().quantitaProperty());

        TableColumn<Articolo, String> descrizioneCol = new TableColumn<>("Descrizione");
        descrizioneCol.setCellValueFactory(cellData -> cellData.getValue().descrizioneProperty());

        TableColumn<Articolo, Number> prezzoCol = new TableColumn<>("Prezzo");
        prezzoCol.setCellValueFactory(cellData -> cellData.getValue().prezzoProperty());

        TableColumn<Articolo, String> unitaCol = new TableColumn<>("Unità di misura");
        unitaCol.setCellValueFactory(cellData -> cellData.getValue().unitaMisuraProperty());

        tableViewMagazzino.getColumns().addAll(codiceCol, quantitaCol, descrizioneCol, prezzoCol, unitaCol);

        // Creazione dei pulsanti
        Button compraButton = new Button("Compra");
        compraButton.setOnAction(event -> mostraContenuto("Compra"));

        Button vendiButton = new Button("Vendi");
        vendiButton.setOnAction(event -> mostraContenuto("Vendi"));

                tableViewMovimenti = new TableView<>();
                leggiMovimentiCSV("movimenti.csv");

                TableColumn<Movimento, String> dataCol = new TableColumn<>("Data");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                dataCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().dataOraProperty().get().format(formatter)));

                TableColumn<Movimento, String> articoloCol = new TableColumn<>("Articolo");
                articoloCol.setCellValueFactory(cellData -> cellData.getValue().articoloProperty());

                TableColumn<Movimento, Number> quantitaCol2 = new TableColumn<>("Quantità");
                quantitaCol2.setCellValueFactory(cellData -> cellData.getValue().quantitaProperty());

                TableColumn<Movimento, Number> costoCol = new TableColumn<>("Costo");
                costoCol.setCellValueFactory(cellData -> cellData.getValue().costoProperty());

                TableColumn<Movimento, String> tipoCol = new TableColumn<>("Tipo");
                tipoCol.setCellValueFactory(cellData -> cellData.getValue().tipoProperty());

                tableViewMovimenti.getColumns().addAll(dataCol, articoloCol, quantitaCol2, costoCol, tipoCol);
                tableViewMovimenti.setItems(movimenti);
        Button movimentiButton = new Button("Movimenti");
        movimentiButton.setOnAction(event -> mostraContenuto("Movimenti"));

        Button articoliPersiButton = new Button("Articoli Persi");
        articoliPersiButton.setOnAction(event -> mostraContenuto("Articoli Persi"));

        Button valorizzaButton = new Button("Valorizza");
        valorizzaButton.setOnAction(event -> mostraContenuto("Valorizza"));

        Button magazzinoButton = new Button("Magazzino");
        magazzinoButton.setOnAction(event -> mostraContenuto("Magazzino"));

        
       // Creazione di un HBox per organizzare i pulsanti in una riga
    HBox buttonsRow = new HBox(10); // Spaziatura tra i pulsanti
    buttonsRow.getChildren().addAll(compraButton, vendiButton, movimentiButton,
            articoliPersiButton, valorizzaButton, magazzinoButton);

    // Creazione del layout principale
    VBox root = new VBox(10); // Spaziatura verticale tra i componenti
    root.getChildren().addAll(tableViewMagazzino, buttonsRow);

    // Creazione della scena
    Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Gestione Magazzino");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void leggiDatiCSV(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length != 5) {
                    System.err.println("Formato riga CSV non valido: " + line);
                    continue; // Salta la riga non valida
                }

                String codice = parts[0];
                int quantita;
                try {
                    quantita = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    System.err.println("Errore di conversione per quantità: " + parts[1]);
                    continue; // Salta la riga con quantità non valida
                }
                String descrizione = parts[2];
                double prezzo;
                try {
                    prezzo = Double.parseDouble(parts[3].trim());
                } catch (NumberFormatException e) {
                    System.err.println("Errore di conversione per prezzo: " + parts[3]);
                    continue; // Salta la riga con prezzo non valido
                }
                String unitaMisura = parts[4];

                articoli.add(new Articolo(codice, quantita, descrizione, prezzo, unitaMisura));
            }
        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file CSV: " + e.getMessage());
        }
    }


    private void mostraContenuto(String tipoContenuto) {
        VBox contenuto = new VBox();
        Label titolo = new Label();

        switch (tipoContenuto) {
            case "Compra":
                if(!checkRowSelected()) return;
                titolo.setText("Compra");
                Articolo articoloSelezionatoPerAcquisto = tableViewMagazzino.getSelectionModel().getSelectedItem();
                mostraScenaAcquisto(contenuto, articoloSelezionatoPerAcquisto);
                
                break;
            case "Vendi":
                if(!checkRowSelected()) return;
                titolo.setText("Vendi");
                Articolo articoloSelezionatoPerVendita = tableViewMagazzino.getSelectionModel().getSelectedItem();
                mostraScenaVendita(contenuto, articoloSelezionatoPerVendita);
                break;
            case "Movimenti":
                titolo.setText("Movimenti");
                tableViewMovimenti.setItems(FXCollections.observableArrayList(movimenti));
                contenuto.getChildren().addAll(tableViewMovimenti);
                break;
            case "Articoli Persi":
                titolo.setText("Articoli Persi");
                // Aggiungi qui il contenuto specifico per l'azione "Articoli Persi"
                break;
            case "Valorizza":
                titolo.setText("Valorizza");
                // Aggiungi qui il contenuto specifico per l'azione "Valorizza"
                break;
            case "Magazzino":
                titolo.setText("Articoli in magazzino");
                contenuto.getChildren().addAll(new Label("Articoli in magazzino"), tableViewMagazzino);
                break;
            default:
                break;
        }

        // Aggiungi il titolo e il contenuto alla finestra principale
        contenuto.getChildren().add(0, titolo);
        ((VBox) primaryStage.getScene().getRoot()).getChildren().set(0, contenuto);
    }

    private void mostraScenaAcquisto(VBox contenuto, Articolo articolo) {
        contenuto.setPadding(new Insets(20));

        Label articoloLabel = new Label("Articolo: " + articolo.getCodice());
        Label quantitaLabel = new Label("Quantità da acquistare:");
        Spinner<Integer> quantitaSpinner = new Spinner<>(1, Integer.MAX_VALUE, 1);

        Button acquistaButton = new Button("Acquista");
        acquistaButton.setOnAction(event -> {
            int quantitaAcquistata = quantitaSpinner.getValue();
            articolo.setQuantita(articolo.getQuantita() + quantitaAcquistata);

            // Aggiorna il file CSV con la nuova quantità
            aggiornaFileCSV();
            
            // Registra il movimento
            String data = LocalDate.now().toString();
            double costoTotale = quantitaAcquistata * articolo.getPrezzo();
            registraMovimento(data, articolo.getCodice(), quantitaAcquistata, costoTotale, "acquisto");


            // Aggiorna la tabella
            tableViewMagazzino.refresh();

             mostraContenuto("Magazzino");
        });

        contenuto.getChildren().addAll(articoloLabel, quantitaLabel, quantitaSpinner, acquistaButton);
    }
    
    private void mostraScenaVendita(VBox contenuto, Articolo articolo) {
        contenuto.setPadding(new Insets(20));

        Label articoloLabel = new Label("Articolo: " + articolo.getCodice());
        Label quantitaLabel = new Label("Quantità da vendere:");
        Spinner<Integer> quantitaSpinner = new Spinner<>(1, Integer.MAX_VALUE, 1);

        Button acquistaButton = new Button("Vendi");
        acquistaButton.setOnAction(event -> {
            int quantitaVenduta = quantitaSpinner.getValue();
            articolo.setQuantita(articolo.getQuantita() - quantitaVenduta);

            // Aggiorna il file CSV con la nuova quantità
            aggiornaFileCSV();
            
            // Registra il movimento
            String data = LocalDate.now().toString();
            double costoTotale = quantitaVenduta * articolo.getPrezzo();
            registraMovimento(data, articolo.getCodice(), quantitaVenduta, costoTotale, "vendita");


            // Aggiorna la tabella
            tableViewMagazzino.refresh();

             mostraContenuto("Magazzino");
        });

        contenuto.getChildren().addAll(articoloLabel, quantitaLabel, quantitaSpinner, acquistaButton);
    }

    private void aggiornaFileCSV() {
        // File CSV di destinazione
        String filePath = "magazzino.csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Scrivi l'intestazione
            writer.write("codice;quantita;descrizione;prezzo;unita");
            writer.newLine();

            // Scrivi i dati degli articoli
            for (Articolo articolo : articoli) {
                writer.write(articolo.getCodice() + ";" + articolo.getQuantita() + ";" +
                        articolo.getDescrizione() + ";" + articolo.getPrezzo() + ";" + articolo.getUnitaMisura());
                writer.newLine();
            }

            System.out.println("File CSV aggiornato con successo: " + filePath);
        } catch (IOException e) {
            System.err.println("Errore durante l'aggiornamento del file CSV: " + e.getMessage());
        }
    }

    private boolean checkRowSelected() {
        if (tableViewMagazzino.getSelectionModel().isEmpty()) {
            // Nessuna riga selezionata, mostra messaggio di avviso
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attenzione");
            alert.setHeaderText(null);
            alert.setContentText("Seleziona un articolo dal magazzino!");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private void leggiMovimentiCSV(String filePath) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Salta l'intestazione
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length != 5) {
                    System.err.println("Formato riga CSV non valido: " + line);
                    continue;
                }
                LocalDateTime dataOra = LocalDateTime.parse(parts[0], formatter);
                String articolo = parts[1];
                int quantita = Integer.parseInt(parts[2].trim());
                double costo = Double.parseDouble(parts[3].trim());
                String tipo = parts[4];

                movimenti.add(new Movimento(dataOra, articolo, quantita, costo, tipo));
                // Ordina i movimenti per data e ora
                movimenti.sort(Comparator.comparing((Movimento m) -> m.dataOraProperty().get()).reversed());
            }
        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file CSV: " + e.getMessage());
        }
    }
    
    private void registraMovimento(String data, String articolo, int quantita, double costo, String tipo) {
        LocalDateTime oraCorrente = LocalDateTime.now();
        // Aggiunge il movimento alla lista
        movimenti.add(new Movimento(oraCorrente, articolo, quantita, costo, tipo));
        // Ordina nuovamente dopo l'inserimento
        movimenti.sort(Comparator.comparing((Movimento m) -> m.dataOraProperty().get()).reversed());
    
        // Scrive tutti i movimenti nel file, inclusa la nuova riga
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("movimenti.csv", true))) {
            writer.write(oraCorrente.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ";" + articolo + ";" + quantita + ";" + costo + ";" + tipo);
            writer.newLine();
            System.out.println("Movimento registrato con successo.");
        } catch (IOException e) {
            System.err.println("Errore durante l'aggiornamento del file dei movimenti: " + e.getMessage());
        }
    }

}
