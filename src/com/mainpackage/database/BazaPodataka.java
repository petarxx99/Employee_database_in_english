package com.mainpackage.database;


import com.mainpackage.Employee;
import com.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;

public class BazaPodataka {

    private Class[] anotiraneKlase;

    public static final int  SVE_OK = 1, NISTA_ZA_UPDATE=-1, NEISPRAVNE_GODINE=-2,  NEISPRAVNI_DOHODAK=-3, HIBERNATE_EXCEPTION = -4, NEISPRAVAN_ID=-5;
    public static final int NEUSPELO_BRISANJE = -1;

    public BazaPodataka(Class[] anotiraneKlase){
        this.anotiraneKlase = anotiraneKlase;
    }


    public void ubaciteZaposlenogUBazu(Employee employee){
        Session session = otvoriSesiju();
        session.save(employee);
        zatvoriSesiju(session);
    }


    public List<Employee> prikaziSveZaposlene(){
        List<Employee> zaReturn = null;
        String queryString = String.format("FROM Employee as zaposleni");

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


    public List<Employee> prikaziZaposlenePoUslovu(String uslov, String alijasKlase){
        List<Employee> zaReturn = null;

        String queryString = String.format("FROM Employee as %s WHERE %s", alijasKlase, uslov);
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
        String queryString = String.format("DELETE FROM Employee as zaposleni WHERE zaposleni.pid=%s", id);
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
            Employee employee = (Employee) session.get(klasaZaposleni, id);
            if (employee == null){
                return NEISPRAVAN_ID;
            }

            if (ime != null){
                employee.setIme(ime);
            }
            if (adresa != null){
                employee.setAdresa(adresa);
            }
            if(godine != null){
                employee.setGodine(godine);
            }
            if(visinaDohotka != null){
                employee.setVisinaDohotka(visinaDohotka);
            }
            session.update(employee);
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

