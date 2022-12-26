package com.mainpackage.display;
import com.mainpackage.database.BazaPodataka;
import com.mainpackage.Zaposleni;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.List;


public class GlavniProzor extends JFrame {
    int duzina, visina;
    private JTextArea textArea;
    private BazaPodataka baza;
    private String imeTabele;
    private int maxDuzinaImena;
    private int maxDuzinaAdrese;
    private int[] prePosleZapeteDohodak;
    private String maxDohodak;
    private String ddlOgranicenjaTabeleZaposleni;

    public GlavniProzor(int duzina, int visina, BazaPodataka baza, int maxDuzinaImena, int maxDuzinaAdrese, int[] prePosleZapeteDohodak){
        this.duzina = duzina;
        this.visina = visina;
        this.baza = baza;
        this.maxDuzinaImena = maxDuzinaImena;
        this.maxDuzinaAdrese = maxDuzinaAdrese;
        this.prePosleZapeteDohodak = prePosleZapeteDohodak;
        this.maxDohodak = "For income you can write " + prePosleZapeteDohodak[0] + " digits before the decimal point and " + prePosleZapeteDohodak[1] + " digits after the decimal point.";
        this.ddlOgranicenjaTabeleZaposleni = String.format("The maximum number of characters for name is %s, the maximum number of characters for address is %s, %s", maxDuzinaImena, maxDuzinaAdrese, maxDohodak);


        this.setSize(duzina, visina);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setLayout(new GridLayout(2, 1));
        JPanel panelPrikaz = new JPanel();

        textArea = new JTextArea(10, 30);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panelPrikaz.add(scrollPane, BorderLayout.CENTER);
        this.getContentPane().add(panelPrikaz);


        JPanel panelIzbor = new JPanel(new GridLayout(6, 1));
        JButton dugmeUnosNovog = new JButton("Insert new employee");
        dugmeUnosNovog.addActionListener(this::unesiZaposlenog);
        panelIzbor.add(dugmeUnosNovog);

        JButton dugmeIzmenaNaOsnovuId = new JButton("Update an employee based on his id");
        dugmeIzmenaNaOsnovuId.addActionListener(this::izmeniNaOsnovuId);
        panelIzbor.add(dugmeIzmenaNaOsnovuId);

        JButton dugmeIzbrisiNaOsnovuId = new JButton("Delete an employee based on his id");
        dugmeIzbrisiNaOsnovuId.addActionListener(this::izbrisiNaOsnovuId);
        panelIzbor.add(dugmeIzbrisiNaOsnovuId);

        JButton dugmePrikazSvih = new JButton("Data about all employees");
        dugmePrikazSvih.addActionListener(this::prikaziSveZaposlene);
        panelIzbor.add(dugmePrikazSvih);

        JButton dugmePretraga = new JButton("Search employees");
        dugmePretraga.addActionListener(this::pretraziZaposlene);
        panelIzbor.add(dugmePretraga);

        JButton dugmeObrisi = new JButton("Clear text");
        dugmeObrisi.addActionListener((ActionEvent event) -> {textArea.setText("");});
        panelIzbor.add(dugmeObrisi);

        this.getContentPane().add(panelIzbor);
        this.setVisible(true);
    }

