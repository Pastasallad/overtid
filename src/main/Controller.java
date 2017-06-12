package main;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private final String DATUM = LocalDate.now().format(DateTimeFormatter.ISO_DATE);


    @FXML
    private TextField txtDatum;
    @FXML
    private TextField txtFilnamn;
    @FXML
    private TextField txtNamn;
    @FXML
    private TextField txtAnstNr;
    @FXML
    private TextField txtKostnad;
    @FXML
    private TextField txtOrt;
    @FXML
    private TextField txtPersonNr;
    @FXML
    private TextArea txtArea;
    @FXML
    private RadioButton radioTid;


    public void initialize(URL url, ResourceBundle rb) {
        txtDatum.setText(DATUM);
        txtFilnamn.setText("Overtid-" + DATUM);
        setAnstNrFocusListener();

    }

    private void setAnstNrFocusListener() {
        txtAnstNr.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue && txtAnstNr.getText().isEmpty()) // Fokus
            {
                txtAnstNr.setText("100 100 ");
            } else if (txtAnstNr.getText().equals("100 100 ")) // Ej fokus
            {
                txtAnstNr.setText("");
            }
        });
    }

    public void skapaDokument(Event event) {

        String tagforsening = "T\\\\u229\\\\'e5gf\\\\u246\\\\'f6rsening "; //RTF-format för Tågförsening

        try {
        // Hämtar data från textArea
        String[][] punktlighet = getPunktlighet();
        if (punktlighet[0][6] != null) {
            ArrayList<Blankett> blanketter = new ArrayList<>();
            Blankett lapp = null;
            int counter = 6;

            // Tid eller pengar
            String komp;
            if (radioTid.isSelected()) {
                komp = "T";
            } else {
                komp = "P";
            }

            for (int i = punktlighet.length - 1; i > -1; i--) {
                if (counter == 6) {
                    lapp = skapaNyBlankett();
                    blanketter.add(lapp);
                    counter = 1;
                }
                if (punktlighet[i][6] != null && Integer.parseInt(punktlighet[i][6]) < 0) {
                    lapp.replace("DATUM" + counter, punktlighet[i][0].substring(2)); // YYYY-MM-DD > YY-MM-DD
                    lapp.replace("TUR" + counter, punktlighet[i][1]);
                    lapp.replace("TAGNR" + counter, punktlighet[i][2]);
                    lapp.replace("FROM" + counter, punktlighet[i][3]);
                    lapp.replace("TOM" + counter, punktlighet[i][4]);
                    lapp.replace("KOMP" + counter, komp);
                    lapp.replace("VANK" + counter, punktlighet[i][5]);
                    lapp.replace("OVR" + counter, tagforsening + punktlighet[i][6]);
                    counter++;
                }
            }

            // Spara blanketter till fil
            for (Blankett lappar : blanketter) {
                lappar.removePlaceHolders();
                lappar.printFile(txtFilnamn.getText());
            }
        }
        } catch (Exception ex) {
            System.out.println("FEL PUNKTLIGHET " + ex);
        }
    }

    private Blankett skapaNyBlankett() {
        // Skapar ny blankett och fyller i överdelen
        Blankett lapp = new Blankett();
        lapp.replace("NAMN", txtNamn.getText());
        lapp.replace("ANSTNR", txtAnstNr.getText());
        lapp.replace("KOSTNAD", txtKostnad.getText());
        lapp.replace("ORT", txtOrt.getText());
        lapp.replace("PERSONNR", txtPersonNr.getText());
        lapp.replace("DATUM", txtDatum.getText());

        return lapp;
    }

    private String[][] getPunktlighet() {
        String input = txtArea.getText();
        String[] rad = input.split("\n");
        String[][] punktlighet = new String[rad.length][7];
        for (int i = 0; i < rad.length; i++) {
            String[] delar = rad[i].split("\t");
            System.arraycopy(delar, 0, punktlighet[i], 0, delar.length);
        }
        return punktlighet;
    }

}
