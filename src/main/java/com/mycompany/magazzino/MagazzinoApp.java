package com.mycompany.magazzino;

import com.mycompany.magazzino.models.Articolo;
import com.mycompany.magazzino.models.HistoricalPrice;
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
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.cell.PropertyValueFactory;


public class MagazzinoApp extends Application {

    private Stage primaryStage; // Definizione come membro della classe
    private TableView<Articolo> tableViewMagazzino;
    private TableView<Movimento> tableViewMovimenti;
    private ObservableList<Articolo> articoli = FXCollections.observableArrayList();
    private ObservableList<Movimento> movimenti = FXCollections.observableArrayList();
    private ObservableList<HistoricalPrice> historicalPrices = FXCollections.observableArrayList();

    private Articolo ultimoArticoloModificato;
    private HBox buttonsRow;
    private TableView<HistoricalPrice> tableViewHistory;
    private DatePicker dpStartDate;
    private DatePicker dpEndDate;
    private TextField tfMarketPrice;
    private Button btnRegister;


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
        
        tableViewMagazzino.setRowFactory(tv -> new TableRow<Articolo>() {
            @Override
            protected void updateItem(Articolo item, boolean empty) {
                super.updateItem(item, empty);
                getChildren().stream().filter(node -> node instanceof TableCell).forEach(cell -> {
                    TableCell<?, ?> tableCell = (TableCell<?, ?>) cell;
                    if (item != null && item.equals(ultimoArticoloModificato) && !empty) {
                        tableCell.setStyle("-fx-background-color: yellow; -fx-text-fill: black;");
                    } else {
                        tableCell.setStyle("");
                    }
                });
            }
        });



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
        magazzinoButton.setVisible(false);
        
       // Creazione di un HBox per organizzare i pulsanti in una riga
    buttonsRow = new HBox(10); // Spaziatura tra i pulsanti
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

    private void setupHistoricalPriceTable() {
        tableViewHistory = new TableView<>();

        TableColumn<HistoricalPrice, String> columnStartDate = new TableColumn<>("Data Inizio");
        columnStartDate.setCellValueFactory(new PropertyValueFactory<>("dataInizio"));

        TableColumn<HistoricalPrice, String> columnEndDate = new TableColumn<>("Data Fine");
        columnEndDate.setCellValueFactory(new PropertyValueFactory<>("dataFine"));

        TableColumn<HistoricalPrice, Integer> columnStock = new TableColumn<>("Quantità Giacenza");
        columnStock.setCellValueFactory(new PropertyValueFactory<>("qtaGiacenza"));

        TableColumn<HistoricalPrice, Double> columnHistoricValue = new TableColumn<>("Valore Storico");
        columnHistoricValue.setCellValueFactory(new PropertyValueFactory<>("valoreStorico"));

        TableColumn<HistoricalPrice, Double> columnMarketValue = new TableColumn<>("Valore Mercato");
        columnMarketValue.setCellValueFactory(new PropertyValueFactory<>("valoreMercato"));

        tableViewHistory.getColumns().addAll(columnStartDate, columnEndDate, columnStock, columnHistoricValue, columnMarketValue);
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
        titolo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        Label articoloLabel = new Label();
        Button magazzinoButton = findButtonByName(buttonsRow, "Magazzino");
        magazzinoButton.setVisible(true);

        Articolo articoloSelezionato = tableViewMagazzino.getSelectionModel().getSelectedItem();
        if (articoloSelezionato != null) {
            articoloLabel.setText("Articolo Selezionato: " + articoloSelezionato.getCodice() + " - " + articoloSelezionato.getDescrizione());
        }
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
                if (!checkRowSelected()) return;

                ComboBox<String> motivoPerdita = new ComboBox<>();
                motivoPerdita.getItems().addAll("rottamazione", "furto");

                TextField numArticoliTextField = new TextField();
                numArticoliTextField.setPromptText("Inserisci il numero di articoli persi");

                Button registraPerditaButton = new Button("Registra");
                registraPerditaButton.setOnAction(event -> gestisciPerditaArticoli(motivoPerdita.getValue(), numArticoliTextField.getText()));

                contenuto.getChildren().addAll(new Label("Seleziona il motivo della perdita:"), motivoPerdita, new Label("Numero di articoli persi:"), numArticoliTextField, registraPerditaButton);
                break;
                
            case "Valorizza":
                if(!checkRowSelected()) return;
                Articolo articoloSelezionatoPerValorizza = tableViewMagazzino.getSelectionModel().getSelectedItem();
                
                setupValorizzaSection(contenuto, articoloSelezionatoPerValorizza);
                
                break;
            case "Magazzino":
                titolo.setText("Articoli in magazzino");
                magazzinoButton.setVisible(false);
                contenuto.getChildren().addAll(tableViewMagazzino);
                break;
            default:
                break;
        }

