package com.example.aztlan.aztlan_iot;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.aztlan.aztlan_iot.dao.DispositivoDAO;
import com.example.aztlan.aztlan_iot.modelo.Dispositivo;

import org.json.JSONException;
import org.json.JSONStringer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelaDeCadastro extends AppCompatActivity{

    private static final int SOLICITA_SSID = 1;
    private DeviceHelper devicehelper;
        private Toolbar toolbar;

        //private String urlserver = ("192.168.15.10");      //String para ser usada no JSON
        private String urlserver = ("aztlan.ddns.com.br");  //String para ser usada no JSON

        private String jsonToserver;

        //Volley Variales
        private RequestQueue mRequestQueue;
        private StringRequest stringRequest;
        private String url = "http://192.168.4.1/wifisave"; // url do ESP

        //Aviso
        AlertDialog.Builder builder;
        //ProgressDialog
        ProgressDialog progressDialog = null;
        //Campo SSID
        //Global para ser usado pela Activity que procura retorna SSID
        private TextView campoWifi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_de_cadastro);

        campoWifi = (TextView) findViewById(R.id.Cadastro_campo_nome_Wifi);
        final TextView campoSenhaWifi = (TextView) findViewById(R.id.Cadastro_campo_senha_Wifi);
        Button btnAdicionarDispositivo = (Button) findViewById(R.id.Cadastro_btn_adicionar_dispositivo);
        final TextView campoNome = (TextView) findViewById(R.id.Cadastro_campo_nome_dispositivo);
        TextView campoAmbiente = (TextView) findViewById(R.id.Cadastro_campo_nome_ambiente);



        toolbar = (Toolbar) findViewById(R.id.TelaDeCadastroToolbar);
        //Seta parametros da toolbar
        //Popula o nome do dispositivo na toolbar
        toolbar.setTitle("Cadastro de Dispositivo");
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

        //Instancia avisos. Alert and Progress Dialogs
        builder = new AlertDialog.Builder(TelaDeCadastro.this);

        //Instancia o deviceHelper
        devicehelper = new DeviceHelper(this);



        btnAdicionarDispositivo.setOnClickListener(new View.OnClickListener() {
            public DispositivoDAO dao;

            @Override
            public void onClick(View v) {


                if (campoNome.getText() == null){
                    Toast.makeText(TelaDeCadastro.this, "Digite um nome válido", Toast.LENGTH_SHORT).show();
                }

                //Pega o nome do dispositivo do DAO
                final Dispositivo dispositivo = devicehelper.pegadispositivo();
                dao = new DispositivoDAO(TelaDeCadastro.this);
                //dao.insere(dispositivo);
                dao.close();


                //Pega os campos digitados
                //Não estão no DAO (sem necessidade)
                final String wifiDigitado = campoWifi.getText().toString() ;
                final String senhaDigitado = campoSenhaWifi.getText().toString() ;


                //Monta um JSON com os dados digitados
                jsonToserver = MontaJson(wifiDigitado,senhaDigitado,dispositivo.getNome().toString());

                //Metodo para request usando Volley API
                //SendVolleyRequest();

                // Manda o JSON criado para o server (pagina de config do ESP)

                // Comunicação aqui !!
                //new ConectaServerTask().execute();


                //*****************************VOLLEY LOGIC*****************************************
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        //Se a progressbar ainda estiver ativa, desabilita ela
                        //Dismiss a progressbar
                        if (progressDialog!= null && progressDialog.isShowing())
                        {
                            progressDialog.dismiss();
                        }

                        //Conexão feita
                        builder.setTitle("Adicionando Dispositivo");
                        //builder.setMessage("Resposta:"+ response);
                        builder.setMessage("Verifique se o Led verde do sensor acendeu. Caso contrário, delete o dispositivo e tente novamente.");
                        builder.setCancelable(false);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            //Listener para o botão da caixa de dialogo
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            //Aqui tem que salvar o aluno

                             dao.insere(dispositivo);
                             dao.close();

                            // Limpa os nomes etc...?
                              finish();

                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // Erro na conexão
                        //Toast.makeText(TelaDeCadastro.this, "Erro!" + error, Toast.LENGTH_SHORT).show();

                        //Dismiss a progressbar
                        if (progressDialog!= null && progressDialog.isShowing())
                        {
                              progressDialog.dismiss();
                        }

                        //Erro no cadastro Conexão
                        //Alert Dialog
                        builder.setTitle("Erro na comunicação");
                        builder.setMessage("Não foi possível encontrar o dispositivo. Verifique se você está conectado à rede do dispositivo e tente novamente.");
                        builder.setIcon(R.mipmap.ic_erro);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        error.printStackTrace();

                    }
                }) {

                    //Add parametros para o POST
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        //Mapa de parametros. Key + Value
                        Map<String,String> params = new HashMap<String, String>();

                        //Valores a serem enviados
                        params.put("s",wifiDigitado);
                        params.put("p",senhaDigitado);
                        params.put("server",urlserver);
                        params.put("port","1883");
                        params.put("user",dispositivo.getNome().toString());
                        params.put("pass",dispositivo.getNome().toString()+"/status");

                        params.put("rele",dispositivo.getNome().toString()+"/retorno");

                        return params;

                    }
                };


                //TextUtils "obriga" o user a não deixar o nome vazio
                if(TextUtils.isEmpty(campoNome.getText().toString())) {
                    campoNome.setError("Campo obrigatório");
                    return;
                }


             //Metodo para jogar tudo na StringRequest ...
             MySingleton.getInstance(TelaDeCadastro.this).addToRequestQueue(stringRequest);

             //Logo após reuniar as informaçãoes para o POST,
             //Inica a tela de dialogo ...
             progressDialog = ProgressDialog.show(TelaDeCadastro.this,"Processando", "Enviando dados ...");



            }
        });


        if (progressDialog!= null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            //Caso o resultado venha da activity que contem esse resultcode ...
            case (SOLICITA_SSID):

                //Verifica se nela, houve RESULT_OK (setada na propria Intent)
                if(resultCode == Activity.RESULT_OK){
                    //Preenche o campo do Wifi com o dado (String) trazido da Intent
                    campoWifi.setText(data.getExtras().getString(ListaWifi.SSID));
                    break;
                }else {/*caso não clicou, não faz nada*/}
        }
    }




     //Volley request funcionou !!!
    // Envio de nome e senha com Volley
    //https://www.youtube.com/watch?v=FL37oah1k8k&index=8&list=PLshdtb5UWjSraOqG1iZW-8mDkJXe3LSL0

    private void SendVolleyRequest() {

        mRequestQueue = Volley.newRequestQueue(this);

        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(TelaDeCadastro.this, "Sucesso", Toast.LENGTH_SHORT).show();

                Toast.makeText(TelaDeCadastro.this, "Json: " + response.toString() , Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(TelaDeCadastro.this, "Erro na resposta", Toast.LENGTH_SHORT).show();


            }
        });

        //Envio
        mRequestQueue.add(stringRequest);

    }

    public String MontaJson(String wifi, String senha , String nome){

        JSONStringer js = new JSONStringer();

        try {

            js.object().key("Dispositivo").array();

            js.object();
            js.key("s").value(wifi);
            js.key("p").value(senha);
            js.key("server").value(urlserver);
            js.key("port").value("1883");
            js.key("user").value(nome);
            js.key("pass").value(" ");
            js.endObject();

            js.endArray().endObject();



        } catch (JSONException e) {
            e.printStackTrace();
        }


        return js.toString();

    }

    private class ConectaServerTask extends AsyncTask<Void,Void,String> {


            @Override
            protected void onPreExecute() {

                Toast.makeText(TelaDeCadastro.this, "Enviando dados ...", Toast.LENGTH_SHORT).show();
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                try {

                    // URl do sever (NodeMCU)
                    //URL url = new URL("http://192.168.4.1/wifisave");

                    URL url = new URL("https://192.168.4.1");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestProperty("User-Agent", "");
                    connection.setRequestMethod("POST");

                    connection.setDoInput(true);

                    String UrlParameters = "s=AZTLAN_2ND&p=aztlan%4000&server=iot.eclipse.org&port=1883&user=sala&pass=mqtt_pass";

                    DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());

                    dStream.writeBytes(UrlParameters);
                    dStream.flush();
                    dStream.close();

                    //int responseCode = connection.getResponseCode();

                    //Avisa o server que o que vai ser enviado será do tipo JSON
                    //connection.setRequestProperty("Content-type","application/json");

                    //Avisa o que aceita como resposta do server
                    //connection.setRequestProperty("Accept","application/json");

                    //Avisa o HttpUrl que será feito um post
                    //connection.setDoInput(true);

                    //Stream de saida. Onde irão os dados
                    //PrintStream outPut = new PrintStream(connection.getOutputStream());
                    //Poe o json (dados) na output Stream
                    //outPut.println(jsonToserver);

                    //Estabelece a conexão com o server
                    connection.connect();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }


            @Override
            protected void onPostExecute(String s) {
                Toast.makeText(TelaDeCadastro.this, "Dados Enviados!", Toast.LENGTH_SHORT).show();



                finish();

                super.onPostExecute(s);
            }


        };

    public void abrePesuisaWifi(View v){

        Intent abrePesquisaWifi = new Intent(TelaDeCadastro.this,ListaWifi.class);
        startActivityForResult(abrePesquisaWifi,SOLICITA_SSID);

    }


}
