package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tuser")
public class User {
    @Id
    private String login;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    //private String rentedPlate;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rentedplate", referencedColumnName = "plate")
    private Vehicle vehicle;

    public User(String login, String password, Role role, Vehicle vehicle) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.vehicle = vehicle;
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.role = Role.USER;
    }

    public User() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", vehicle=" + vehicle +
                '}';
    }

    public String toCSV() {
        return new StringBuilder()
                .append(this.getClass().getSimpleName())
                .append(";")
                .append(this.login)
                .append(";")
                .append(this.password)
                .append(";")
                .append(this.role)
                .append(this.vehicle)
                .toString();
    }

    public enum Role {
        USER,ADMIN;
    }
}
