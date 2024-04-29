package org.example.dao.hibernate;


import org.example.authenticate.Authenticator;
import org.example.dao.IVehicleRepository;
import org.example.model.User;
import org.example.model.Vehicle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collection;


public class VehicleDAO implements IVehicleRepository {
    SessionFactory sessionFactory;
    private static VehicleDAO instance = null;

    public static VehicleDAO getInstance(SessionFactory sessionFactory) {
        assert (sessionFactory.isOpen());
        if (instance == null) {
            instance = new VehicleDAO(sessionFactory);
        }
        return instance;
    }


    private VehicleDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        }

    @Override
    public boolean rentVehicle(String plate, String login) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            User user = session.get(User.class, login);
            Vehicle vehicle = session.get(Vehicle.class, plate);

            if (user != null && vehicle != null && user.getVehicle() == null) {
                vehicle.setUser(user);
                vehicle.setRent(true);
                user.setVehicle(vehicle);

                session.saveOrUpdate(user);
                session.saveOrUpdate(vehicle);

                transaction.commit();
                return true;
            } else {
                if (transaction != null) {
                    transaction.rollback();
                }
                return false;
            }
        } catch (RuntimeException e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }


    @Override
    public boolean addVehicle(Vehicle vehicle) {
        Session session =  sessionFactory.openSession();
        Transaction transaction = null;
        boolean success=false;

        try{
            transaction = session.beginTransaction();
            session.persist(vehicle);
            transaction.commit();
            success = true;
            System.out.println("Udalo sie dodac pojazd");
        }catch (RuntimeException e){
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Nie dalo sie dodac pojazdu!!!");
            e.printStackTrace();
        }
        finally {
            session.close();
        }
        return success;
    }
    //Volkswagen;T-cross;2022;5000.00;RLA51038
    @Override
    public boolean removeVehicle(String plate) {
        //TODO: Implemented
        //
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            //User user = session.get(User.class, login);
            Vehicle vehicle = session.get(Vehicle.class, plate);
            if (vehicle != null && !vehicle.isRent()) {
                session.remove(vehicle);
                System.out.println("Pojazd usuniety");
            } else {
                System.out.println("Wystapil blad podczas usuwania pojazdu");
                return false;
            }
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }


        //
        return true;
    }

    @Override
    public Vehicle getVehicle(String plate) {
        Session session = sessionFactory.openSession();
        try {
            Vehicle vehicle = session.get(Vehicle.class, plate);
            return vehicle;
        } finally {
            session.close();
        }
    }

    //Must implement old interface. Plate is no longer needed when User has Vehicle.
    public boolean returnVehicle(String plate,String login) {
        //TODO:Implement returnVehicle method
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            User user = session.get(User.class, login);
            Vehicle vehicle = session.get(Vehicle.class, plate);

            if (user != null && vehicle != null && user.getVehicle().getPlate().equals(plate)) {
                vehicle.setUser(null);
                vehicle.setRent(false);
                user.setVehicle(null);
                session.saveOrUpdate(user);
                session.saveOrUpdate(vehicle);
                transaction.commit();
                System.out.println("Zwrocono pojazd");
                return true;
            } else {
                if (transaction != null) {
                    transaction.rollback();
                }
                System.out.println("Nie udalo sie zwrocic pojazdu!!!");
                //user != null && vehicle != null && user.getVehicle().getPlate().equals(plate)

                if (user != null) System.out.println("User nie istnieje. Cos jest bardzo zle!!!!!");
                if (user.getVehicle() == null) System.out.println("User nie ma wynajetego rzadnego samochodu!!!");
                if (vehicle != null) System.out.println("Taki samochod nie istnieje. Cos jest bardzo zle!!!!!");
             //   if (user.getVehicle().getPlate().equals(plate)) System.out.println("Przypisany samochod do usera i przekazana rejestracja nie zgadzaja sie");

                return false;
            }
        } catch (RuntimeException e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public Collection<Vehicle> getVehicles() {
        Session session = sessionFactory.openSession();
        Collection<Vehicle> vehicles;
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            vehicles = session.createQuery("FROM Vehicle", Vehicle.class).getResultList();
//            users = session.createQuery("FROM User", User.class).getResultList();
            transaction.commit();
        }catch (RuntimeException e){
            if (transaction != null) transaction.rollback();
            throw e;
        }
            finally {
            session.close();
        }
        return vehicles;
    }
}
