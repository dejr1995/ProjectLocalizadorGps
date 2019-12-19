package com.example.projectlocalizador.Entidades;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ActivoEntidad {

    String id_activo;
    private String ID;
    private String nombre_activo;
    private String direccion;
    private String coordenadas;
    private String latitud;
    private String longitud;
    private String descripcion;
    private String serie;
    private String modelo;
    private String marca;
    private String dato;
    private String id_user;
    private String id_equipo;



    public ActivoEntidad() {
    }

    public ActivoEntidad(String id_activo, String nombre_activo) {
        this.id_activo = id_activo;
        this.nombre_activo = nombre_activo;
    }

    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;
        try{
            byte[] byteCode= Base64.decode(dato,Base64.DEFAULT);
            this.imagen= BitmapFactory.decodeByteArray(byteCode, 0, byteCode.length);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Bitmap imagen;

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    public String getId_activo() {
        return id_activo;
    }

    public void setId_activo(String id_activo) {
        this.id_activo = id_activo;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNombre_activo() {
        return nombre_activo;
    }

    public void setNombre_activo(String nombre_activo) {
        this.nombre_activo = nombre_activo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getId_equipo() {
        return id_equipo;
    }

    public void setId_equipo(String id_equipo) {
        this.id_equipo = id_equipo;
    }
}