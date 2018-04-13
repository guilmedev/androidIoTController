package com.example.aztlan.aztlan_iot;

import android.widget.EditText;

import com.example.aztlan.aztlan_iot.modelo.Dispositivo;

/**
 * Created by Aztlan on 03/08/2017.
 */

public class DeviceHelper {

    private final EditText nomedodispositivo;
    private final EditText nomedoAmbiente;


    public DeviceHelper(TelaDeCadastro telaDeCadastro) {

        // Recolhe os dados digitados
        nomedodispositivo = (EditText) telaDeCadastro.findViewById(R.id.Cadastro_campo_nome_dispositivo);
        nomedoAmbiente = (EditText) telaDeCadastro.findViewById(R.id.Cadastro_campo_nome_ambiente);
    }


    public DeviceHelper(TelaDeResgate telaDeResgate) {

        // Recolhe os dados digitados
        nomedodispositivo = (EditText) telaDeResgate.findViewById(R.id.TelaDeResgate_dispositivo_digitado);
        nomedoAmbiente = (EditText) telaDeResgate.findViewById(R.id.TelaDeResgate_ambiente_digitado);
    }


    public Dispositivo pegadispositivo() {

        Dispositivo dispositivo = new Dispositivo();
        // Monta um dispositivo com os nomes das textviews
        dispositivo.setNome(nomedodispositivo.getText().toString());
        dispositivo.setAmbiente(nomedoAmbiente.getText().toString());

        return dispositivo;
    }
}
