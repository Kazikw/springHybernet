package org.example;

import org.example.authenticate.Authenticator;
import org.example.configuration.HibernateUtil;
import org.example.dao.IUserRepository;
import org.example.dao.IVehicleRepository;
import org.example.dao.hibernate.UserDAO;
import org.example.dao.hibernate.VehicleDAO;
import org.example.model.Car;
import org.example.model.Motorcycle;
import org.example.model.User;
import org.example.model.Vehicle;

import java.util.Scanner;

public class App {
    public static  User user = null;
    private final Scanner scanner = new Scanner(System.in);
    private final IUserRepository iur = UserDAO.getInstance(HibernateUtil.getSessionFactory());


    //private final IVehicleRepository ivr = new VehicleDAO(HibernateUtil.getSessionFactory());
    private final IVehicleRepository ivr = VehicleDAO.getInstance(HibernateUtil.getSessionFactory());
    public void run() {

        System.out.println("LOG IN");

        user = Authenticator.login(scanner.nextLine(),scanner.nextLine());
        if(user!=null){
            System.out.println("logged in!!");

        String response = "";
        boolean running=true;
        while(running) {

            System.out.println("-----------MENU------------");
            System.out.println("00 - show info");
            System.out.println("01 - show cars");
            System.out.println("02 - show users");
            System.out.println("1 - rent car");
            System.out.println("2 - return car");
            System.out.println("3 - show car info");
            System.out.println("6 - add car");
            System.out.println("7 - remove car");
            System.out.println("8 - add user");
            System.out.println("9 - remove user");
            response = scanner.nextLine();
            switch (response) {
                case "00":
                        user = iur.getUser(user.getLogin());
                        System.out.println(user);
                    break;
                case "01":
                    for (Vehicle v : ivr.getVehicles()) {
                        System.out.println(v);
                    }
                    break;
                case "02":
                    for (User u: iur.getUsers()) {
                        System.out.println(u);
                    }
                    break;
                case "1":
                    System.out.println("Rent car by plates:");
                    String plate = scanner.nextLine();
                    boolean result = ivr.rentVehicle(plate,user.getLogin());
                    user = iur.getUser(user.getLogin());
                    if (result){System.out.println("Pomyslnie wynajeto");}
                    else {System.out.println("Nie udalo sie wynajac cos poszlo nie tak!!!");}
                    break;
                case "2":
                    System.out.println("function for return car");

                    try {
                        String plateForReturn = user.getVehicle().getPlate();
                        ivr.returnVehicle(plateForReturn,user.getLogin());
                    }catch (RuntimeException e){
                        System.out.println("Ten user nie ma wyporzyczonego samochodu!!!");
                    }
                    user = iur.getUser(user.getLogin());
                    break;
                case "3":
                    System.out.println("plates:");
                    String plateToShow = scanner.nextLine();
                    System.out.println(ivr.getVehicle(plateToShow));

                    break;
                case "6":
                    System.out.println("add car (only) separator is ; String brand, String model, int year, double price, String plate ");
                    ////Motorcycle(String brand, String model, int year, double price, String plate, String category)
                    String line = scanner.nextLine();
                    String[] arr = line.split(";");
                    System.out.println("what do you want to add? Car/Motrocycle");
                    line = scanner.nextLine();
                    if (line.equals("Car")) {
                        ivr.addVehicle(new Car(arr[0],
                                        arr[1],
                                        Integer.parseInt(arr[2]),
                                        Double.parseDouble(arr[3]),
                                        arr[4]));
                    }else if(line.equals("Motrocycle")) {
                    ivr.addVehicle(new Motorcycle(arr[0],
                            arr[1],
                            Integer.parseInt(arr[2]),
                            Double.parseDouble(arr[3]),
                            arr[4], arr[5]));
                }else{System.out.println("Zle wybrane: Car/Motrocycle");}
                    break;

                case "7":
                    System.out.println("remove car, by plate:");
                    String  removePlate = scanner.nextLine();
                    ivr.removeVehicle(removePlate);
                    break;


                case "8":
                    System.out.println("add user (only) separator is ; String login, String password");
                    line = scanner.nextLine();
                    //arr = line.split(";");
                    String[] arr2 = line.split(";");
                    User user1 = new User(arr2[0], arr2[1]);
                    if (iur.getUser(arr2[0]) == null){
                        iur.addUser(user1);
                        //iur.addUser(new User(arr2[0], arr2[1]));
                    }
                    else {
                        System.out.println("Proba dodania usera ktory juz istnieje");

                    }
                    break;
// add  test test1234   ///   test;tester1234
                //kazik;kazik1234
                case "9":
                    System.out.println("remove user by login:");
                    String  removeLogin = scanner.nextLine();
                    iur.removeUser(removeLogin);
                    break;

                default:
                    running=false;
                }
            }
        }else{
            System.out.println("Bledne dane!");
        }
        System.exit(0);
    }
}