package com.example.projectlocalizador.Entidades;

public class UsuarioEntidad {

    private String user;
    private String pass;
    private String user_nombres;
    private String user_apellidos;
    private String perfil;

    public UsuarioEntidad(String user, String user_nombres) {
        this.user = user;
        this.user_nombres = user_nombres;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUser_nombres() {
        return user_nombres;
    }

    public void setUser_nombres(String user_nombres) {
        this.user_nombres = user_nombres;
    }

    public String getUser_apellidos() {
        return user_apellidos;
    }

    public void setUser_apellidos(String user_apellidos) {
        this.user_apellidos = user_apellidos;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }
}
