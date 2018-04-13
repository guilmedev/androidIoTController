package com.example.aztlan.aztlan_iot;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.aztlan.aztlan_iot.adapter.DispositivosAdapter;
import com.example.aztlan.aztlan_iot.dao.DispositivoDAO;
import com.example.aztlan.aztlan_iot.modelo.Dispositivo;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity {

     private ListView listaDevices;
     private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Popula Toolbar
        toolbar = (Toolbar) findViewById(R.id.MainToolar);
        toolbar.setTitle("Lista de dispositivos");
        toolbar.setSubtitle("Seus dispositivos cadastrados");
        //toolbar.setTitleTextColor(getColor(R.color.white));

        setSupportActionBar(toolbar);

        //Floating Button
        floatingActionButton = (FloatingActionButton) findViewById(R.id.mainFloatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent abreTelaDeCadastro = new Intent(MainActivity.this,TelaDeCadastro.class);
                startActivity(abreTelaDeCadastro); // abre tela de cadastro
            }
        });


    }



    @Override
    protected void onResume() {
        super.onResume();
        //Popula a lista do banco
        carregaLista();



    }

    private void carregaLista() {
        //Busca a lista do banco de dados
        DispositivoDAO dao = new DispositivoDAO(this);
        List<Dispositivo> dispositivos = dao.buscaDispositivos();
        dao.close();

        listaDevices = (ListView) findViewById(R.id.deviceList);

        //ArrayAdapter<Dispositivo> adapter = new ArrayAdapter<Dispositivo>(this, android.R.layout.simple_list_item_1, dispositivos);
        DispositivosAdapter adapter = new DispositivosAdapter(this,dispositivos);


        listaDevices.setAdapter(adapter);

        registerForContextMenu(listaDevices);


//        if (adapter.getCount() == 0){
//            Toast.makeText(this, "Lista Vazia", Toast.LENGTH_SHORT).show();
//        }else {
//            Toast.makeText(this, "Tem dados", Toast.LENGTH_SHORT).show();
//        }




        listaDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long l) {
                    Dispositivo dispositivo = (Dispositivo) listaDevices.getItemAtPosition(position);


                //Abre tela de acao e leva o nome do dispositivo criado para popular dos dados
                // e fazer a conexão com o broker
                Intent abreTelaDeAcao = new Intent(MainActivity.this,TelaDeAcao.class);
                abreTelaDeAcao.putExtra("Dispositivo", dispositivo);
                startActivity(abreTelaDeAcao);




            }
        });

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem deletar = menu.add("Apagar");

        MenuItem editar = menu.add("Editar");

        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                Dispositivo dispositivo = (Dispositivo) listaDevices.getItemAtPosition(info.position);

                DispositivoDAO dao = new DispositivoDAO(MainActivity.this);
                dao.deleta(dispositivo);
                dao.close();
                carregaLista();
                return false;
            }
        });

        editar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(MainActivity.this, "Editar device", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()){

            case R.id.menuResgatar:

                Intent abreTelaResgate = new Intent(MainActivity.this,TelaDeResgate.class);
                startActivity(abreTelaResgate); // abre tela de cadastro
                break;

         }

        return super.onOptionsItemSelected(item);
    }
}
