package com.mainpackage.main;

import com.mainpackage.Zaposleni;
import com.mainpackage.database.BazaPodataka;
import com.mainpackage.display.GlavniProzor;

public class Mainclass {

    public static void main(String[] args){
        BazaPodataka baza = new BazaPodataka(new Class[]{Zaposleni.class});
        GlavniProzor glavniProzor = new GlavniProzor(500, 500, baza,  50, 50, new int[]{8, 4});

    }


}
