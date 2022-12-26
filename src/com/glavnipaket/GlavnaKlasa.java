package com.glavnipaket;

import com.glavnipaket.prozor.GlavniProzor;

public class GlavnaKlasa {

    public static void main(String[] args){
        BazaPodataka baza = new BazaPodataka(new Class[]{Zaposleni.class});
        GlavniProzor glavniProzor = new GlavniProzor(500, 500, baza,  50, 50, new int[]{8, 4});

    }


}
