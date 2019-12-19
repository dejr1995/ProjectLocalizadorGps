package com.example.projectlocalizador.Entidades;

public class EntidadEquipo {

    String id_equipo;
    private String coordenadas;
    private String latitud;
    private String longitud;
    private String estado;


    public EntidadEquipo() {
    }

    public EntidadEquipo(String id_equipo, String estado) {
        this.id_equipo = id_equipo;
        this.estado = estado;
    }

    public String getId_equipo() {
        return id_equipo;
    }

    public void setId_equipo(String id_equipo) {
        this.id_equipo = id_equipo;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