    public JFrame napraviSporedniProzor(int duzina, int visina){
        this.setEnabled(false);
        JFrame f = napraviProzor(duzina, visina);
        GlavniProzor thisProzor = this;
        f.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e1){
                thisProzor.setEnabled(true);
                f.dispose();
            }
        });

        return f;
    }

    public JFrame napraviProzor(int duzina, int visina){
        JFrame f = new JFrame();
        f.setSize(duzina, visina);
        f.setLocationRelativeTo(null);

        return f;
    }



    public void unesiZaposlenog(ActionEvent event){
        JFrame f = napraviSporedniProzor(duzina, visina);
        JScrollPane scrollPaneObavestenja = napraviScrollPaneObavestenja();
        f.getContentPane().add(scrollPaneObavestenja, BorderLayout.PAGE_START);
        JPanel panelPodaci = new JPanel(new GridLayout(4, 2));
        
        JLabel labelaIme = new JLabel("name: ");
        JTextField tfIme = new JTextField();
        
        JLabel labelaGodine = new JLabel("age: ");
        JTextField tfGodine = new JTextField();

        
        JLabel labelaAdresa = new JLabel("address: ");
        JTextField tfAdresa = new JTextField();

        
        JLabel labelaVisinaDohotka = new JLabel("income: ");
        JTextField tfVisinaDohotka = new JTextField();
        staviKomponenteNaPanel(panelPodaci, new JComponent[]{labelaIme, tfIme, labelaGodine, tfGodine, labelaAdresa, tfAdresa, labelaVisinaDohotka, tfVisinaDohotka});


        JButton dugme = new JButton("Insert into database.");
        f.getContentPane().add(dugme, BorderLayout.PAGE_END);
        dugme.addActionListener((ActionEvent e1) -> {
             String ime = tfIme.getText();
            if(ime.length() > maxDuzinaImena || ime.length() < 1){
                JOptionPane.showMessageDialog(null, "The maximum number of characters for name is " + maxDuzinaImena + ", the minimum is 1.");
                return;
            }

             String adresa = tfAdresa.getText();
            if(adresa.length() > maxDuzinaAdrese){
                JOptionPane.showMessageDialog(null, "The maximum number of characters for address is " + maxDuzinaAdrese);
                return;
            }

             int godine= preuzmiGodine(tfGodine);
             if(godine<0){
                 JOptionPane.showMessageDialog(null, "Something was wrong when parsing age");
                 return;
             }

             BigDecimal visinaDohotka = preuzmiVisinuDohotka(tfVisinaDohotka);
             if (visinaDohotka == null){
                 JOptionPane.showMessageDialog(null, "Something went wrong while parsing income");
                 return;
             }

             try{
                 Zaposleni zaposleni = new Zaposleni(ime, godine, adresa, visinaDohotka);
                 baza.ubaciteZaposlenogUBazu(zaposleni);
                 this.setEnabled(true);
                 f.dispose();
             } catch(Exception e){
                 e.printStackTrace();
                 JOptionPane.showMessageDialog(null, "Something went wrong.");
             }

        });

        f.getContentPane().add(panelPodaci, BorderLayout.CENTER);
        f.setVisible(true);
    }





    public void izmeniNaOsnovuId(ActionEvent event){
        JFrame f = napraviSporedniProzor(duzina, visina);
        JScrollPane scrollPaneObavestenja = napraviScrollPaneObavestenja();
        f.getContentPane().add(scrollPaneObavestenja, BorderLayout.PAGE_START);

        JPanel panelPodaci = new JPanel(new GridLayout(5, 3));

        JLabel labelId = new JLabel("id to update: ");
        JTextField tfId = new JTextField(10);
        JLabel labelPrazan = new JLabel();
        staviKomponenteNaPanel(panelPodaci, new JComponent[]{labelId, tfId, labelPrazan});


        JLabel labelIme = new JLabel("name: ");
        JTextField tfIme = new JTextField();
        JCheckBox checkBoxIme = new JCheckBox();
        staviKomponenteNaPanel(panelPodaci, new JComponent[]{labelIme, tfIme, checkBoxIme});


        JLabel labelGodine = new JLabel("age: ");
        JTextField tfGodine = new JTextField();
        JCheckBox checkBoxGodine = new JCheckBox();
        staviKomponenteNaPanel(panelPodaci, new JComponent[]{labelGodine, tfGodine, checkBoxGodine});

        JLabel labelAdresa = new JLabel("address: ");
        JTextField tfAdresa = new JTextField();
        JCheckBox checkBoxAdresa = new JCheckBox();
        staviKomponenteNaPanel(panelPodaci, new JComponent[]{labelAdresa, tfAdresa, checkBoxAdresa});

        JLabel labelVisinaDohotka = new JLabel("income");
        JTextField tfVisinaDohotka = new JTextField();
        JCheckBox checkBoxVisinaDohotka = new JCheckBox();
        staviKomponenteNaPanel(panelPodaci, new JComponent[]{labelVisinaDohotka, tfVisinaDohotka, checkBoxVisinaDohotka});

        f.getContentPane().add(panelPodaci, BorderLayout.CENTER);

        JButton dugmeIzmeni = new JButton("Update");
        dugmeIzmeni.addActionListener((ActionEvent e1) -> {
            int id=-1;
            try{
                id = Integer.parseInt(tfId.getText());
            } catch(Exception e){
                JOptionPane.showMessageDialog(null, "You haven't entered a valid id.");
                return;
            }

            String ime=null, adresa=null;
            Integer godine = null;
            BigDecimal visinaDohotka = null;

            if (checkBoxIme.isSelected()){
                ime = tfIme.getText();
                if(ime.length() > maxDuzinaImena || ime.length() < 1){
                    JOptionPane.showMessageDialog(null, "The maximum number of characters for name is " + maxDuzinaImena);
                    return;
                }
            }

            if(checkBoxAdresa.isSelected()){
                adresa = tfAdresa.getText();
                if(adresa.length() > maxDuzinaAdrese){
                    JOptionPane.showMessageDialog(null, "The maximum number of characters for address is " + maxDuzinaAdrese);
                    return;
                }
            }

            if(checkBoxVisinaDohotka.isSelected()){
                try{
                    visinaDohotka = new BigDecimal(tfVisinaDohotka.getText());
                } catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Error while parsing income");
                    return;
                }
            }

            if(checkBoxGodine.isSelected()){
                try{
                    godine = Integer.parseInt(tfGodine.getText());
                    if(godine < 0){
                        JOptionPane.showMessageDialog(null, "Something went wrong while parsing age");
                        return;
                    }
                } catch(Exception e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Invalid age");
                }
            }

            int daLiJeSveProsloKakoTreba = baza.update(id, ime, godine, adresa, visinaDohotka, Zaposleni.class);
            switch(daLiJeSveProsloKakoTreba){
                case BazaPodataka.SVE_OK:{
                    this.setEnabled(true);
                    f.dispose();
                    break;
                }
                case BazaPodataka.NEISPRAVAN_ID:{
                    JOptionPane.showMessageDialog(null, "Invalid id");
                    break;
                }
                case BazaPodataka.HIBERNATE_EXCEPTION:{
                    JOptionPane.showMessageDialog(null, "Something went wrong when dealing with the database.");
                    break;
                }
                default: {}
            }
        });
        f.getContentPane().add(dugmeIzmeni, BorderLayout.PAGE_END);
        f.setVisible(true);
    }






    public void izbrisiNaOsnovuId(ActionEvent event){
        JFrame f = napraviSporedniProzor(duzina, visina);
        f.setLayout(new FlowLayout());

        JLabel labela = new JLabel("Enter id to delete: ");
        JTextField tf = new JTextField(10);
        JButton dugme= new JButton("Delete employee.");

        f.getContentPane().add(labela);
        f.getContentPane().add(tf);
        f.getContentPane().add(dugme);

        dugme.addActionListener((ActionEvent e1) -> {
            int id=-1;
            try{
                id = Integer.parseInt(tf.getText());
            } catch(Exception e){
                JOptionPane.showMessageDialog(null, "You haven't entered a valid id.");
                return;
            }

            try{
                baza.izbrisiteZaposlenog(id);
                this.setEnabled(true);
                f.dispose();
            } catch(Exception e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "There was an error while trying to delete the employee");
            }
        });

        f.setVisible(true);
    }





    public void prikaziSveZaposlene(ActionEvent event){
        ispisiZaposlene(baza.prikaziSveZaposlene());
    }

    public void ispisiZaposlene(List<Zaposleni> zaposleni){
        StringBuilder sb = new StringBuilder();
        for(Zaposleni z : zaposleni){
            sb.append(z);
            sb.append("\n");
        }

        textArea.setText(sb.toString());
    }


    public void pretraziZaposlene(ActionEvent event){
        JFrame f = napraviSporedniProzor(duzina, visina);
        JLabel label = new JLabel("Check the boxes with which you want to query employees.");
        f.getContentPane().add(label, BorderLayout.PAGE_START);


        JPanel panelPodaci = new JPanel(new GridLayout(4, 1));
        JPanel panelIme = new JPanel(new FlowLayout());
        
        JLabel labelIme = new JLabel("name: ");
        JTextField tfIme = new JTextField(10);
        JCheckBox checkBoxIme = new JCheckBox();
        staviKomponenteNaPanel(panelIme, new JComponent[]{labelIme, tfIme, checkBoxIme});
        panelPodaci.add(panelIme);

        final int MIN = 0;
        final int MAX = 1;

        final int MAX_GODINE = 200;
        final int MIN_GODINE = 1;
        int[] minMaxGodine = new int[2];
        minMaxGodine[MIN] = MIN_GODINE;
        minMaxGodine[MAX] = MAX_GODINE;

        JPanel panelGodine = new JPanel(new FlowLayout());
        JLabel labelGodine = new JLabel("minimum age: ");
        JSpinner spinnerMinGodine = napraviNumberSpinner(MIN_GODINE, MAX_GODINE, 1, MIN_GODINE);
        JLabel godineDo = new JLabel("maximum age: ");
        JSpinner spinnerMaxGodine = napraviNumberSpinner(MIN_GODINE, MAX_GODINE, 1, MAX_GODINE);
        JCheckBox checkBoxGodine = new JCheckBox();

        staviKomponenteNaPanel(panelGodine, new JComponent[]{labelGodine, spinnerMinGodine, godineDo, spinnerMaxGodine, checkBoxGodine});
        panelPodaci.add(panelGodine);

        JPanel panelAdresa = new JPanel(new FlowLayout());
        JLabel labelAdresa = new JLabel("address: ");
        JTextField tfAdresa = new JTextField(10);
        JCheckBox checkBoxAdresa = new JCheckBox();
        staviKomponenteNaPanel(panelAdresa, new JComponent[]{labelAdresa, tfAdresa, checkBoxAdresa});
        panelPodaci.add(panelAdresa);

        int MIN_DOHODAK = 0;
        int MAX_DOHODAK = 1000000;
        int[] minMaxDohodak = new int[2];
        minMaxDohodak[MIN] = MIN_DOHODAK;
        minMaxDohodak[MAX] = MAX_DOHODAK;

        JPanel panelDohodak = new JPanel(new FlowLayout());
        JLabel labelVisinaDohotkaOd = new JLabel("min income: ");
        JSpinner spinnerMinDohodak = napraviNumberSpinner(MIN_DOHODAK, MAX_DOHODAK, 10000, MIN_DOHODAK);
        JLabel labelVisinaDohotkaDo = new JLabel("max income: ");
        JSpinner spinnerMaxDohodak = napraviNumberSpinner(MIN_DOHODAK, MAX_DOHODAK, 10000, 100000);
        JCheckBox checkBoxDohodak = new JCheckBox();

        staviKomponenteNaPanel(panelDohodak, new JComponent[]{labelVisinaDohotkaOd, spinnerMinDohodak, labelVisinaDohotkaDo, spinnerMaxDohodak, checkBoxDohodak});
        panelPodaci.add(panelDohodak);

        f.getContentPane().add(panelPodaci, BorderLayout.CENTER);

        GlavniProzor thisProzor = this;
        JButton dugme = new JButton("Search");
        f.getContentPane().add(dugme, BorderLayout.PAGE_END);
        dugme.addActionListener((ActionEvent e) -> {
            if (nijedanCheckBoxNijeSelektovan(new JCheckBox[]{checkBoxIme, checkBoxAdresa, checkBoxGodine, checkBoxDohodak})){
                JOptionPane.showMessageDialog(null, "You haven't checked any checkbox with which the employees will be queried");
                return;
            }

            String alijasKlase = "zaposleni";
            String ime = null;
            if(checkBoxIme.isSelected()){
                String imeString = tfIme.getText();
                ime = String.format("(%s.ime='%s')", alijasKlase, tfIme.getText());
            }

            String uslovGodineMin = null;
            String uslovGodineMax = null;
            if(checkBoxGodine.isSelected()) {
                int godineMin = (int) spinnerMinGodine.getValue();
                int godineMax = (int) spinnerMaxGodine.getValue();
                uslovGodineMin = String.format("(%s.godine < %d)", alijasKlase, godineMax);
                uslovGodineMax = String.format("(%s.godine > %d)", alijasKlase, godineMin);
            }

            String adresa = null;
            if(checkBoxAdresa.isSelected()){
                String adresaString = tfAdresa.getText();
                adresa = String.format("(%s.adresa='%s')", alijasKlase, tfAdresa.getText());
            }

            String uslovDohotkaMin = null;
            String uslovDohotkaMax = null;
            if(checkBoxDohodak.isSelected()) {
                int minDohodak = (int) spinnerMinDohodak.getValue();
                int maxDohodak = (int) spinnerMaxDohodak.getValue();
                uslovDohotkaMin = String.format("(%s.visinaDohotka < %d)", alijasKlase, maxDohodak);
                uslovDohotkaMax = String.format("(%s.visinaDohotka > %d)", alijasKlase, minDohodak);
            }

            String[] uslovi = new String[]{ime, uslovGodineMin, uslovGodineMax, adresa, uslovDohotkaMin, uslovDohotkaMax};
            String uslov = napraviUslovOdStringovaKojiNisuNull(uslovi);
            List<Zaposleni> zaposleni = baza.prikaziZaposlenePoUslovu(uslov, alijasKlase);
            if(zaposleni != null){
                ispisiZaposlene(zaposleni);
                thisProzor.setEnabled(true);
                f.dispose();
            }
        });
        f.setVisible(true);
    }

    public boolean nijedanCheckBoxNijeSelektovan(JCheckBox[] checkBoxovi){
        for(JCheckBox checkBox : checkBoxovi){
            if(checkBox.isSelected()){
                return false;
            }
        }

        return true;
    }


    public void staviKomponenteNaPanel(JPanel kontejner, JComponent[] komponente){
        for(JComponent c : komponente){
            kontejner.add(c);
        }
    }

    public String napraviUslovOdStringovaKojiNisuNull(String[] uslovi){
        StringBuilder sb = new StringBuilder();
        int brojUslovaDoSada = 0;

        for(int i=0; i<uslovi.length; i++){
            if(uslovi[i] != null){
                if(brojUslovaDoSada != 0){
                    sb.append("AND");
                }
                sb.append(uslovi[i]);
                brojUslovaDoSada++;
            }
        }

        return sb.toString();
    }

    public JSpinner napraviNumberSpinner(int min, int max, int stepSize, int pocetnaVrednost){
        SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel();
        spinnerNumberModel.setMinimum(min);
        spinnerNumberModel.setMaximum(max);
        spinnerNumberModel.setStepSize(stepSize);
        if(pocetnaVrednost>max){
            spinnerNumberModel.setValue(max);
        } else if (pocetnaVrednost < min){
            spinnerNumberModel.setValue(min);
        } else {
            spinnerNumberModel.setValue(pocetnaVrednost);
        }
        
        return new JSpinner(spinnerNumberModel);
    }


    public int preuzmiGodine(JTextField tfGodine){
        int godine=-1;
        try{
            godine = Integer.parseInt(tfGodine.getText());
        } catch(Exception e){
            JOptionPane.showMessageDialog(null, "Age is invalid.");
            return -1;
        }
        if(godine<0){
            JOptionPane.showMessageDialog(null, "Age is invalid.");
            return -1;
        }

        return godine;
    }

    public BigDecimal preuzmiVisinuDohotka(JTextField tfVisinaDohotka){
        BigDecimal visinaDohotka;
        try{
            double visinaDohotkaDouble = Double.parseDouble(tfVisinaDohotka.getText());
            visinaDohotka = new BigDecimal(visinaDohotkaDouble);
        } catch(Exception e){
            JOptionPane.showMessageDialog(null, "Income is invalid.");
            return null;
        }
        if (visinaDohotka == null){
            JOptionPane.showMessageDialog(null, "Something went wrong when parsing income.");
            return null;
        }


        return visinaDohotka;
    }

    public JScrollPane napraviScrollPaneObavestenja(){
        JTextArea ta = new JTextArea(ddlOgranicenjaTabeleZaposleni);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(ta);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }




}

