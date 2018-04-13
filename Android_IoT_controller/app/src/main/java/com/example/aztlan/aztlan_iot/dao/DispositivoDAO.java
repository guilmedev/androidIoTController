package com.example.aztlan.aztlan_iot.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.aztlan.aztlan_iot.modelo.Dispositivo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aztlan on 03/08/2017.
 */

// Classe usada para auxiliar ações com Banco de dados

public class DispositivoDAO extends SQLiteOpenHelper{

    public DispositivoDAO(Context context) {
        super(context, "Banco", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE Dispositivos (id INTEGER PRIMARY KEY, nome TEXT NOT NULL, valor TEXT);";

        db.execSQL(sql);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "DROP TABLE IF EXISTS Dispositivos";
        db.execSQL(sql);
        onCreate(db);
    }

    public void insere(Dispositivo dispositivo) {

        // Instancia uma referencia para o banco de dados
        SQLiteDatabase db = getWritableDatabase();

        // Guarda o nome do Aluno no banco de dados
        ContentValues dados = new ContentValues();
        dados.put("nome",dispositivo.getNome());
        dados.put("valor",dispositivo.getAmbiente());
        db.insert("Dispositivos",null,dados);

    }

    public List<Dispositivo> buscaDispositivos() {

        String sql = "SELECT * FROM Dispositivos;";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql,null);

        List<Dispositivo> dispositivos= new ArrayList<Dispositivo>();


        // Passa por todos os nomes do banco , cria o Dispositivo e pega os valores do banco
        while (c.moveToNext())
        {
            Dispositivo dispositivo = new Dispositivo();


            dispositivo.setId( c.getLong(c.getColumnIndex("id") ) );
            dispositivo.setNome( c.getString(c.getColumnIndex("nome") ) );
            dispositivo.setAmbiente( c.getString(c.getColumnIndex("valor") ) );

            // Adiciona o dispositvo montado na Array de dispositivos
            dispositivos.add(dispositivo);

        }
        //Fecha o curor que varreu o banco de dados
        c.close();

        // Retorna a lista de alunos montados
        return dispositivos;


    }

    public void deleta(Dispositivo dispositivo) {
        SQLiteDatabase db = getWritableDatabase();

        String[] params = {dispositivo.getId().toString()};
        db.delete("Dispositivos", "id = ?", params);

    }
}

