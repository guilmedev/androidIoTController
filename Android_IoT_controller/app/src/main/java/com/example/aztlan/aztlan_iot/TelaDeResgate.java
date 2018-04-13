package com.example.aztlan.aztlan_iot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aztlan.aztlan_iot.dao.DispositivoDAO;
import com.example.aztlan.aztlan_iot.modelo.Dispositivo;

public class TelaDeResgate extends AppCompatActivity {

    private DispositivoDAO dao;
    private Toolbar toolbar;
    private TextView nome;
    private TextView ambiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_de_resgate);

        //Declara widgets
        nome = (TextView) findViewById(R.id.TelaDeResgate_dispositivo_digitado);
        ambiente = (TextView) findViewById(R.id.TelaDeResgate_ambiente_digitado);

        Button btnResgataDispositivo = (Button) findViewById(R.id.TelaDeResgate_btn_resgate);

        toolbar = (Toolbar) findViewById(R.id.TelaDeResgateToolbar);
        //Seta parametros da toolbar
        toolbar.setTitle("Resgate de Dispositivo");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        //Metodo para voltar clicando na seta da toolbar (NavigationIcon)
        if (toolbar!=null){
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        //Instancia o dao
        dao = new DispositivoDAO(TelaDeResgate.this);

        final DeviceHelper helper = new DeviceHelper(TelaDeResgate.this);

        btnResgataDispositivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Se deixar algum campo de texto, avisa o usuário
                if(nome.getText().toString().matches("") || ambiente.getText().toString().matches(""))
                {
                    Toast.makeText(TelaDeResgate.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else {
                    /*
                    Instancia um novo disposivo para ser salvo a partir do helper que montou
                     o dispositivo usando o metodo pegadispositivo:
                     esse metedo agigliza o processo de nome.gettext() ,ambiente.gettext() etc ...
                    */
                    final Dispositivo dispositivo = helper.pegadispositivo();

                    //Salva o dispositivo no Banco
                    dao.insere(dispositivo);
                    dao.close();

                    //Fecha a activity
                    finish();
                }

            }
        });

    }
}
