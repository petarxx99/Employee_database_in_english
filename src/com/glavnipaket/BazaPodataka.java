package com.glavnipaket;


import com.util.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BazaPodataka {

    private Class[] anotiraneKlase;

    public static final int  SVE_OK = 1, NISTA_ZA_UPDATE=-1, NEISPRAVNE_GODINE=-2,  NEISPRAVNI_DOHODAK=-3, HIBERNATE_EXCEPTION = -4, NEISPRAVAN_ID=-5;
    public static final int NEUSPELO_BRISANJE = -1;

    public BazaPodataka(Class[] anotiraneKlase){
        this.anotiraneKlase = anotiraneKlase;
    }


    public void ubaciteZaposlenogUBazu(Zaposleni zaposleni){
        Session session = otvoriSesiju();
        session.save(zaposleni);
        zatvoriSesiju(session);
    }


    public List<Zaposleni> prikaziSveZaposlene(){
        List<Zaposleni> zaReturn = null;
        String queryString = String.format("FROM Zaposleni as zaposleni");

        Session session = otvoriSesiju();
        Transaction transaction = null;
        try{
            transaction = session.beginTransaction();
            zaReturn = session.createQuery(queryString).list();
            transaction.commit();
        } catch (HibernateException e){
            rollbackTransactionAkoNijeNull(transaction);
            e.printStackTrace();
        } finally {
            zatvoriSesiju(session);
        }

        return zaReturn;
    }


    public List<Zaposleni> prikaziZaposlenePoUslovu(String uslov, String alijasKlase){
        List<Zaposleni> zaReturn = null;

        String queryString = String.format("FROM Zaposleni as %s WHERE %s", alijasKlase, uslov);
        Transaction transaction = null;
        Session session = otvoriSesiju();
        try{
            transaction = session.beginTransaction();
            zaReturn = session.createQuery(queryString).list();
            transaction.commit();
        } catch (HibernateException e){
            rollbackTransactionAkoNijeNull(transaction);
            e.printStackTrace();
        } finally {
            zatvoriSesiju(session);
        }

        return zaReturn;
    }




    public int izbrisiteZaposlenog(int id){
        String queryString = String.format("DELETE FROM Zaposleni as zaposleni WHERE zaposleni.pid=%s", id);
        Session session = otvoriSesiju();
        Transaction transaction = null;
        int zaReturn = NEUSPELO_BRISANJE;
        try {
            transaction = session.beginTransaction();
            zaReturn = session.createQuery(queryString).executeUpdate();
            transaction.commit();
        }catch(HibernateException e){
            rollbackTransactionAkoNijeNull(transaction);
            e.printStackTrace();
            zaReturn = NEUSPELO_BRISANJE;
        } finally {
            zatvoriSesiju(session);
        }

        return zaReturn;
    }


    public int update(int id, String ime, Integer godine, String adresa, BigDecimal visinaDohotka, Class klasaZaposleni){
        Session session = otvoriSesiju();
        Transaction transaction = null;
        try{
            transaction = session.beginTransaction();
            Zaposleni zaposleni = (Zaposleni) session.get(klasaZaposleni, id);
            if (zaposleni == null){
                return NEISPRAVAN_ID;
            }

            if (ime != null){
                zaposleni.setIme(ime);
            }
            if (adresa != null){
                zaposleni.setAdresa(adresa);
            }
            if(godine != null){
                zaposleni.setGodine(godine);
            }
            if(visinaDohotka != null){
                zaposleni.setVisinaDohotka(visinaDohotka);
            }
            session.update(zaposleni);
            transaction.commit();
            return SVE_OK;
        } catch(HibernateException e){
            rollbackTransactionAkoNijeNull(transaction);
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Upit nije uspeo.");
            return HIBERNATE_EXCEPTION;
        } finally {
            zatvoriSesiju(session);
        }
    }


    public Session otvoriSesiju(){
        return HibernateUtil.createSessionFactory(anotiraneKlase).openSession();
    }

    public void zatvoriSesiju(Session session){
        session.close();
        HibernateUtil.close();
    }

    public void rollbackTransactionAkoNijeNull(Transaction transaction){
        if(transaction != null){
            transaction.rollback();
        }
    }

}

