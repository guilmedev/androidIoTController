package com.example.aztlan.aztlan_iot;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.icu.util.RangeValueIterator;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.renderscript.Element;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Aztlan on 27/11/2017.
 */

public class ListaWifi extends ListActivity {

    public static String SSID = null;
    String[] items;
    WifiManager wifiManager;
    private List<ScanResult> wifilist;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Instancia o wifimanager
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        //Inicia a procura ...
        wifiManager.startScan();

        //Obtem a lista de redes wifi ...
        wifilist = wifiManager.getScanResults();

        //Inicia o Adapter
        ArrayAdapter<String> ArrayWifi = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        //Preenche a lista, a partir do ScanResult
        if (wifilist.size() > 0){
        //Percorre pelo ScanResult
            for (ScanResult wifiDevice : wifilist){
                String ssid = wifiDevice.SSID.toString();
                ArrayWifi.add(ssid);
            }
        //Passa a array para o adapter mostrar na tela
            setListAdapter(ArrayWifi);
        }
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //Pega o nome do item clicado
        String ssid = ((TextView)v) .getText().toString();

        //Passa para a Activity de cadastro
        Intent retornaSSID = new Intent();
        //Coloca o nome do ssid clicado no putextra
        retornaSSID.putExtra(SSID,ssid);
        //Da um retorno de OK para a Activity
        //A Activity que irá receber o dado, vai veficiar no metodo onActivityForResult
        setResult(RESULT_OK,retornaSSID);
        //Fecha a aplicação
        finish();
    }
}
