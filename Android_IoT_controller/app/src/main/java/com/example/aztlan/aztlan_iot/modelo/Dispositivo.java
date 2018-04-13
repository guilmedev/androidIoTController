package com.example.aztlan.aztlan_iot.modelo;

import java.io.Serializable;

/**
 * Created by Aztlan on 03/08/2017.
 */

public class Dispositivo implements Serializable{



    private  Long id;


    public Long getId() {
        return id;
    }

    private String nome;

    public String getAmbiente() {
        return ambiente;
    }

    private String ambiente;

    private String valor;


    public void setId(long id) {
        this.id = id;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return getNome();
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

}
