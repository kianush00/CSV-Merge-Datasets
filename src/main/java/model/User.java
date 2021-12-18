package model;

import java.util.ArrayList;
import java.util.Date;

public class User {
    private int id;
    private String correo;
    private Date ultima_conexion;
    private boolean inactivo;
    private ArrayList<User> seguidos;
    private ArrayList<User> seguidores;

    public User() {
        seguidos = new ArrayList<>();
        seguidores = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Date getUltima_conexion() {
        return ultima_conexion;
    }

    public void setUltima_conexion(Date ultima_conexion) {
        this.ultima_conexion = ultima_conexion;
    }

    public boolean isInactivo() {
        return inactivo;
    }

    public void setInactivo(boolean inactivo) {
        this.inactivo = inactivo;
    }

    public ArrayList<User> getSeguidos() {
        return seguidos;
    }

    public void setSeguidos(ArrayList<User> seguidos) {
        this.seguidos = seguidos;
    }

    public ArrayList<User> getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(ArrayList<User> seguidores) {
        this.seguidores = seguidores;
    }
}