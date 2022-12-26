package com.glavnipaket;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name="zaposleni")
public class Zaposleni {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int pid;

    @Column(name = "ime")
    private String ime;

    @Column(name = "godine")
    private int godine;

    @Column(name = "adresa")
    private String adresa;

    @Column(name = "visina_dohotka")
    private BigDecimal visinaDohotka;

    public Zaposleni(){}

    public Zaposleni(String ime, int godine, String adresa, BigDecimal visinaDohotka) {
        this.ime = ime;
        this.godine = godine;
        this.adresa = adresa;
        this.visinaDohotka = visinaDohotka;
    }

    public Zaposleni(int id, String ime, int godine, String adresa, BigDecimal visinaDohotka){
        this.pid = id;
        this.ime = ime;
        this.godine = godine;
        this.adresa = adresa;
        this.visinaDohotka = visinaDohotka;
    }


    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public int getGodine() {
        return godine;
    }

    public void setGodine(int godine) {
        this.godine = godine;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public BigDecimal getVisinaDohotka() {
        return visinaDohotka;
    }

    public void setVisinaDohotka(BigDecimal visinaDohotka) {
        this.visinaDohotka = visinaDohotka;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    @Override
    public String toString(){
        return  "id= " + pid + "\n" +
                "name: " + ime + "\n" +
                "age: " + godine + "\n" +
                "address: " + adresa + "\n" +
                "income: " + visinaDohotka;
    }

}
