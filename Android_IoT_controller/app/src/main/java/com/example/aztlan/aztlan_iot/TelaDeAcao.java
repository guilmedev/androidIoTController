package com.example.aztlan.aztlan_iot;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.aztlan.aztlan_iot.modelo.Dispositivo;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.sql.Time;
import java.util.Timer;

public class TelaDeAcao extends AppCompatActivity {

    private MqttAndroidClient client; //Cliente mqtt
    //static String server = "tcp://iot.eclipse.org:1883";
    static String server = "tcp://aztlan.ddns.com.br:1883";
    private TextView nomeAcaoConexao;
    private Dispositivo dispositivo;
    private TextView consumoDeviceText;
    private Button btnLiga, btnDesliga;
    private TextView totalemreais;
    private TextView kiloWattHora;
    private float valorRecebido;
    private float tensaoDeRede = 235;
    private float tarifa = (float) 0.56;
    private float kiloWattHoraCalculo;
    private float totalemreaisCalculo;

    final private int TIMEOUTCOMANDO = 8000;

    //ProgressDialog
    ProgressDialog progressDialog = null;

    private Toolbar toolbar;
    private ToggleButton toogleButton;
    //Resposta do ESP (fisico)
    private boolean falhaNoComando;


    //private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_de_acao);

        //Cria um novo cliente mqtt
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), server, clientId);

        // Pega o device que veio da intent
        Intent intent = getIntent();
        dispositivo = (Dispositivo) intent.getSerializableExtra("Dispositivo");

        //Referencia para o nome XML
        nomeAcaoConexao = (TextView) findViewById(R.id.acao_status_conexao);
        consumoDeviceText = (TextView) findViewById(R.id.acao_consumo_device);
        totalemreais = (TextView) findViewById(R.id.acao_total_em_reais_device);
        kiloWattHora = (TextView) findViewById(R.id.acao_KiloWattHora_device);
        //aSwitch = (Switch) findViewById(R.id.switch1);

        toogleButton = (ToggleButton) findViewById(R.id.toogleButton);

        toogleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){

                   toogleButton.setBackgroundDrawable(getDrawable(R.drawable.bg_button_round));
                }else
                {
                   toogleButton.setBackgroundDrawable(getDrawable(R.drawable.bg_button_round_off));
                }
            }
        });

        //Seta parametros da toolbar
        //Popula o nome do dispositivo na toolbar
        toolbar = (Toolbar) findViewById(R.id.TelaDeAcaoToolbar);
        toolbar.setTitle(dispositivo.getNome());
        toolbar.setSubtitle("Dados do dispositivo");
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

        //Conecta ao mqtt
        Conecta_mqtt();

        //Eventos de quando chegar mensagens do broker
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                nomeAcaoConexao.setText("Falha ao conectar");
                nomeAcaoConexao.setTextColor(Color.RED);

                // Conecta de novo
                Conecta_mqtt();
            }


            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                String dadoRecebido = new String(message.getPayload());

                //Parar o progressDiaolog ... quando vier resposta, tanto ligado, quanto desligado
                //A resposta vem do subscribe nome+/retorno
                if (dadoRecebido.contains("LIGADO")){

                    if (progressDialog!= null && progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                }

                    /*Veja se o dado recebido contém a palavra "LIGADO" ou
                      a palavra "DESLIGADO". Caso tenha uma das palavras,
                      muda o estado do botão. Se o dado recebido for numero,
                      apenas muda o textView
                    */
                    if (dadoRecebido.equalsIgnoreCase("LIGADO")){

                        //Ativa o botão
                        toogleButton.setChecked(true);

                        //Muda o sprite do botão
                        toogleButton.setBackgroundDrawable(getDrawable(R.drawable.bg_button_round));

                    }else if(dadoRecebido.equalsIgnoreCase("DESLIGADO")){

                        //Desativa o botão
                        toogleButton.setChecked(false);
                        //Muda o sprite do botão
                        toogleButton.setBackgroundDrawable(getDrawable(R.drawable.bg_button_round_off));

                    }else {

                        //Atualiza o textview "consumo" com a mensagem que chegar do subscribe
                        consumoDeviceText.setText(dadoRecebido);
                    }

                    //Guarda o valor recebid na variavel valorRecebido
                    valorRecebido = Float.parseFloat(new String(message.getPayload()));

                    //Faz o calculo do kiloWattHora
                    kiloWattHoraCalculo =  (valorRecebido * tensaoDeRede) / 1000;

                    kiloWattHora.setText(String.format("%.02f",kiloWattHoraCalculo));
                    //Calcula total em reais
                    totalemreaisCalculo = kiloWattHoraCalculo * tarifa;

                    //Popula o valor em reais
                    totalemreais.setText(String.format("%.02f",totalemreaisCalculo));
             }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    //Função para desligar o progressBar passado, depois de um periodo de tempo (milli sec)
    public void timerDelayRemoveDialog(long time, final Dialog d){
        new Handler().postDelayed(new Runnable() {
            public void run() {



                if (d!= null && d.isShowing())
                {
                    d.dismiss();

                    Toast.makeText(TelaDeAcao.this, "Sem resposta do dispositivo", Toast.LENGTH_SHORT).show();
                    //Exibir um alerta para ficar mais explícito o erro.

                    //Volta para desligado "cancelando" a ação de envio
                    toogleButton.setChecked(false);
                }

                //falhaNoComando = true;



            }
        }, time);
    }

    public void toogleButtonClick(View view){

    //boolean bttoggle = aSwitch.isChecked();
    boolean btntoogle = toogleButton.isChecked();


    if ( /*bttoggle ||*/ btntoogle){

        //Manda o comando ...
        pubMqttLiga(view);

        //Mostra o progressDialog
        progressDialog = ProgressDialog.show(TelaDeAcao.this,"Aguarde","Enviando comando",true);

        //Desabilita sozinho depois de TIMEOUTCOMANDO. Caso não tenha retorno do server
        //Caso receba, a função OnMesageArrived, vai desabilitar a progressBar automaticamente
        timerDelayRemoveDialog(TIMEOUTCOMANDO,progressDialog);

    }else
    {
        pubMqttDesliga(view);

        //Mostra o progressDialog
        progressDialog = ProgressDialog.show(TelaDeAcao.this,"Aguarde","Enviando comando",true);

        //Desabilita sozinho depois de 3seg. Caso não tenha retorno do server
        //Caso receba, a função OnMesageArrived, vai desabilitar a progressBar automaticamente
        timerDelayRemoveDialog(TIMEOUTCOMANDO,progressDialog);

        //Muda o sprite do botão
       //toogleButton.setBackgroundDrawable(getDrawable(R.drawable.bg_button_round_off));
    }
}


    public void pubMqttDesliga(View view){

        String topic = dispositivo.getNome()+ "/status";
        String message = "0";

        try {
            client.publish(topic, message.getBytes(), 1, true);

        } catch (MqttException e) {

            Toast.makeText(this, "Falha ao enviar", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void pubMqttLiga(View view){

        String topic = dispositivo.getNome()+ "/status";
        String message = "1";

        try {
              client.publish(topic, message.getBytes(), 1, true);

        } catch (MqttException e) {

            Toast.makeText(this, "Falha ao enviar", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setSubscribe() {

        try {
            //Valores do sensor
            client.subscribe(dispositivo.getNome(),0);
            //valor mandado para a lampada
            client.subscribe(dispositivo.getNome()+ "/status",0);
            //Valor mandado para saber se o rele está ativado
            client.subscribe(dispositivo.getNome()+ "/retorno",0);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {

        Conecta_mqtt();

        super.onResume();
    }

    private void Conecta_mqtt() {
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    nomeAcaoConexao.setText("Conectado");
                    nomeAcaoConexao.setTextColor(Color.GREEN);

                    //Subscribe
                    setSubscribe();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    nomeAcaoConexao.setText("Falha ao conectar");
                    nomeAcaoConexao.setTextColor(Color.RED);

                    //Se Falhar, tenta conectar de novo.
                    //Sozinho
                    Conecta_mqtt();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


}