        // Aggiungi il titolo e il contenuto alla finestra principale
        contenuto.getChildren().add(0, titolo);
        contenuto.getChildren().add(1, articoloLabel);
        ((VBox) primaryStage.getScene().getRoot()).getChildren().set(0, contenuto);
    }

    

    private void setupHistoryTable() {
        TableColumn<HistoricalPrice, String> columnStartDate = new TableColumn<>("Data Inizio");
        columnStartDate.setCellValueFactory(new PropertyValueFactory<>("dataInizio"));

        TableColumn<HistoricalPrice, String> columnEndDate = new TableColumn<>("Data Fine");
        columnEndDate.setCellValueFactory(new PropertyValueFactory<>("dataFine"));

        TableColumn<HistoricalPrice, Integer> columnStock = new TableColumn<>("Quantità Giacenza");
        columnStock.setCellValueFactory(new PropertyValueFactory<>("qtaGiacenza"));

        TableColumn<HistoricalPrice, Double> columnHistoricValue = new TableColumn<>("Valore Storico");
        columnHistoricValue.setCellValueFactory(new PropertyValueFactory<>("valoreStorico"));

        TableColumn<HistoricalPrice, Double> columnMarketValue = new TableColumn<>("Valore Mercato");
        columnMarketValue.setCellValueFactory(new PropertyValueFactory<>("valoreMercato"));

        tableViewHistory.getColumns().addAll(columnStartDate, columnEndDate, columnStock, columnHistoricValue, columnMarketValue);
    }

    

    private void addRecordToCsv(String code, String startDate, String endDate, int stock, double historicValue, double marketPrice) {
        // File CSV di destinazione
        String filePath = "storico.csv";

        try (FileWriter fw = new FileWriter(filePath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            // Componi la linea da aggiungere
            String line = String.format("%s,%s,%s,%d,%.2f,%.2f",
                                        code, startDate, endDate, stock, historicValue, marketPrice);

            // Scrivi la linea nel file CSV
            out.println(line);
            System.out.println("Record aggiunto con successo al file CSV: " + filePath);
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura nel file CSV: " + e.getMessage());
        }
    }
    
    private void gestisciPerditaArticoli(String motivo, String numArticoliPersiText) {
        if (motivo == null || numArticoliPersiText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Errore", "Devi selezionare un motivo e inserire il numero di articoli persi.");
            return;
        }

        int numArticoliPersi;
        try {
            numArticoliPersi = Integer.parseInt(numArticoliPersiText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Errore di Formato", "Il numero di articoli deve essere un valore intero.");
            return;
        }

        Articolo articoloSelezionato = tableViewMagazzino.getSelectionModel().getSelectedItem();
        if (articoloSelezionato != null) {
            articoloSelezionato.setQuantita(articoloSelezionato.getQuantita() - numArticoliPersi);
            ultimoArticoloModificato = articoloSelezionato;
            aggiornaFileCSV();  // Assicurati che questo metodo aggiorni correttamente il file CSV del magazzino
            mostraContenuto("Magazzino");
        } else {
            showAlert(Alert.AlertType.WARNING, "Nessun Articolo Selezionato", "Seleziona un articolo dal magazzino!");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    
    private void mostraScenaAcquisto(VBox contenuto, Articolo articolo) {
        contenuto.setPadding(new Insets(20));

        Label articoloLabel = new Label(); //"Articolo: " + articolo.getCodice()
        Label quantitaLabel = new Label("Quantità da acquistare:");
        Spinner<Integer> quantitaSpinner = new Spinner<>(1, Integer.MAX_VALUE, 1);

        Button acquistaButton = new Button("Acquista");
        acquistaButton.setOnAction(event -> {
            int quantitaAcquistata = quantitaSpinner.getValue();
            Articolo articoloSelezionato = tableViewMagazzino.getSelectionModel().getSelectedItem();
            articoloSelezionato.setQuantita(articoloSelezionato.getQuantita() + quantitaAcquistata);
            ultimoArticoloModificato = articoloSelezionato;
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

        Label articoloLabel = new Label(); //"Articolo: " + articolo.getCodice()
        Label quantitaLabel = new Label("Quantità da vendere:");
        Spinner<Integer> quantitaSpinner = new Spinner<>(1, Integer.MAX_VALUE, 1);

        Button vendiButton = new Button("Vendi");
        vendiButton.setOnAction(event -> {
            int quantitaVenduta = quantitaSpinner.getValue();
            Articolo articoloSelezionato = tableViewMagazzino.getSelectionModel().getSelectedItem();
            articoloSelezionato.setQuantita(articoloSelezionato.getQuantita() - quantitaVenduta);
            ultimoArticoloModificato = articoloSelezionato;

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

        contenuto.getChildren().addAll(articoloLabel, quantitaLabel, quantitaSpinner, vendiButton);
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

    
    private Button findButtonByName(HBox buttonsRow, String buttonName) {
        for (Node node : buttonsRow.getChildren()) {
            if (node instanceof Button && ((Button) node).getText().equals(buttonName)) {
                return (Button) node;
            }
        }
        return null; // o gestire il caso di non trovato
    }

    private void loadHistoricalPrices(String codiceArticolo) {
        String filePath = "storico.csv";
        historicalPrices.clear();  // Pulisci la lista precedente

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            // Salta la prima riga (intestazioni del CSV)
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].trim().equals(codiceArticolo)) {
                    // Crea un nuovo oggetto HistoricalPrice se il codice articolo corrisponde
                    HistoricalPrice price = new HistoricalPrice(
                        data[0].trim(), // codiceArticolo
                        data[1].trim(), // dataInizio
                        data[2].trim(), // dataFine
                        Integer.parseInt(data[3].trim()), // qtaGiacenza
                        Double.parseDouble(data[4].trim()), // valoreStorico
                        Double.parseDouble(data[5].trim())  // valoreMercato
                    );
                    historicalPrices.add(price);
                }
            }
        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file " + filePath + ": " + e.getMessage());
        }
    }
    
    private void setupValorizzaSection(VBox contenuto, Articolo articoloSelezionatoPerValorizza) {
    contenuto.getChildren().clear();

    Label titolo = new Label("Valorizza Articolo: " + articoloSelezionatoPerValorizza.getCodice() + " - " + articoloSelezionatoPerValorizza.getDescrizione());
    titolo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

    // Configura e carica la tabella dello storico dei prezzi
    setupHistoricalPriceTable();
    loadHistoricalPrices(articoloSelezionatoPerValorizza.getCodice());
    tableViewHistory.setItems(historicalPrices);  // Assicurati che i dati siano impostati

    // Verifica che la tabella abbia dati da mostrare
    if (historicalPrices.isEmpty()) {
        contenuto.getChildren().add(new Label("Nessun dato storico disponibile per questo articolo."));
    } else {
        contenuto.getChildren().add(tableViewHistory); // Aggiungi la tabella se ci sono dati
    }

    // Setup dei DatePicker e altri controlli
    HBox datePickers = new HBox(10);
    dpStartDate = new DatePicker();
    dpEndDate = new DatePicker();
    datePickers.getChildren().addAll(new Label("Data Inizio:"), dpStartDate, new Label("Data Fine:"), dpEndDate);

    TextField tfMarketPrice = new TextField();
    tfMarketPrice.setPromptText("Inserisci il prezzo di mercato");

    Button btnRegister = new Button("Registra");
    btnRegister.setOnAction(e -> {
        registerMarketPrice(articoloSelezionatoPerValorizza.getCodice(), dpStartDate.getValue(), dpEndDate.getValue(), Double.parseDouble(tfMarketPrice.getText()));
    });

    // Aggiunta dei componenti al layout
    contenuto.getChildren().addAll(titolo, datePickers, new Label("Prezzo di Mercato:"), tfMarketPrice, btnRegister);
}
    
    /**
    * Registra il nuovo prezzo di mercato per l'articolo selezionato nel periodo specificato.
    * @param codiceArticolo Codice dell'articolo selezionato.
    * @param startDate Data di inizio del periodo.
    * @param endDate Data di fine del periodo.
    * @param marketPrice Nuovo prezzo di mercato inserito dall'utente.
    */
   private void registerMarketPrice(String codiceArticolo, LocalDate startDate, LocalDate endDate, double marketPrice) {
       // Controllo delle date: startDate non deve essere dopo endDate
       if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
           showAlert("Errore Data", "La data di inizio deve essere precedente o uguale alla data di fine.");
           return;
       }

       // Controllo del prezzo di mercato: deve essere un valore positivo
       if (marketPrice <= 0) {
           showAlert("Errore Prezzo", "Il prezzo di mercato deve essere un valore positivo.");
           return;
       }

       // Converti le date in stringhe nel formato desiderato
       String formattedStartDate = startDate.toString();
       String formattedEndDate = endDate.toString();

       // Valori fittizi per la quantità di giacenza e il valore storico, che potrebbero essere calcolati o inseriti dall'utente
       int stock = 0;  // Esempio: questo valore potrebbe essere ricavato da altri dati o da un input dell'utente
       double historicValue = 0;  // Esempio: potrebbe essere calcolato basandosi su altre metriche

       // Aggiungi il record al CSV
       addRecordToCsv(codiceArticolo, formattedStartDate, formattedEndDate, stock, historicValue, marketPrice);

       // Notifica all'utente che l'operazione è stata completata
       showAlert("Successo", "Il nuovo prezzo di mercato è stato registrato correttamente.");
   }

   /**
    * Mostra un alert con un messaggio di errore o di conferma.
    * @param title Titolo dell'alert.
    * @param content Contenuto del messaggio.
    */
   private void showAlert(String title, String content) {
       Alert alert = new Alert(Alert.AlertType.INFORMATION);
       alert.setTitle(title);
       alert.setHeaderText(null);
       alert.setContentText(content);
       alert.showAndWait();
   }

}
