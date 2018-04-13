package com.example.aztlan.aztlan_iot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aztlan.aztlan_iot.MainActivity;
import com.example.aztlan.aztlan_iot.R;
import com.example.aztlan.aztlan_iot.modelo.Dispositivo;

import java.util.List;

/**
 * Created by Aztlan on 03/08/2017.
 */

public class DispositivosAdapter extends BaseAdapter {
    private final List<Dispositivo> dispositivos;
    private final Context context;


    public DispositivosAdapter(Context context, List<Dispositivo> dispositivos) {
        this.context = context;
        this. dispositivos = dispositivos;
    }

    @Override
    public int getCount() {
        return dispositivos.size();
    }

    @Override
    public Object getItem(int position) {
        return dispositivos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dispositivos.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //Referencia para a lista
        Dispositivo dispositivo = dispositivos.get(position);
        // Infla o XML criado no lugar o list padrão ...
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = convertView;

        ViewHolder mainViewHolder = null;

        if(convertView == null){

            convertView = inflater.inflate(R.layout.list_device, parent, false);

           final ViewHolder viewHolder = new ViewHolder();
           viewHolder.botao = (Button) convertView.findViewById(R.id.List_item_botao);
           viewHolder.ambiente = (TextView) convertView.findViewById(R.id.List_item_ambiente);
           viewHolder.nome = (TextView) convertView.findViewById(R.id.item_nome);

            viewHolder.nome.setText(dispositivo.getNome());
            viewHolder.ambiente.setText(dispositivo.getAmbiente());

            viewHolder.botao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Botao do item" + position, Toast.LENGTH_SHORT).show();
                }
            });

            convertView.setTag(viewHolder);


        }else {
            mainViewHolder = (ViewHolder) convertView.getTag();
            mainViewHolder.nome.setText((Integer) getItem(position));

        }


        //Popula os valores usando a variavel view (referencia ao XML criado no inflate)
//        TextView campoNome = (TextView) view.findViewById(R.id.item_nome);
//        campoNome.setText(dispositivo.getNome());




        return convertView;
    }

    static class ViewHolder{
        Button botao;
        TextView ambiente;
        TextView nome;
    }
}
